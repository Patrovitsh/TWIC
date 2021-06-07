package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Arrow extends Shots {
	
	private final ArrowHandler handler;
	private final Sprite[] sprite = new Sprite[4];

	/**
	 * Default Arrow Constructor
	 * @param area (Area), Not null
	 * @param orientation (Orientation), Not null
	 * @param position (DiscreteCoordinates), Not null
	 * @param distanceMax (int), The max distance to reach
	 * @param vitesse (int), The arrow's speed
	 */
	public Arrow(Area area, Orientation orientation, DiscreteCoordinates position, int distanceMax, int vitesse) {
		super(area, orientation, position, distanceMax, vitesse);
		
		handler = new ArrowHandler();
		initDommageType(Monster.DommageType.PHYSIC);
		
		Vector anchor = orientation.opposite().toVector().resized(0.5f);
		
		for(int i = 0; i < sprite.length; ++i) 
			sprite[i] = new Sprite("zelda/arrow", 1, 1, this, new RegionOfInterest(i*32, 0, 32, 32), anchor, 1, -1);
	}
	
	@Override
	public void draw(Canvas canvas) {
		sprite[getOrientation().ordinal()].draw(canvas);
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Handle all the interactions with some actors
	 */
	private class ArrowHandler implements TWICInteractionVisitor {
		public void interactWith(Monster monster) {
			if(monster.hurted(2.f, getAttackType())) 
				stop();
		}
		
		public void interactWith(TWICPlayer TWICPlayer) {
			if(TWICPlayer.hurted(2.f, getAttackType()))
				stop();
		}
		
		public void interactWith(FireSpell fireSpell) {
			fireSpell.dispawn(getAttackType());
		}
		
		public void interactWith(Bomb bomb) {
			bomb.toExplode();
			stop();
		}
		
		public void interactWith(Grass grass) {
			grass.cutGrass();
		}
		
		public void interactWith(Orb orb) {
			orb.activation();
			stop();
		}
		
		public void interactWith(Personnage personnage) {
			if(personnage.hurted(2.f, getAttackType()))
				stop();
		}
		
		public void interactWith(StoryPersonnage harpy) {
			harpy.hurted();
		}
		
		public void interactWith(Shop shop) {
			shop.hurted();
		}
		
	}

}
