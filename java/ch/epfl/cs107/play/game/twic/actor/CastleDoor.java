package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class CastleDoor extends Door {
	
	private Sprite sprite;
	private float cptDeTemps = 0.f;
	private boolean doorHasJustClosed = true;
	
	/**
	 * Default CastleCoor Constructor
	 * @param destination (String), Name of the destination area, not null
	 * @param otherSideCoordinates (DiscreteCoordinates), The coordinates when you're changinf of area
	 * @param signal (Logic), Not null
	 * @param area (Area), The current area, not null
	 * @param orientation (Orientation), The door's orientation, not null
	 * @param position (DiscreteCoordinates), The door's position, not null
	 * @param otherCells (DiscreteCoordinates), Others door's position
	 */
	public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Logic signal, 
			Area area, Orientation orientation, DiscreteCoordinates position, DiscreteCoordinates... otherCells) {
		super(destination, otherSideCoordinates, signal, area, orientation, position, otherCells);
		sprite = new Sprite("zelda/castleDoor.close", 2f, 2f, this, new RegionOfInterest(0, 0, 32, 32));
		sprite.setDepth(-100000.f);
	}
	
	@Override
    public boolean takeCellSpace() {
        return !isOpen();
    }

    @Override
    public boolean isCellInteractable() {
        return isOpen();
    }

    @Override
    public boolean isViewInteractable(){
        return !isOpen();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((TWICInteractionVisitor)v).interactWith(this);
    }
    
    /**
     * Set the signal, if the door is locked or not
     */
    public void setSignal(Logic signal) {
    	if(signal.equals(Logic.TRUE)) {
    		sprite = new Sprite("zelda/castleDoor.open", 2f, 2f, this, new RegionOfInterest(0, 0, 32, 32));
    		sprite.setDepth(-100000.f);
    	} else if(signal.equals(Logic.FALSE))
    		doorHasJustClosed = true;
    	super.setSignal(signal);
    }
    
    public void draw(Canvas canvas) {
    	sprite.draw(canvas);
    }
    
    @Override
    public void update(float deltaTime) {
    	if(!isOpen() && doorHasJustClosed) {
    		cptDeTemps += deltaTime;
    		if(cptDeTemps > 0.25f) {
    			sprite = new Sprite("zelda/castleDoor.close", 2f, 2f, this, new RegionOfInterest(0, 0, 32, 32));
        		sprite.setDepth(-100000.f);
        		cptDeTemps = 0;
        		doorHasJustClosed = false;
    		}
    	}
    }

}
