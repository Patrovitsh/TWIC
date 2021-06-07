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
import ch.epfl.cs107.play.game.twic.actor.Monster.DommageType;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class FireSpell extends AreaEntity implements FlyableEntity, Interactor {

	private final FireSpellHandler handler;

	private final Animation animation;
	
	private final DommageType[] vulnerability;
	private final DommageType attackType;
	private final Orientation orientation;
	private final int force;
	private int lifeTime;
	private int propagationCpt = 0;

	/**
	 * Default FireSpell Constructor
	 * @param area (Area), Not null
	 * @param orientation (Orientation), Not null
	 * @param position (DiscreteCoordinates), Not null
	 * @param force (int), The force of the spell
	 */
	public FireSpell(Area area, Orientation orientation, DiscreteCoordinates position, int force) {
		super(area, orientation, position);
		
		handler = new FireSpellHandler();
		vulnerability = new DommageType[] {DommageType.MAGIC, DommageType.PHYSIC};
		attackType = DommageType.FIRE;
		
		this.orientation = orientation;
		this.force = force;
		int MIN_LIFE_TIME = 120;
		int MAX_LIFE_TIME = 240;
		lifeTime = MIN_LIFE_TIME + RandomGenerator.getInstance().nextInt(MAX_LIFE_TIME - MIN_LIFE_TIME);

		Sprite[] sprite = new Sprite[7];
		for(int i = 0; i < sprite.length; ++i) {
			sprite[i] = new RPGSprite("zelda/fire", 1, 1, this,
					new RegionOfInterest(i*16, 0, 16, 16), Vector.ZERO, 1, -1000);
		}
		int ANIMATION_DURATION = 8;
		animation = new Animation(ANIMATION_DURATION /2, sprite);
	}
	
	@Override
	public boolean canFly() {
		return false;
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
		animation.draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		--lifeTime;
		if(lifeTime <= 0) getOwnerArea().unregisterActor(this);
		
		Area area = getOwnerArea();
		++propagationCpt;
		int PROPAGATION_TIME_FIRE = 12;
		if(propagationCpt == PROPAGATION_TIME_FIRE && force > 1) {
			DiscreteCoordinates coords = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
			List<DiscreteCoordinates> collection = new ArrayList<>();
	    	collection.add(coords);
	    	if(area.canEnterAreaCells(this, collection))
	    		area.registerActor(new FireSpell(area, orientation, coords, force-1));
		}
			
		animation.update(deltaTime);
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return null;
	}

	@Override
	public boolean wantsCellInteraction() {
		return true;
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Remove the fire spell if it's "hurted"
	 * @param dommageType (DommageType), The damage type
	 */
	public void dispawn(DommageType dommageType) {
		for(DommageType dommage : vulnerability)
			if(dommageType == dommage)
				getOwnerArea().unregisterActor(this);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class FireSpellHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer TWICPlayer) {
			TWICPlayer.hurted(1f, attackType);
		}
		
		public void interactWith(Monster monster) {
			monster.hurted(0.5f, attackType);
		}
		
		public void interactWith(Grass grass) {
			grass.cutGrass();
		}
		
		public void interactWith(Bomb bomb) {
			bomb.toExplode();
		}
		
		public void interactWith(Personnage personnage) {
			personnage.hurted(1f, attackType);
		}
		
		public void interactWith(Shop shop) {
			shop.hurted();
		}
		
	}

}

