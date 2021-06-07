package ch.epfl.cs107.play.game.twic.area;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.twic.TWIC;
import ch.epfl.cs107.play.game.twic.TWICBehavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

public abstract class TWICArea extends Area {

    private int timer = getTimeSpawn()+1;

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();
    
    /**
     * @return (Actor[]) the actors in the area
     */
    protected abstract Actor[] getActors();
    
    /**
     * makes the actor spawn the index finger i
     * @param i : actor table index
     */
    protected abstract void placeActors(int i);
    
    /**
     * @return (int) time difference between two spawn
     */
    protected abstract int getTimeSpawn();
    
    /**
     * @return (Story) the story corresponding to the area
     */
    protected abstract Story getStory();

    
    // EnigmeArea extends Area

    @Override
    public final float getCameraScaleFactor() {
        return TWIC.CAMERA_SCALE_FACTOR;
    }

    // Demo2Area implements Playable

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            TWICBehavior behavior = new TWICBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }
    
    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	if(getStory() != null && !getStory().isEnded())
    		getStory().updateStory(deltaTime);
    	
    	if(getActors() == null) return;
		
		List<Integer> index = new ArrayList<>();
		for(int i = 0; i < getActors().length; ++i) 
			if(!exists(getActors()[i])) 
				index.add(i);
		
		if(index.size() == 0) return;
		
		++timer;
		if (timer < getTimeSpawn()) return;
		
		timer = 0;
		
		for(Integer i : index) {
			placeActors(i);
		}
    }
    
}
