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
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class DarkLord extends Monster {
	
	private final int MIN_SPELL_WAIT_DURATION = 80;
	private final int MAX_SPELL_WAIT_DURATION = 120;

	private final DarkLordHandler handler;

	private final Animation[] animationsPassive;
	private final Animation[] animationsActive;
	
	private Etat etat;
	private final FireSpell fireSpell;
	private DiscreteCoordinates playerCoords;
	
	private int cycleSimulation;
	private int cptCycle = 0;

	
	private boolean afficher = false;
	private int cptOrientate = 0;

	/**
	 * Default DarkLord Constructor
	 * @param area (Area), Not null
	 * @param orientation (Orientation), Not null
	 * @param position (DiscreteCoordinates), Not null
	 * @param maxHp (float), His max HP, not null
	 * @param object (Object), The object that appears when he died
	 */
	public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp, Object object) {
		super(area, orientation, position, maxHp, object);
		
		handler = new DarkLordHandler();
		super.initDommageType(new DommageType[] {DommageType.MAGIC}, null);
		
		etat = Etat.IDLE;
		cycleSimulation = MIN_SPELL_WAIT_DURATION + 
				RandomGenerator.getInstance().nextInt(MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
		
		// Animations
		Sprite[][] spritesPassive = RPGSprite.extractSprites("zelda/darkLord", 3, 2, 2,
				this, 32, 32, new Vector(-0.4f, 0.f),
				new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
		animationsPassive = RPGSprite.createAnimations(ANIMATION_DURATION/2, spritesPassive);

		Sprite[][] spritesActive = RPGSprite.extractSprites("zelda/darkLord.spell", 3, 2, 2,
				this, 32, 32, new Vector(-0.4f, 0.f),
				new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
		animationsActive = RPGSprite.createAnimations(ANIMATION_DURATION/2, spritesActive, false);
		
		fireSpell = new FireSpell(area, orientation, position, 8);
	}
	
	@Override
	public boolean takeCellSpace() {
		return isLiving() && etat != Etat.TELEPORT;
	}
	
	/**
	 * Get the animation depending on this status
	 * @param etat (Etat), His status, not null
	 * @return (Animation), The animation
	 */
	private Animation getAnimation(Etat etat) {
		switch(etat) {
		case IDLE: 
		case TELEPORT : return animationsPassive[getOrientation().ordinal()];
		case ATTACK: 
		case INVOKE_CREATURE: 
		case INVOKE_TELEPORT: return animationsActive[getOrientation().ordinal()];
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
		Area area = getOwnerArea();
		
		if(!isLiving()) {
			super.update(deltaTime);
			return;
		}
		
		if(isDisplacementOccurs() || (etat != Etat.IDLE && etat != Etat.TELEPORT))
			getAnimation(etat).update(deltaTime);
		else getAnimation(etat).reset();
		
		++cptCycle;
		if(cptCycle == cycleSimulation) {
			cycleSimulation = MIN_SPELL_WAIT_DURATION + 
					RandomGenerator.getInstance().nextInt(MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
			cptCycle = 0;
			strategie();
		}
		
		List<DiscreteCoordinates> collection = Collections.singletonList 
        		(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
		
		switch(etat) {
		case IDLE: displacementIA(deltaTime);
			break;
		case ATTACK: 
			if(getAnimation(etat).isCompleted()) {
				getAnimation(etat).reset();
				if(area.canEnterAreaCells(fireSpell, collection))
					area.registerActor(new FireSpell(area, getOrientation(), 
						getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 8));
				etat = Etat.IDLE;
			}
			break;
		case INVOKE_CREATURE: 
			if(getAnimation(etat).isCompleted()) {
				getAnimation(etat).reset();
				if(area.canEnterAreaCells(fireSpell, collection))
					area.registerActor(new FlameSkull(area, getOrientation(), 
		    			getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 0.5f));
				etat = Etat.IDLE;
			}
			break;
		case INVOKE_TELEPORT: 
			if(!isDisplacementOccurs() && getAnimation(etat).isCompleted()) {
				getAnimation(etat).reset();
				etat = Etat.TELEPORT;
			}
			break;
		case TELEPORT :
			teleport();
			etat = Etat.IDLE;
			break;
		}
		
		super.update(deltaTime);
	}
	
	/**
	 * Teleport the darklord
	 */
	private void teleport() {
		DiscreteCoordinates[] coordsTeleport = new DiscreteCoordinates[3];
		for(int i = 0; i < coordsTeleport.length; ++i) 
			coordsTeleport[i] = getTeleportCoords();
		
		float[] distances = new float[3];
		for(int i = 0; i < distances.length; ++i) {
			if(coordsTeleport[i] == null) distances[i] = 0;
			else distances[i] = DiscreteCoordinates.distanceBetween(coordsTeleport[i], playerCoords);
		}
		
		float maxDistance = distances[0];
		int index = 0;
		
		for(int i = 1; i < distances.length; ++i) {
			if(distances[i] > maxDistance) {
				maxDistance = distances[i];
				index = i;
			}
		}
		
		getOwnerArea().leaveAreaCells(this, Collections.singletonList(getCurrentMainCellCoordinates()));
		setCurrentPosition(getCurrentMainCellCoordinates().jump(coordsTeleport[index].toVector()).toVector());
	}
	
	/**
	 * Get the teleportation coordinates
	 * @return (DiscreteCoordinates) The teleportation cell
	 */
	private DiscreteCoordinates getTeleportCoords() {
		int x, y;
		int compteur = 0;
		
		do {
			int TELEPORTATION_RADIUS = 5;
			x = TELEPORTATION_RADIUS - 2 + RandomGenerator.getInstance().nextInt(TELEPORTATION_RADIUS - 2);
			y = TELEPORTATION_RADIUS - 2 + RandomGenerator.getInstance().nextInt(TELEPORTATION_RADIUS - 2);
			if(RandomGenerator.getInstance().nextBoolean()) x = -x;
			if(RandomGenerator.getInstance().nextBoolean()) y = -y;
			++compteur;
		} while(!canEnter(x, y) && compteur <= 5);
		
		if(compteur <= 5)
			return new DiscreteCoordinates(x, y);
		return new DiscreteCoordinates(0, 0);
	}
	
	/**
	 * Tell if the dark lord can enter in a specific cell
	 * @param x (int), The number of cells to jump in axe x 
	 * @param y (int), The number of cells to jump in axe y
	 * @return (boolean), True if he can enter
	 */
	private boolean canEnter(int x, int y) {
		Area area = getOwnerArea();
		List<DiscreteCoordinates> collection = Collections.singletonList(
				getCurrentMainCellCoordinates().jump(new Vector(x, y)));

		return area.canEnterAreaCells(this, collection);
	}
	
	/**
	 * Change the status of the darklord
	 */
	private void strategie() {
		int randomInt = RandomGenerator.getInstance().nextInt(2);
		switch(randomInt) {
		case 0 : etat = Etat.ATTACK;
			break;
		case 1 : etat = Etat.INVOKE_CREATURE;
			break;
		}
		strategieOrientation();
	}
	
	/**
	 * Orientated the next attack of the darklord
	 */
	private void strategieOrientation() {
		int randomInt = RandomGenerator.getInstance().nextInt(4);
		Orientation orientation = Orientation.fromInt(randomInt);
		
		Area area = getOwnerArea();
		assert orientation != null;
		List<DiscreteCoordinates> collection = Collections.singletonList(
    			getCurrentMainCellCoordinates().jump(orientation.toVector()));

    	if(area.canEnterAreaCells(fireSpell, collection)) {
    		cptOrientate = 0;
    		orientate(orientation);
    	} else {
    		++cptOrientate;
    		if(cptOrientate <= 4)
    			strategieOrientation();
    		else 
    			cptOrientate = 0;
    	}
	}
	
	public boolean isLiving() {
		return super.isLiving();
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		int RAYON = 3;
		for(int i = -RAYON; i <= RAYON; ++i) {
			for(int j = -RAYON; j <= RAYON; ++j) {
				collection.add(getCurrentMainCellCoordinates().jump(i, j));
			}
		}
		return collection;
	}
	
	@Override
	public boolean wantsViewInteraction() {
		return isLiving();
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class DarkLordHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer TWICPlayer) {
			if(etat == Etat.IDLE) {
				etat = Etat.INVOKE_TELEPORT;
				playerCoords = TWICPlayer.getCurrentCells().get(0);
			}
		}
		
		public void interactWith(Personnage personnage) {
			if(etat == Etat.IDLE) {
				etat = Etat.INVOKE_TELEPORT;
				playerCoords = personnage.getCurrentCells().get(0);
			}
		}
	}
	
	private enum Etat {
		IDLE,
		ATTACK,
		INVOKE_CREATURE,
		INVOKE_TELEPORT,
		TELEPORT
	}

}
