package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity.CollectableObject;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Grass extends AreaEntity {

	private CollectableObject object;
	
	private final Sprite sprite;
	private boolean cut = false;
	private final Animation cutAnimation;
	private final Area area = getOwnerArea();
	
	/**
	 * Default Grass Constructor
	 * @param owner (Area), The area he's attached to, not null
	 * @param orientation (Orientation), The orientation given, not null
	 * @param coordinates (DiscreteCoordinates), The coordinates in the area, not null
	 */
	public Grass (Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
		super(owner, orientation, coordinates);
		sprite = new Sprite("zelda/grass", 1.f, 1.f, this,
				new RegionOfInterest(0,0,16,16));
		sprite.setDepth(-20.f);

		Sprite[] spriteAnim = new Sprite[4];
		for(int i = 0; i < spriteAnim.length; ++i)
			spriteAnim[i] = new RPGSprite("zelda/grass.sliced", 2, 2, this,
					new RegionOfInterest(i*32+6, 0, 32, 32));
		
		cutAnimation = new Animation(4, spriteAnim, false);
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
        ((TWICInteractionVisitor)v).interactWith(this);
	}
	
	@Override
    public boolean isCellInteractable() {
        return !cut;
    }
	
	@Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
	
	@Override
    public boolean isViewInteractable(){
        return !cut;
    }
	
	@Override
	public void draw(Canvas canvas) {
		if (cut && !cutAnimation.isCompleted()) {
			cutAnimation.draw(canvas);
		} else if (!cut) {
			sprite.draw(canvas);
		}
	}
	
	public void update(float deltaTime) {
		if(!cut) return;
		cutAnimation.update(deltaTime);
		if(cutAnimation.isCompleted()) {
			area.unregisterActor(this);
		}
	}
	
	@Override
    public boolean takeCellSpace() {
        return !cut;
    }
	
	/**
	 * Cut the grass
	 */
	public void cutGrass() {
		cut = true;
		DiscreteCoordinates coordonnees = getCurrentMainCellCoordinates();
    	List<DiscreteCoordinates> collection = new ArrayList<>();
    	collection.add(coordonnees);
		if(!chooseItem() || !area.canEnterAreaCells(this, collection)) return;
		TWICCollectableAreaEntity collectableAreaEntity = new TWICCollectableAreaEntity(area, 
				Orientation.UP, getCurrentMainCellCoordinates(), object);
		area.registerActor(collectableAreaEntity);
	}
	
	/**
	 * When the grass is cut, choose an item to spawn
	 * @return (boolean), True if an item spawns
	 */
	private boolean chooseItem() {
		double nb1 = RandomGenerator.getInstance().nextDouble();
		double PROBABILITY_TO_DROP_ITEM = 0.4;
		if(nb1 > PROBABILITY_TO_DROP_ITEM) return false;
		
		double nb2 = RandomGenerator.getInstance().nextDouble();
		double PROBABILITY_TO_DROP_HEART = 0.5;
		if(nb2 <= PROBABILITY_TO_DROP_HEART) object = CollectableObject.HEART;
		else object = CollectableObject.COIN;
		return true;
	}

}
