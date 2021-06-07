package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.twic.utils.DisplacementAdvancedIA;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Dragon extends Monster implements FlyableEntity {
	
	private final DragonHandler handler;

	private final Animation[] animations;
	
	private Etat state;
	private DiscreteCoordinates targetCoords;
	private MovableAreaEntity target;
	
	private boolean afficher = false;
	private int timerFire = 0;
	
	private final DragonSpell dragonSpellTest;

	public Dragon(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp, Object object) {
		super(area, orientation, position, maxHp, object);
		handler = new DragonHandler();
		super.initDommageType(new DommageType[]{DommageType.MAGIC,  DommageType.PHYSIC}, DommageType.PHYSIC);

		Sprite[][] sprites = RPGSprite.extractSprites("personalAdds/dragon", 3, 3, 3,
				this, 191, 148, new Vector(-1f, 0f),
				new Orientation[]{Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		animations = RPGSprite.createAnimations(4, sprites);
		
		animations[Orientation.UP.ordinal()].setAnchor(new Vector(-1f, -1f));

		for (Sprite[] sprite : sprites)
			for (Sprite value : sprite)
				value.setDepth(1000);
		
		state = Etat.IDLE;
		
		dragonSpellTest = new DragonSpell(area, orientation, position, 0);
	}
	
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	
	@Override
	public boolean wantsCellInteraction() {
		return isLiving();
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return newListDiscreteCoordinates();
	}
	
	/**
	 * 
	 * @return new ArrayList<DiscreteCoordinates> with the coordinates of the cells in the radius
	 */
	private List<DiscreteCoordinates> newListDiscreteCoordinates() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = -10; i <= 10; ++i) {
			for(int j = -10; j <= 10; ++j) {
				collection.add(getCurrentMainCellCoordinates().jump(i, j));
			}
		}
		return collection;
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
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection;
		DiscreteCoordinates myCoordinates = getCurrentMainCellCoordinates();
		
		collection = myCoordinates.getNeighbours();
		collection.add(myCoordinates);
		
		if (getOrientation() == Orientation.DOWN)
			collection.remove(myCoordinates.down());
		
		return collection;
	}
	
	@Override
	public void update(float deltaTime) {
		if(!isLiving()) {
			super.update(deltaTime);
			return;
		}
		
		animations[getOrientation().ordinal()].update(deltaTime);
		
		Orientation orientation;
		
		switch(state) {
		case IDLE: displacementIA(deltaTime, 5);
		    break;
		case FIRE: timerFire++;
			orientation = DisplacementAdvancedIA.goToTarget(this, targetCoords,
					getCurrentMainCellCoordinates(), getOwnerArea());
			orientate(orientation);
			if (timerFire == 1) unleashTheDragon();
			if (timerFire > 50) {
				state = Etat.IDLE;
				timerFire = 0;
			}
			break;
		case HUNTING:
			orientation = DisplacementAdvancedIA.goToTarget(this, targetCoords,
					getCurrentMainCellCoordinates(), getOwnerArea());
			if(orientation != null)
				moveOrientate(orientation, 5);
			break;
		}
		
		super.update(deltaTime);
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
	 * @param entity : the target
	 * @return true if the entity is on the same scell as the dragon
	 */
	private boolean interactionIA(MovableAreaEntity entity) {
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
		
		List<DiscreteCoordinates> allDragonCoords = getCurrentCells();
		if(allDragonCoords.contains(entityCoords)) {
			state = Etat.IDLE;
			target = null;
			targetCoords = null;
			return true;
		}
		
		if(!entity.equals(target)) return false;
		targetCoords = entityCoords;
		
		if (DiscreteCoordinates.distanceBetween(coords, targetCoords) < 4) {
			state = Etat.FIRE;
			return false;
		}
		
		return false;
	}
	
	/**
	 * make the dragon spit fire
	 */
	private void unleashTheDragon() {
		Area area = getOwnerArea();
		Orientation orientation = this.getOrientation();
		List<DiscreteCoordinates> collection = Collections.singletonList(
				getCurrentMainCellCoordinates().jump(orientation.toVector()));
		
		if(area.canEnterAreaCells(dragonSpellTest, collection))
			area.registerActor(new DragonSpell(area, getOrientation(), 
				getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 4));
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class DragonHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer player) {
			if (state != Etat.FIRE) state = Etat.HUNTING;
			if(interactionIA(player))
				player.hurted(5.f, getAttackType());
		}
	}
	
	private enum Etat {
		IDLE,
		FIRE,
		HUNTING
	}

}
