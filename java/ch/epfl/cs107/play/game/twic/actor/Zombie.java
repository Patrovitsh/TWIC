package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.twic.utils.DisplacementAdvancedIA;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Zombie extends Monster {
	
	private final ZombieHandler handler;
	
	private final Sprite spriteRip;
	private final Animation[] animations;
	private final Animation[] animationsNoMvt;
	
	private State state;
	private DiscreteCoordinates targetCoords;
	private AreaEntity target;
	
	private boolean afficher = false;
	private int timerState = 0;
	private int timerAttack = 0;
	private int timerBorn;

	public Zombie(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp, int timeToBorn) {
		super(area, orientation, position, maxHp, null);
		super.initDommageType(new DommageType[] {DommageType.PHYSIC}, DommageType.PHYSIC);
		handler = new ZombieHandler();
		
		state = State.BORN;
		timerBorn = 48 + RandomGenerator.getInstance().nextInt(timeToBorn);
		
		// Animations
		spriteRip = new Sprite("personalAdds/croix", 1, 1, this, new RegionOfInterest(0, 0, 32, 32), Vector.ZERO, 1, -100);

		Sprite[][] sprites = RPGSprite.extractSprites("personalAdds/zombie", 4, 1, 2, this, 16, 32,
				new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
		animations = RPGSprite.createAnimations(4, sprites);

		Sprite[][] spritesNoMvt = RPGSprite.extractSprites("personalAdds/zombieNoMvt", 3, 1, 2, this, 16, 32,
				new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
		animationsNoMvt = RPGSprite.createAnimations(4, spritesNoMvt);
		
	}
	
	public Zombie(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp) {
		this(area, orientation, position, maxHp, 1);
		this.timerBorn = 0;
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
		
		if(state == State.BORN) {
			spriteRip.draw(canvas);
			return;
		}
		
		if(isDisplacementOccurs())
			animations[getOrientation().ordinal()].draw(canvas);
		else
			animationsNoMvt[getOrientation().ordinal()].draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		if(!isLiving()) {
			super.update(deltaTime);
			return;
		}
		
		if(timerBorn > 0)
			state = State.BORN;
		
		if(isDisplacementOccurs())
			animations[getOrientation().ordinal()].update(deltaTime);
		else
			animationsNoMvt[getOrientation().ordinal()].update(deltaTime);
		
		switch(state) {
		case BORN : --timerBorn;
		    if(timerBorn <= 0) 
		    	state = State.IDLE;
			break;
		case IDLE: displacementIA(deltaTime);
			if(isTimeSupThanMax(200)) state = State.RESEARCH;
		    break;
		case RESEARCH: displacementIA(deltaTime);
			if(isTimeSupThanMax(20)) state = State.IDLE;
			break;
		case HUNTING: Orientation orientation;
			orientation = DisplacementAdvancedIA.goToTarget(this, targetCoords, getCurrentMainCellCoordinates(),
					getOwnerArea());
			if(orientation != null)
				moveOrientate(orientation, 8);
			if(isTimeSupThanMax(25)) {
				state = State.IDLE;
				targetCoords = null;
			} 
			break;
		}
		
		super.update(deltaTime);
	}
	
	/**
	 * @return true if the time is greater than max
	 */
	private boolean isTimeSupThanMax(int max) {
		++timerState;
		if(timerState > max) {
			timerState = 0;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean wantsViewInteraction() {
		return isLiving() && state != State.BORN;
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		switch(state) {
		case RESEARCH: return newListDiscreteCoordinates(8);
		case HUNTING : return newListDiscreteCoordinates(6); 
		default : return newListDiscreteCoordinates(4);
		}
	}
	
	
	private List<DiscreteCoordinates> newListDiscreteCoordinates(int rayon) {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = -rayon; i <= rayon; ++i) {
			for(int j = -rayon; j <= rayon; ++j) {
				collection.add(getCurrentMainCellCoordinates().jump(i, j));
			}
		}
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * @param entity : the target
	 * @return true if the entity is on the same scell as the dragon
	 */
	private boolean interactionIA(AreaEntity entity) {
		DiscreteCoordinates entityCoords = entity.getCurrentCells().get(0);
		DiscreteCoordinates coords = getCurrentMainCellCoordinates();
		if(targetCoords == null) {
			targetCoords = entity.getCurrentCells().get(0);
			target = entity;
			
		}
		
		// Mise a jour de la cible si une cible est plus proche
		if(DiscreteCoordinates.distanceBetween(coords, entityCoords) 
				< DiscreteCoordinates.distanceBetween(coords, targetCoords)) {
			targetCoords = entityCoords;
			target = entity;
		}
		// ------------
		
		if(entityCoords.equals(coords.jump(getOrientation().toVector()))) {
			return true;
		}
		
		// Mise a jour des coordonnees de la cible
		if(!entity.equals(target)) return false;
		targetCoords = entityCoords;
		// ------------
		
		return false;
	}
	
	/**
	 * add a new zombie in coordinates coords
	 */
	private void addZombie(DiscreteCoordinates coords, Orientation orientation) {
		Area area = getOwnerArea();
		DiscreteCoordinates newCoords;
		List<DiscreteCoordinates> collection;
		
		for(int x = coords.x -1; x <= coords.x+1; ++x) {
			for(int y = coords.y -1; y <= coords.y+1; ++y) {
				
				newCoords = new DiscreteCoordinates(x, y);
				collection = Collections.singletonList(newCoords);
				
				if(area.canEnterAreaCells(this, collection)) {
					area.registerActor(new Zombie(area, orientation, newCoords, getMaxHp(), 100));
					return;
				}
			}
		}
		
	}
	
	/**
	 * increase the time before it attacks by 1
	 * @return true if the time is greater than max
	 */
	private boolean timerAttackUpdate(int max) {
		++timerAttack;
		if(timerAttack > max) {
			timerAttack = 0;
			return true;
		}
		return false;
	}
	
	private class ZombieHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer TWICPlayer) {
			state = State.HUNTING;
			if(interactionIA(TWICPlayer)) {
				if(timerAttackUpdate(18))
					TWICPlayer.hurted(1.f, getAttackType());
				timerState = 0;
			}
		}
		
		public void interactWith(Personnage personnage) {
			state = State.HUNTING;
			if(interactionIA(personnage)) {
				if(timerAttackUpdate(12))
					personnage.hurted(2.f, getAttackType());
				if(!personnage.isLiving())
					addZombie(personnage.getCurrentCells().get(0), personnage.getOrientation());
				timerState = 0;
			}
		}
		
		public void interactWith(Shop shop) {
			if(!shop.isLiving()) return;
				
			state = State.HUNTING;
			if(interactionIA(shop)) {
				if(timerAttackUpdate(12))
					shop.hurted();
				if(!shop.isLiving())
					addZombie(shop.getCurrentCells().get(0), shop.getOrientation());
				timerState = 0;
			}
		}
		
	}
	
	private enum State {
		BORN,
		IDLE,
		RESEARCH,
		HUNTING
	}

}

