package ch.epfl.cs107.play.game.twic.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Monster extends MovableAreaEntity implements Interactor {
	
	protected int ANIMATION_DURATION = 10;

	private final Animation deadAnim;
	
	private DommageType[] vulnerability;
	private DommageType attackType;
	private final Object object;
	private Orientation orientation;
	
	private float hp;
	private final float MAX_HP;
	private boolean enVie;
	private boolean invincible = false;
	
	private int wait1 = 15 + RandomGenerator.getInstance().nextInt(20);
	private int wait2 = 55 + RandomGenerator.getInstance().nextInt(30);
	private float deltaTimeCpt = 0.f;
	private int pasDeSimulation = 0;
	private float cptDeTemps = 0f;
	
	private boolean forceDeath = false;

	/**
	 * Default Monster Constructor
	 * @param area (Area), The area he's attached to, not null
	 * @param orientation (Orientation), The orientation given, not null
	 * @param position (DiscreteCoordinates), The coordinates in the area, not null
	 * @param maxHp (float), The maximum of HP, not null
	 * @param object (Object), The object spawn when the monster dies
	 */
	public Monster(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp, Object object) {
		super(area, orientation, position);
		
		this.object = object;
		MAX_HP = maxHp;
		hp = MAX_HP;
		enVie = true;
		this.orientation = orientation;

		Sprite[] deadSprite = new Sprite[7];
		for(int i = 0; i < deadSprite.length; ++i) 
			deadSprite[i] = new RPGSprite("zelda/vanish", 1.5f, 1.5f, this, 
					new RegionOfInterest(i*32, 0, 32, 32), 
					new Vector(-0.25f, 0.f));
		deadAnim = new Animation(4, deadSprite, false);
	}
	
	/**
	 * Initialize the damage type
	 * @param vulnerability (DommageType[]), The vulnerability of the monster
	 * @param attackType (DommageType), The attack type of the monster
	 */
	protected void initDommageType(DommageType[] vulnerability, DommageType attackType) {
		this.vulnerability = vulnerability;
		this.attackType = attackType;
	}
	
	/**
	 * Get the monster's attack type
	 * @return (DommageType), The attack type
	 */
	protected DommageType getAttackType() {
		return attackType;
	}
	
	/**
	 * Get the monster's vulnerability
	 * @return (DommageType[]), The vulnerability
	 */
	protected DommageType[] getVulnerability() {
		return vulnerability;
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	
	@Override
	public boolean takeCellSpace() {
		return enVie;
	}
	
	@Override
	public boolean isCellInteractable() {
		return enVie;
	}
	
	@Override
	public boolean isViewInteractable() {
		return enVie;
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}
	
	public void forceDeath() {
		forceDeath = true;
	}
	
	@Override
	public void draw(Canvas canvas) {
		if ((!enVie && !deadAnim.isCompleted()) && !forceDeath) 
			deadAnim.draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		if(invincible) {
	    	cptDeTemps += deltaTime;
	    	if(cptDeTemps > 1.25f) {
	    		invincible = false;
	    		cptDeTemps = 0.f;
	    	}
	    }
		
		super.update(deltaTime);
		
		if(enVie) return;
		
		deadAnim.update(deltaTime);
		
		if(deadAnim.isCompleted()) {
			Area area = getOwnerArea();
			if(object != null && object instanceof TWICCollectableAreaEntity.CollectableObject) {
				TWICCollectableAreaEntity collectableAreaEntity = new TWICCollectableAreaEntity(area, 
						Orientation.UP, getCurrentMainCellCoordinates(), (TWICCollectableAreaEntity.CollectableObject) object);
				area.registerActor(collectableAreaEntity);
			} else if(object != null && object instanceof TWICItem) 
				area.registerActor(new CastleKey(area, Orientation.UP, getCurrentMainCellCoordinates()));
			
			area.unregisterActor(this);
		}
		
	}
	
	/**
	 * Displace automatically a monster with a simple IA
	 * @param deltaTime (float), The deltaTime between two updates
	 */
	protected void displacementIA(float deltaTime, int speed) {
		++pasDeSimulation;
		if(pasDeSimulation > wait1 && pasDeSimulation < wait2) 
			return;
		else if (pasDeSimulation > wait2) {
			pasDeSimulation = 0;
			wait1 = 15 + RandomGenerator.getInstance().nextInt(20);
			wait2 = 55 + RandomGenerator.getInstance().nextInt(30);
		}
		
		displacement((float) 0.95, speed);
		if(!isDisplacementOccurs()) {
			deltaTimeCpt += deltaTime;
			if(deltaTimeCpt > 2*deltaTime) {
				displacement(0f, speed);
				deltaTimeCpt = 0.f;
			}
		}
	}
	
	protected void displacementIA(float deltaTime) {
		displacementIA(deltaTime, ANIMATION_DURATION);
	}
	
	/**
	 * Displace a monster
	 * @param proba (float), The probability to change his direction
	 */
	protected void displacement(float proba, int speed) {
		float randomFloat = RandomGenerator.getInstance().nextFloat();
		int randomInt;
		
		if(randomFloat > proba) {
			do {
				randomInt = RandomGenerator.getInstance().nextInt(4);
			} while(orientation == Orientation.fromInt(randomInt));
			orientation = Orientation.fromInt(randomInt);
		}
		moveOrientate(orientation, speed);
	}
   
	
    /**
     * Hurt a monster
     * @param lessHp (float), The HP to remove
     * @param dommageType (DommageType), The type of damage made 
     * @return (boolean), True if the monster is hurted
     */
	public boolean hurted(float lessHp, DommageType dommageType) {
		boolean test = false;
		for(DommageType dommage : vulnerability)
			if (dommage.equals(dommageType)) {
				test = true;
				break;
			}
		
		if (!test) return false;
		
		if(invincible) 
			return false;
		else if(lessHp > 0)
			invincible = true;
		
		hp -= lessHp;
		if(hp <= 0) enVie = false;
		return true;
	}

	/**
	 * Get the maximum of HP of a monster
	 * @return (float) The maximum of HP
	 */
	protected float getMaxHp() {
		return MAX_HP;
	}
	
	/**
	 * Tell if a monster lives
	 * @return (boolean), True if it is
	 */
	public boolean isLiving() {
		return enVie;
	}
	
	/**
	 * Set that a monster dies
	 */
	protected void setDeath() {
		enVie = false;
	}
	
	/**
	 * Tell if a monster is invincible for a short moment
	 * @return (boolean), True if it is
	 */
	protected boolean isInvincible() {
		return invincible;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList (getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}

	@Override
	public boolean wantsCellInteraction() {
		return false;
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

	@Override
	public void interactWith(Interactable other) {
		// TODO Auto-generated method stub		
	}
	
	public enum DommageType {
		PHYSIC,
		FIRE,
		MAGIC
	}
	
}
