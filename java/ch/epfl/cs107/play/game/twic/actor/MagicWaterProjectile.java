package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class MagicWaterProjectile extends Shots {
	
	private final MagicWaterProjectileHandler handler;
	private final Animation animation;

	/**
	 * Default MagicWaterProjectile Constructor
	 * @param area (Area), The area he's attached to, not null
	 * @param orientation (Orientation), The orientation given, not null
	 * @param position (DiscreteCoordinates), The coordinates in the area, not null
	 * @param distanceMax (int), The maximal distance to reach
	 * @param vitesse (int), The projectile's speed
	 */
	public MagicWaterProjectile(Area area, Orientation orientation, DiscreteCoordinates position, 
			int distanceMax, int vitesse) {
		super(area, orientation, position, distanceMax, vitesse);
		
		handler = new MagicWaterProjectileHandler();
		initDommageType(Monster.DommageType.MAGIC);
		
		Vector anchor = orientation.opposite().toVector().resized(0.5f);

		Sprite[] sprite = new Sprite[4];
		for(int i = 0; i < sprite.length; ++i)
			sprite[i] = new RPGSprite("zelda/magicWaterProjectile", 1, 1, this,
					new RegionOfInterest(i*32, 0, 32, 32), anchor, 1, -1);
		animation = new Animation(4, sprite);
	}
	
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
		super.update(deltaTime);
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class MagicWaterProjectileHandler implements TWICInteractionVisitor {
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

