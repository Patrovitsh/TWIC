package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class TWICCollectableAreaEntity extends CollectableAreaEntity implements FlyableEntity {
	
	public enum CollectableObject {
		COIN ("zelda/coin", 1, 1, 16, 4),
		HEART ("zelda/heart", 1, 1, 16, 4),
		STAFF_WATER ("zelda/staff", 2, 2, 32, 8),
		ARROW_HEAD_RESEARCHER ("personalAdds/magicArrow", 2, 2, 192, 8);
		
		private final String title;
		private final float width;
		private final float height;
		private final int sizeSprite;
		private final int nbSprite;
		
		CollectableObject(String title, float width, float height, int sizeSprite, int nbSprite) {
			this.title = title;
			this.width = width;
			this.height = height;
			this.sizeSprite = sizeSprite;
			this.nbSprite = nbSprite;
		}
		
		public String getTitle() {
			return title;
		}
		
		public float getWidth() {
			return width;
		}
		
		public float getHeigth() {
			return height;
		}
		
		public int getSizeSprite() {
			return sizeSprite;
		}
		
		public int getNbSprite() {
			return nbSprite;
		}
	}

	private final CollectableObject object;
	private final Sprite[] spriteAnim;
	private Animation animation;
	private boolean isCollect = false;

	public TWICCollectableAreaEntity(Area area, Orientation orientation, DiscreteCoordinates position,
									 CollectableObject object) {
		super(area, orientation, position);
		this.object = object;
		spriteAnim = new Sprite[object.getNbSprite()];
		afficher(object);
	}
	
	/**
	 * define the animation according to the object
	 */
	private void afficher(CollectableObject object) {
		String title = object.getTitle();
		float width = object.getWidth();
		float height = object.getHeigth();
		int size = object.getSizeSprite();
		
		for(int i = 0; i < spriteAnim.length; ++i) {
			spriteAnim[i] = new RPGSprite(title, width, height, this,
					new RegionOfInterest(i*size, 0, size, size), Vector.ZERO, 1, -1);
			if(width > 1) spriteAnim[i].setAnchor(new Vector(-width/4.f, 0));
		}
		int ANIMATION_DURATION = 4;
		animation = new Animation(ANIMATION_DURATION, spriteAnim);
	}
	
	/**
	 * Collect the object
	 * @return (CollectableObject) The object
	 */
	protected CollectableObject collect() {
		getOwnerArea().unregisterActor(this);
		isCollect = true;
		return object;
	}
	
	/**
	 * @return true if it's collect
	 */
	public boolean isCollect() {
		return isCollect;
	}
	
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		// TODO Auto-generated method stub	
	    ((TWICInteractionVisitor)v).interactWith(this);
	}

}
