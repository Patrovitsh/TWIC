package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Orb extends AreaEntity implements Interactor {
	
	private final OrbHandler handler;
	private final Animation animationBefore;
	private final Animation animationAfter;
	
	private boolean activated = false;

	/**
	 * Default Orb Constructor
	 * @param area (Area), The area he's attached to, not null
	 * @param orientation (Orientation), The orientation given, not null
	 * @param position (DiscreteCoordinates), The coordinates in the area, not null
	 */
	public Orb(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		handler = new OrbHandler();

		Sprite[] spriteBefore = new Sprite[6];
		for(int i = 0; i < spriteBefore.length; ++i)
			spriteBefore[i] = new RPGSprite("zelda/orb", 1, 1, this,
					new RegionOfInterest(i*32, 0, 32, 32),
					new Vector(0, 0.10f), 1, 10);
		
		animationBefore = new Animation(4, spriteBefore);

		Sprite[] spriteAfter = new Sprite[6];
		for(int i = 0; i < spriteAfter.length; ++i)
			spriteAfter[i] = new RPGSprite("zelda/orb", 1, 1, this,
					new RegionOfInterest(i*32, 64, 32, 32),
					new Vector(0, 0.10f), 1, 10);
		
		animationAfter = new Animation(4, spriteAfter);
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
		return !activated;
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
		if(!activated) 
			animationBefore.draw(canvas);
		else 
			animationAfter.draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		if(!activated) 
			animationBefore.update(deltaTime);
		else 
			animationAfter.update(deltaTime);
	}
	
	public void activation() {
		activated = true;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = -4; i <= 4; ++i) {
			for(int j = -4; j <= 4; ++j) {
				collection.add(getCurrentMainCellCoordinates().jump(i, j));
			}
		}
		return collection;
	}

	@Override
	public boolean wantsCellInteraction() {
		return false;
	}

	@Override
	public boolean wantsViewInteraction() {
		return activated;
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private static class OrbHandler implements TWICInteractionVisitor {
		public void interactWith(Bridge bridge) {
			bridge.open();
		}
	}

}
