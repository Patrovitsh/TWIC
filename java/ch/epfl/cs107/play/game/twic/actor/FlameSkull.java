package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class FlameSkull extends Monster implements FlyableEntity {
	
	private final FlameSkullHandler handler;
	private final Animation[] animations;

	private float lifeTime;
	private boolean afficher = false;
	private float deltaTimeCpt;

	/**
	 * Default FlameSkull Constructor
	 * @param area (Area), Not null
	 * @param orientation (Orientation), Not null
	 * @param position (DiscreteCoordinates), Not null
	 * @param maxHp (float), The max HP, not null
	 */
	public FlameSkull(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp) {
		super(area, orientation, position, maxHp, null);
		// TODO Auto-generated constructor stub
		
		handler = new FlameSkullHandler();
		super.initDommageType(new DommageType[] {DommageType.PHYSIC, DommageType.MAGIC}, DommageType.FIRE);

		Sprite[][] sprites = RPGSprite.extractSprites("zelda/flameSkull", 3, 2, 2,
				this, 32, 32, new Vector(-0.5f, 0),
				new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
		animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);

		float MAX_LIFE_TIME = 30.f;
		float MIN_LIFE_TIME = 25.f;
		lifeTime = MIN_LIFE_TIME + (MAX_LIFE_TIME - MIN_LIFE_TIME)*RandomGenerator.getInstance().nextFloat();
	}
	
	@Override
	public boolean takeCellSpace() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}
	
	@Override
	public boolean wantsCellInteraction() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(!isLiving()) {
			super.draw(canvas);
			return;
		}
		
		if(isInvincible()) {
			afficher = !afficher;
			if(afficher) return;
		}
		animations[getOrientation().ordinal()].draw(canvas);
	}
	
	@Override
	public void interactWith(Interactable other) {
		// TODO Auto-generated method stub	
		other.acceptInteraction(handler);
	}
	
	@Override
	public void update(float deltaTime) {
		if(isLiving()) {
			animations[getOrientation().ordinal()].update(deltaTime);
			displacement(0.9f, 6);
			if(!isDisplacementOccurs()) {
				deltaTimeCpt += deltaTime;
				if(deltaTimeCpt > 2*deltaTime) {
					displacement(0f, 6);
					deltaTimeCpt = 0.f;
				}
			}
		}
		
		lifeTime -= deltaTime;
		if(lifeTime <= 0) setDeath();
		
		super.update(deltaTime);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class FlameSkullHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer TWICPlayer) {
			TWICPlayer.hurted(1.f, getAttackType());
		}
		
		public void interactWith(Grass grass) {
			grass.cutGrass();
		}
		
		public void interactWith(Bomb bomb) {
			bomb.toExplode();
		}
		
		public void interactWith(Monster monster) {
			monster.hurted(1.f, getAttackType());
		}
		
		public void interactWith(Personnage personnage) {
			personnage.hurted(1.f, getAttackType());
		}
		
		public void interactWith(Shop shop) {
			shop.hurted();
		}
		
	}

}
