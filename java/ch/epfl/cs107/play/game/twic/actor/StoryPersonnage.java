package ch.epfl.cs107.play.game.twic.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class StoryPersonnage extends MovableAreaEntity {

	private final Animation[] animations;
    private final Sprite kingSprite;

	private final Animation deadAnim;
	
	private final StoryPerso personnage;
	private final boolean takeCellSpace;
	
    private boolean isLiving = true;
    private boolean dying = false;

	public StoryPersonnage(Area area, Orientation orientation, DiscreteCoordinates position, StoryPerso personnage, boolean takeCellSpace) {
		super(area, orientation, position);
		this.personnage = personnage;
		this.takeCellSpace = takeCellSpace;

		Sprite[][] sprites = RPGSprite.extractSprites("personalAdds/" + personnage.getName(), 4, 3, 3, this, 64, 64, new Vector(-1f, 0),
				new Orientation[]{Orientation.DOWN, Orientation.LEFT, Orientation.RIGHT, Orientation.UP});
		animations = RPGSprite.createAnimations(3, sprites);
		
		kingSprite = new Sprite("zelda/" + StoryPerso.KING.getName(), 1, 2, this, new RegionOfInterest(0, 64, 16, 32), 
				new Vector(0, -0.1f), 1, -100);

		Sprite[] deadSprite = new Sprite[7];
		for(int i = 0; i < deadSprite.length; ++i)
			deadSprite[i] = new RPGSprite("zelda/vanish", 1.5f, 1.5f, this, new RegionOfInterest(i*32, 0, 32, 32), 
					new Vector(-0.25f, 0.f));
		deadAnim = new Animation(4, deadSprite, false);
		
	}
	
	public StoryPersonnage(Area area, Orientation orientation, DiscreteCoordinates position, StoryPerso personnage) {
		this(area, orientation, position, personnage, true);
	}
	
	/**
	 * @return (StoryPerso) which type of personnage is
	 */
	public StoryPerso getTypePerso() {
		return personnage;
	}
	
	/**
	 * @return true if the storyPersonnage is living
	 */
	public boolean isLiving() {
		return isLiving;
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return takeCellSpace;
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return true;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		if (dying && !deadAnim.isCompleted()) 
			deadAnim.draw(canvas);
		
		else if(isLiving && !dying) {
			if(personnage == StoryPerso.KING)
				kingSprite.draw(canvas);
			else
				animations[getOrientation().ordinal()].draw(canvas);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		updateStory(deltaTime);
	}
	
	/**
     * Simulates a single time step for Story.
     * Note: Need to be Override
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
	public void updateStory(float deltaTime) {
		
		if(deadAnim.isCompleted() && isLiving) {
			isLiving = false;
			getOwnerArea().unregisterActor(this);
			
		} else if(dying && !deadAnim.isCompleted()) 
			deadAnim.update(deltaTime);
		
		else if(personnage != StoryPerso.KING)
			animations[getOrientation().ordinal()].update(deltaTime);
		
		super.update(deltaTime);
	}
	
	/**
	 * kill the storyPersonnage
	 */
	public boolean hurted() {
		dying = true;
		return true;
	}
	
	public enum StoryPerso {
		NICE_HARPY("niceHarpy"),
		BAD_HARPY("badHarpy"),
		KING("king");
		
		private final String name;
		
		StoryPerso(String name) {
			this.name = name;
		}
		
		private String getName() {
			return name;
		}
		
	}

}
