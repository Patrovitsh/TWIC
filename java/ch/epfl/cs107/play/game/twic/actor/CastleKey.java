package ch.epfl.cs107.play.game.twic.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class CastleKey extends AreaEntity {
	
	private final TWICItem castleKey;
	private final Sprite sprite;
	private boolean isCollect = false;

	/**
	 * Default CastleKey Constructor
	 * @param area (Area), Not null
	 * @param orientation (Orientation), Not null
	 * @param position (DiscreteCoordinates), Not null
	 */
	public CastleKey(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		castleKey = TWICItem.CASTLE_KEY;
		sprite = castleKey.getSprite();
		sprite.setDepth(-999);
		sprite.setParent(this);
	}
	
	/**
	 * @return true if the key is collect
	 */
	public boolean isCollect() {
		return isCollect;
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	
	/**
	 * Collect the castle key
	 * @return (ARPGItem) The key
	 */
	public TWICItem collect() {
		isCollect = true;
		getOwnerArea().unregisterActor(this);
		return castleKey;
	}

}