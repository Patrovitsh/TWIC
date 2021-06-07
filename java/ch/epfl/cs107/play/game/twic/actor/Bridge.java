package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Bridge extends AreaEntity  {
	
	private final Sprite sprite;
	private boolean open = false;
	
	/**
	 * Default Bridge Constructor
	 * @param area (Area), Not null
	 * @param orientation (Orientation), Not null
	 * @param position (DiscreteCoordinates), Not null
	 */
	public Bridge(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		sprite = new Sprite("zelda/bridge", 4, 3, this, new RegionOfInterest(0, 0, 64, 48));
		sprite.setDepth(-10000);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		collection.add(getCurrentMainCellCoordinates().jump(1, 1));
		collection.add(getCurrentMainCellCoordinates().jump(2, 1));
		return collection;
	}

	@Override
	public boolean takeCellSpace() {
		return !open;
	}

	@Override
	public boolean isCellInteractable() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return !open;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		if(open) 
			sprite.draw(canvas);
	}
	
	/**
	 * Makes the bridge visible
	 */
	public void open() {
		open = true;
	}

}
