package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity.CollectableObject;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class LogMonster extends Monster {
	
	private final LogMonsterHandler handler;

	private float sleepTime;

	private final Animation[] animationsIdle;
	private final Animation animationSleep;
	private final Animation animationWakeUp;
	
	private Etat etat;
	
	private boolean afficher = false;
	private boolean delayUpdate = false;

	/**
	 * Default LogMonster Constructor
	 * @param area (Area), The area he's attached to, not null
	 * @param orientation (Orientation), The orientation given, not null
	 * @param position (DiscreteCoordinates), The coordinates in the area, not null
	 * @param object (CollectableObject), An object given when the logMonster dies, not null
	 */
	public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp,
					  CollectableObject object) {
		super(area, orientation, position, maxHp, object);
		
		super.initDommageType(new DommageType[] {DommageType.PHYSIC, DommageType.FIRE}, DommageType.PHYSIC);
		handler = new LogMonsterHandler();
		
		etat = Etat.IDLE;
		
		// Animations
		Sprite[][] spritesIdle = RPGSprite.extractSprites("zelda/logMonster", 4, 2, 2,
				this, 32, 32, new Vector(-0.5f, 0),
				new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
		animationsIdle = RPGSprite.createAnimations(ANIMATION_DURATION/2, spritesIdle);

		Sprite[] spriteSleep = new Sprite[4];
		for(int i = 0; i < 4; ++i)
			spriteSleep[i] = new RPGSprite("zelda/logMonster.sleeping", 2, 2, this, 
					new RegionOfInterest(0, i*32, 32, 32), new Vector(-0.5f, 0));
		animationSleep = new Animation(4, spriteSleep);

		Sprite[] spriteWakeUp = new Sprite[3];
		for(int i = 0; i < 3; ++i)
			spriteWakeUp[i] = new RPGSprite("zelda/logMonster.wakingUp", 2, 2, this, 
					new RegionOfInterest(0, i*32, 32, 32), new Vector(-0.5f, 0));
		animationWakeUp = new Animation(4, spriteWakeUp, false);
		
	}
	
	@Override
	public boolean wantsViewInteraction() {
		return (etat == Etat.ATTACK || etat == Etat.IDLE);
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		if(etat == Etat.ATTACK) return Collections.singletonList (
				getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
		else if(etat == Etat.IDLE) {
			List<DiscreteCoordinates> collection = new ArrayList<>();
			for(int i = 1; i < 9; ++i) {
				collection.add(getCurrentMainCellCoordinates().jump(getOrientation().toVector().resized(i)));
			}
			return collection;
		} 
		return null;
	}
	
	/**
	 * Get the correct animation depending on the status
	 * @param etat (Etat), The status, not null
	 * @return (Animation), The animation
	 */
	private Animation getAnimation(Etat etat) {
		switch(etat) {
		case IDLE : ANIMATION_DURATION = 10;
		    animationsIdle[getOrientation().ordinal()].setSpeedFactor(1);
			return animationsIdle[getOrientation().ordinal()];
		case ATTACK : ANIMATION_DURATION = 4;
		    animationsIdle[getOrientation().ordinal()].setSpeedFactor(2);
			return animationsIdle[getOrientation().ordinal()];
		case TO_SLEEP :
		case SLEEP : return animationSleep;
		case WAKE_UP : return animationWakeUp;
		default : return getAnimation(Etat.IDLE);
		}
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
		getAnimation(etat).draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		if(!isLiving()) {
			super.update(deltaTime);
			return;
		}
		
		if(isDisplacementOccurs() || (etat != Etat.IDLE && etat != Etat.ATTACK)) 
			getAnimation(etat).update(deltaTime);
		else getAnimation(etat).reset();

		float MIN_SLEEPING_DURATION = 3.f;
		float MAX_SLEEPING_DURATION = 8.f;
		switch(etat) {
		case IDLE: displacementIA(deltaTime);
			break;
		case ATTACK: move(ANIMATION_DURATION);
			if(delayUpdate) 
				delayUpdate = false;
			else if(!isDisplacementOccurs()) 
				etat = Etat.TO_SLEEP;
			break;
		case TO_SLEEP: sleepTime = MIN_SLEEPING_DURATION + (MAX_SLEEPING_DURATION - MIN_SLEEPING_DURATION) 
				* RandomGenerator.getInstance().nextFloat();
		    etat = Etat.SLEEP;
			break;
		case SLEEP: sleepTime -= deltaTime;
		    if(sleepTime <= 0) etat = Etat.WAKE_UP;
			break;
		case WAKE_UP: if(getAnimation(etat).isCompleted()) etat = Etat.IDLE;
			break;
		default:
			break;
		}
		
		super.update(deltaTime);
	}
    
    @Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
    /**
     * Handle all the interactions with the others actors
     */
	private class LogMonsterHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer TWICPlayer) {
			if(etat == Etat.ATTACK)
				TWICPlayer.hurted(2f, getAttackType());
			else if(etat == Etat.IDLE) {
				etat = Etat.ATTACK;
				delayUpdate = true;
			}
		}
		
		public void interactWith(Personnage personnage) {
			personnage.hurted(2f, getAttackType());
		}
		
		public void interactWith(Shop shop) {
			shop.hurted();
		}
		
	}
	
	private enum Etat {
		IDLE,
		ATTACK,
		TO_SLEEP,
		SLEEP,
		WAKE_UP
	}
	
}
