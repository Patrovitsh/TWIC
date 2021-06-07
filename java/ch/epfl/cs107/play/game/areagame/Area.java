package ch.epfl.cs107.play.game.areagame;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.epfl.cs107.play.game.Playable;
import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Mouse;
import ch.epfl.cs107.play.window.Window;


/**
 * Area is a "Part" of the AreaGame. An Area is made of a Behavior, and a List of Actors
 */
public abstract class Area implements Playable {

    // Context objects
    private Window window;
    //private FileSystem fileSystem;
    // Camera Parameter
    private Actor viewCandidate;
    private Vector viewCenter;
    /// List of Actors inside the area
    private List<Actor> actors;
    /// List of Actors we want to register/unregistered from the area for next update iteration
    private List<Actor> registeredActors;
    private List<Actor> unregisteredActors;
    /// Sublist of actor (interactors) inside the area
    private List<Interactor> interactors;
    private Map<Interactable, List<DiscreteCoordinates>> interactablesToEnter;
    private Map<Interactable, List<DiscreteCoordinates>> interactablesToLeave;
    /// The behavior Map
    private AreaBehavior areaBehavior;
    /// pause mechanics and menu to display. May be null
    /// - start indicate if area already begins, paused indicate if we display the pause menu
    private boolean started, paused;



    /** @return (float): camera scale factor, assume it is the same in x and y direction */
    public abstract float getCameraScaleFactor();

    /**
     * Setter for the Behavior of this Area
     * Please call this method in the begin method of every subclass
     * @param ab (AreaBehavior), not null
     */
    protected final void setBehavior(AreaBehavior ab){
        this.areaBehavior = ab;
    }

    /**
     * Setter for the view Candidate
     * @param a (Actor), not null
     */
    public final void setViewCandidate(Actor a){ 
        this.viewCandidate = a;
    }
    
    
    /**
     * Add an actor to the actors list
     * and to the behavior area cell if the actor is an Interactable
     * and to the interactor list if the actor is an Interactor
     * @param a (Actor)(Interactor?)(Interactable?): the actor to add, not null
     * @param safeMode (Boolean): if True, the method ends
     */
    private void addActor(Actor a, boolean safeMode) {

        boolean errorHappen = false;

        if(a instanceof Interactor) 
        	errorHappen = !interactors.add((Interactor) a);
        
        if(a instanceof Interactable) 
        	errorHappen = errorHappen || !enterAreaCells(((Interactable) a), ((Interactable) a).getCurrentCells());
        errorHappen = errorHappen || !actors.add(a);

        if(errorHappen && !safeMode) {
            System.out.println("Actor " + a + " cannot be completely added, so remove it from where it was");
            // Call it in safe mode to avoid recursive calls
            removeActor(a, true);
        }
    }

    /**
     * Remove an actor form the actor list
     * and from the behavior area cell if the actor is an Interactable
     * and from the interactor list if the actor is an Interactor
     * @param a (Actor): the actor to remove, not null
     * @param safeMode (Boolean): if True, the method ends
     */
    private void removeActor(Actor a, boolean safeMode){
        boolean errorHappen = false;

        if(a instanceof Interactor)
            errorHappen = !interactors.remove((Interactor)a);
        if(a instanceof Interactable)
            errorHappen = errorHappen || !leaveAreaCells(((Interactable) a), ((Interactable) a).getCurrentCells());
        errorHappen = errorHappen || !actors.remove(a);

        if(errorHappen && !safeMode) {
            System.out.println("Actor " + a + " cannot be completely removed, so add it from where it was");
            // Call it in safe mode to avoid recursive calls
            addActor(a, true);
        }
    }

    /**
     * Register an actor : will be added at next update
     * @param a (Actor): the actor to register, not null
     * @return (boolean): true if the actor is correctly registered
     */
    public final boolean registerActor(Actor a){
        //  finer Area strategies can be implemented here if wanted
        return registeredActors.add(a);
    }

    /**
     * Unregister an actor : will be removed at next update
     * @param a (Actor): the actor to unregister, not null
     * @return (boolean): true if the actor is correctly unregistered
     */
    public final boolean unregisterActor(Actor a){
        // finer Area strategies can be implemented here if wanted
        return unregisteredActors.add(a);
    }

    /**
     * Indicate if the given actor exists into the actor list
     * @param a (Actor): the given actor, may be null
     * @return (boolean): true if the given actor exists into actor list
     */
    public boolean exists(Actor a){
        return actors.contains(a);
    }


    /**
     * Getter for the area width
     * @return (int) : the width in number of cols
     */
    public int getWidth(){ 
        return areaBehavior.getWidth();
    }

    /**
     * Getter for the area height
     * @return (int) : the height in number of rows
     */
    public int getHeight(){ 
        return areaBehavior.getHeight();
    }

    /** @return the Window Keyboard for inputs */
    public final Keyboard getKeyboard () {
        return window.getKeyboard();
    }
    
    /** @return the Window Mouse for inputs */
    public final Mouse getMouse() {
    	return window.getMouse();
    }

    /** @return the mouse position relatively to the area and the cells */
    public Vector getRelativeMousePosition() {
    	return getMouse().getPosition();
    			/*.max(new Vector(0,0))
				.min(new Vector(getWidth(),getHeight()));*/
    }
    
    /** @return the mouse coordinates relatively to the area and the cells */
    public DiscreteCoordinates getRelativeMouseCoordinates() {
    	Vector mousePosition = getRelativeMousePosition();
    	DiscreteCoordinates mouseCoordinate = new DiscreteCoordinates((int)Math.floor(mousePosition.x), (int)Math.floor(mousePosition.y));
    	return mouseCoordinate;
    }
    
    /** @return (boolean): true if the method begin already called once. You can use resume() instead*/
    public final boolean isStarted() {
        return started;
    }

    /**
     * If possible make the given interactable entity leave the given area cells
     * @param entity (Interactable), not null
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to leave
     */
    public final boolean leaveAreaCells(Interactable entity, List<DiscreteCoordinates> coordinates) {
        // Finer Area strategies can be implemented here if wanted
        // Until now, the entity is put in a map waiting the update end to avoid concurrent exception during interaction
        if(areaBehavior.canLeave(entity, coordinates)){
            interactablesToLeave.put(entity, coordinates);
            return true;
        }
        return false;
    }

    /**
     * If possible make the given interactable entity enter the given area cells
     * @param entity (Interactable), not null
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to enter
     */
    public final boolean enterAreaCells(Interactable entity, List<DiscreteCoordinates> coordinates) {
        // Finer Area strategies can be implemented here if wanted
        // Until now, the entity is put in a map waiting the update end to avoid concurrent exception during interaction
        if(areaBehavior.canEnter(entity, coordinates)){
            interactablesToEnter.put(entity, coordinates);
            return true;
        }
        return false;
    }
    
    /**
     * Inform if the entity can enter the area cells
     * @param entity (Interactable), not null
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to enter
     */
    public final boolean canEnterAreaCells(Interactable entity, List<DiscreteCoordinates> coordinates) {
        return areaBehavior.canEnter(entity, coordinates);
    }


    /// Area implements Playable

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        this.window = window;
        //this.fileSystem = fileSystem;
        actors = new LinkedList<>();
        interactors = new LinkedList<>();
        registeredActors = new LinkedList<>();
        unregisteredActors = new LinkedList<>();
        interactablesToEnter = new HashMap<>();
        interactablesToLeave = new HashMap<>();
        viewCenter = Vector.ZERO;
        paused = false;
        started = true;
        return true;
    }

    /**
     * Resume method: Can be overridden
     * @param window (Window): display context, not null
     * @param fileSystem (FileSystem): given file system, not null
     * @return (boolean) : if the resume succeed, true by default
     */
    public boolean resume(Window window, FileSystem fileSystem){
    	this.paused = false;
        return true;
    }

    @Override
    public void update(float deltaTime) {    	
    	purgeRegistration();

    	// Render actors
    	if(!paused) {
    		for (Actor actor : actors)
        		actor.update(deltaTime);
    	}

    	// Realize interaction between interactors and their cells contents
    	for (Interactor interactor : interactors) {
    		if (interactor.wantsCellInteraction()) {
    			areaBehavior.cellInteractionOf(interactor);
    		}
    		if (interactor.wantsViewInteraction()) {
    			areaBehavior.viewInteractionOf(interactor);
    		}
    	}

    	// Update camera location
    	updateCamera();

    	// Draw actors
    	for (Actor actor : actors) {
    		actor.bip(window);
    		actor.draw(window);
    	}

    }

    final void purgeRegistration() {
        // PART 1
        // - Register actors
        for (Actor actor : registeredActors) {
            addActor(actor, false);
        }
        registeredActors.clear();

        // - unregister actors
        for (Actor actor : unregisteredActors) {
            removeActor(actor, false);
        }
        unregisteredActors.clear();

        // PART 2
        // - leave old cells
        for (Map.Entry<Interactable, List<DiscreteCoordinates>> entry : interactablesToLeave.entrySet()){
            areaBehavior.leave(entry.getKey(), entry.getValue());
        }
        interactablesToLeave.clear();
        // - enter new cells
        for (Map.Entry<Interactable, List<DiscreteCoordinates>> entry : interactablesToEnter.entrySet()){
            areaBehavior.enter(entry.getKey(), entry.getValue());
        }
        interactablesToEnter.clear();
    }


    private void updateCamera () {

        // Permet de ne plus voir les bords noirs lorsque le player et proche des bords de l'aire.
    	
        Vector candidatPosition = viewCandidate.getPosition();
        float X = getCameraScaleFactor()/2f;
        float Y = getCameraScaleFactor()/2f;
        
        if (candidatPosition.getX() > getWidth() - X) {
        	if(candidatPosition.getY() > getHeight() - Y) {
        		viewCenter = new Vector(getWidth() - X, getHeight() - Y);
        	} else if(candidatPosition.getY() < Y) {
        		viewCenter = new Vector(getWidth() - X, Y);
        	} else {
        		viewCenter = new Vector(getWidth() - X, candidatPosition.getY());
        	}
        } else if(candidatPosition.getX() < X) {
        	if(candidatPosition.getY() > getHeight() - Y) {
        		viewCenter = new Vector(X, getHeight() - Y);
        	} else if(candidatPosition.getY() < Y) {
        		viewCenter = new Vector(X, Y);
        	} else {
        		viewCenter = new Vector(X, candidatPosition.getY());
        	}
        } else {
        	if(candidatPosition.getY() > getHeight() - Y) {
        		viewCenter = new Vector(candidatPosition.getX(), getHeight() - Y);
        	} else if (candidatPosition.getY() < Y) {
        		viewCenter = new Vector(candidatPosition.getX(), Y);
        	} else {
        		viewCenter = viewCandidate.getPosition();
        	}
        }
        
        if(getWidth() < getCameraScaleFactor() || getHeight() < getCameraScaleFactor())
        	viewCenter = new Vector(getWidth()/(float)2,getHeight()/(float)2);
        
        // Compute new viewport
        Transform viewTransform = Transform.I.scaled(getCameraScaleFactor()).translated(viewCenter);
        window.setRelativeTransform(viewTransform);
    }
    
    /**
     * Inform if the entity can spawn the area cells
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to spawn
     */
    public boolean canSpawnAreaCell(List<DiscreteCoordinates> coordinates) {
    	return areaBehavior.canSpawn(coordinates);
    }

    /**
     * Suspend method: Can be overridden, called before resume other
     */
    public void suspend() {
        // Does nothing by default
    	this.paused = true;
    }
    
    /**
     * Remet le jeu en route.
     */
    public void resume() {
    	this.paused = false;
    }


    @Override
    public void end() {
        // by default does nothing
    	// can save the Area state somewhere if wanted
    }

}
