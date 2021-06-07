package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.twic.actor.Monster.DommageType;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Shots extends MovableAreaEntity implements FlyableEntity, Interactor {
	
	private final int VITESSE;
	private final int RANGED_MAX;
	private DommageType attackType;
	private final List<DiscreteCoordinates> cellsTraveled = new ArrayList<>();
	
	private boolean stop = false;

	/**
	 * Default Shots Constructor
	 * @param area (Area), The area he's attached to, not null
	 * @param orientation (Orientation), The orientation given, not null
	 * @param position (DiscreteCoordinates), The coordinates in the area, not null
	 * @param distanceMax (int), The maximal distance to reach
	 * @param vitesse (int), The shots' speed
	 */
	public Shots(Area area, Orientation orientation, DiscreteCoordinates position, int distanceMax, int vitesse) {
		super(area, orientation, position);
		
		VITESSE = vitesse;
		RANGED_MAX = distanceMax;
		cellsTraveled.add(getCurrentMainCellCoordinates());
		
	}
	
	/**
	 * Initialize the damage type of the shot
	 * @param attackType (DommageType), The damage type, not null
	 */
	protected void initDommageType(DommageType attackType) {
		this.attackType = attackType;
	}
	
	/**
	 * Get the attack type of the shot
	 * @return (DommageType), The attack type
	 */
	protected DommageType getAttackType() {
		return attackType;
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
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
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		// TODO Auto-generated method stub
	}

	@Override
	public void draw(Canvas canvas) {
	}
	
	@Override
	public void update(float deltaTime) {
		DiscreteCoordinates lastCell = cellsTraveled.get(cellsTraveled.size()-1);
		if(!getCurrentMainCellCoordinates().equals(lastCell))
			cellsTraveled.add(getCurrentMainCellCoordinates());
		
		if(!stop) 
			move(100/VITESSE);
		
		Area area = getOwnerArea();
		List<DiscreteCoordinates> collection = Collections.singletonList(
				getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
		
		if((cellsTraveled.size() >= RANGED_MAX || !area.canEnterAreaCells(this, collection)) && !stop) 
			stop();
		
		super.update(deltaTime);
	}
	
	/**
	 * Stop the shot
	 */
	protected void stop() {
		if(!stop) getOwnerArea().unregisterActor(this);
		stop = true;
	}

}
