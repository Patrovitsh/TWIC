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
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class DragonSpell extends AreaEntity implements FlyableEntity, Interactor {

	private final DragonSpellHandler handler;

	private final Animation animation;
	
	private final Monster.DommageType[] vulnerability;
	private final Monster.DommageType attackType;
	private final Orientation orientation;
	private final int force;
	private int lifeTime;
	private int propagationCpt = 0;

	public DragonSpell(Area area, Orientation orientation, DiscreteCoordinates position, int force) {
		super(area, orientation, position);
		
		handler = new DragonSpellHandler();
		vulnerability = new Monster.DommageType[] {Monster.DommageType.MAGIC, Monster.DommageType.PHYSIC};
		attackType = Monster.DommageType.FIRE;
		
		this.orientation = orientation;
		this.force = force;
		int MAX_LIFE_TIME = 35;
		int MIN_LIFE_TIME = 20;
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
		int PROPAGATION_TIME_FIRE = 4;
		if(propagationCpt == PROPAGATION_TIME_FIRE && force > 1) {
			DiscreteCoordinates coords_1 = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
			DiscreteCoordinates coords_2 = getCurrentMainCellCoordinates().jump(
					getOrientation().toVector()).jump(getOrientation().hisRight().toVector());
			DiscreteCoordinates coords_3 = getCurrentMainCellCoordinates().jump(
					getOrientation().toVector()).jump(getOrientation().hisLeft().toVector());
			List<DiscreteCoordinates> collection = new ArrayList<>();
			collection.add(coords_1);
			collection.add(coords_2);
			collection.add(coords_3);

			if(area.canEnterAreaCells(this, collection)) {
	    		area.registerActor(new DragonSpell(area, orientation, coords_1, force-1));
	    		area.registerActor(new DragonSpell(area, orientation, coords_2, force-1));
	    		area.registerActor(new DragonSpell(area, orientation, coords_3, force-1));
	    	}
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
	public void dispawn(Monster.DommageType dommageType) {
		for(Monster.DommageType dommage : vulnerability)
			if(dommageType == dommage)
				getOwnerArea().unregisterActor(this);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class DragonSpellHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer TWICPlayer) {
			TWICPlayer.hurted(1.5f, attackType);
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
			personnage.hurted(1.5f, attackType);
		}
		
		public void interactWith(Shop shop) {
			shop.hurted();
		}
		
	}

}
