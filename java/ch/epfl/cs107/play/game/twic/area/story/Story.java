package ch.epfl.cs107.play.game.twic.area.story;

import java.util.List;

import ch.epfl.cs107.play.game.twic.utils.DisplacementAdvancedIA;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Story extends AreaEntity implements Interactor {

	public Story(Area area) {
		super(area, Orientation.UP, new DiscreteCoordinates(0,0));
	}
	
	/**
     * Simulates a single time step for Story.
     * Note: Need to be Override
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
	public abstract void updateStory(float deltaTime);
	
	/**
	 * @return true if the story is ended.
	 */
	public abstract boolean isEnded();
	
	/**
	 * Allows you to move a character to a given destination
	 * @param entity
	 * @param destination
	 * @param area
	 * @return true if the entity moves
	 */
	protected boolean move(MovableAreaEntity entity, DiscreteCoordinates destination, Area area) {
		return move(entity, destination, area, 8);
	}
	
	/**
	 * Allows you to move a character to a given destination
	 * @param entity
	 * @param destination
	 * @param area
	 * @param frameForMove
	 * @return true if the entity moves
	 */
	protected boolean move(MovableAreaEntity entity, DiscreteCoordinates destination, Area area, int frameForMove) {
		Orientation orientation = DisplacementAdvancedIA.goToTarget(entity, destination, entity.getCurrentCells().get(0), area);
		if(orientation != null)
			entity.moveOrientate(orientation, frameForMove); 
		else if(entity.getCurrentCells().get(0).equals(destination))
			return false; 
		
		return true;
	}

	@Override
	public boolean takeCellSpace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		// TODO Auto-generated method stub
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean wantsCellInteraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean wantsViewInteraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void interactWith(Interactable other) {
		// TODO Auto-generated method stub		
	}
	
}
