package ch.epfl.cs107.play.game.twic.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.twic.actor.Monster.DommageType;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class Personnage extends MovableAreaEntity {
	
	private final int ANIMATION_DURATION = 8;
	private int wait1 = 15 + RandomGenerator.getInstance().nextInt(20);
	private int wait2 = 55 + RandomGenerator.getInstance().nextInt(30);

	private final Animation[] animations;
	private final Animation deadAnim;
	
	private final ImageGraphics[] dialogs = new ImageGraphics[3];
	
	private Orientation orientation;
    
    private final DommageType[] vulnerability;
	private float hp;
    private boolean invincible = false;
	private boolean isLiving;
	private boolean immortal = false;
	
	private float timer = 0f;
	private boolean display = false;
	private float deltaTimeCpt = 0.f;
	private int pasDeSimulation = 0;
	
	private boolean displayMessage = false;
	private int index;

	public Personnage(Area area, Orientation orientation, DiscreteCoordinates position, float maxHp) {
		super(area, orientation, position);
		
		this.orientation = orientation;
		
		isLiving = true;
		vulnerability = new DommageType[] {DommageType.FIRE, DommageType.MAGIC, DommageType.PHYSIC};
		hp = maxHp;

		Sprite[][] sprites = RPGSprite.extractSprites("zelda/character", 4, 1, 2,
				this, 16, 32,
				new Orientation[]{Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);

		Sprite[] deadSprite = new Sprite[7];
		for(int i = 0; i < deadSprite.length; ++i)
			deadSprite[i] = new RPGSprite("zelda/vanish", 1.5f, 1.5f, this,
					new RegionOfInterest(i*32, 0, 32, 32),
					new Vector(-0.25f, 0.f));
		deadAnim = new Animation(ANIMATION_DURATION/2, deadSprite, false);
		
		for(int i = 0; i < dialogs.length; ++i) {
			dialogs[i] = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtPersonnage" + (char)(i+49)), 
					9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1,
					TWICPlayerStatusGUI.DEPTH);
		}
		
	}
	
	public Personnage(Area area, Orientation orientation, DiscreteCoordinates position, boolean immortal) {
		this(area, orientation, position, 1);
		this.immortal = immortal;
	}
	
	/**
	 * request a display and choose the message to display
	 */
	protected void displayMessage() {
		displayMessage = true;
		index = RandomGenerator.getInstance().nextInt(3);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return isLiving;
	}

	@Override
	public boolean isCellInteractable() {
		return isLiving;
	}

	@Override
	public boolean isViewInteractable() {
		return isLiving;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		
		if(displayMessage)
			drawMessage(canvas);
		
		if(immortal) {
			animations[getOrientation().ordinal()].draw(canvas);
			return;
		}
		
		if(isLiving) {
			if(invincible) {
				display = !display;
				if(display) {
					animations[getOrientation().ordinal()].draw(canvas);
				}
			} else animations[getOrientation().ordinal()].draw(canvas);
		} else if (!deadAnim.isCompleted()) 
			deadAnim.draw(canvas);
		
	}
	
	/**
	 * show message
	 */
	private void drawMessage(Canvas canvas) {
		Keyboard keyboard = getOwnerArea().getKeyboard();
		if(keyboard.get(Keyboard.ENTER).isPressed()) 
			displayMessage = false;
			
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));
		
		dialogs[index].setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialogs[index].draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		if(isLiving)
			displacementIA(deltaTime);
		
		if (isDisplacementOccurs() && isLiving)
			animations[getOrientation().ordinal()].update(deltaTime);
		else if(isLiving)
			animations[getOrientation().ordinal()].reset();
		else {
			deadAnim.update(deltaTime);
			if(deadAnim.isCompleted()) {
				getOwnerArea().unregisterActor(this);
			}
		}
		
		if(invincible) {
	    	timer += deltaTime;
	    	if(timer > 1.25f) {
	    		invincible = false;
	    		timer = 0.f;
	    	}
	    }
		
		super.update(deltaTime);
	}
	
	/**
	 * Displace automatically a monster with a simple IA
	 * @param deltaTime (float), The deltaTime between two updates
	 */
	private void displacementIA(float deltaTime) {
		++pasDeSimulation;
		if(pasDeSimulation > wait1 && pasDeSimulation < wait2) 
			return;
		else if (pasDeSimulation > wait2) {
			pasDeSimulation = 0;
			wait1 = 15 + RandomGenerator.getInstance().nextInt(20);
			wait2 = 55 + RandomGenerator.getInstance().nextInt(30);
		}
		
		displacement((float) 0.95);
		if(!isDisplacementOccurs()) {
			deltaTimeCpt += deltaTime;
			if(deltaTimeCpt > 2*deltaTime) {
				displacement(0f);
				deltaTimeCpt = 0.f;
			}
		}
	}
	
	/**
	 * Displace a monster
	 * @param proba (float), The probability to change his direction
	 */
	private void displacement(float proba) {
		
		float randomFloat = RandomGenerator.getInstance().nextFloat();
		int randomInt;
		
		if(randomFloat > proba) {
			do {
				randomInt = RandomGenerator.getInstance().nextInt(4);
			} while(orientation == Orientation.fromInt(randomInt));
			orientation = Orientation.fromInt(randomInt);
		}
		moveOrientate(orientation);
	}
	
	/**
	 * Move a monster in a simple direction
	 * @param orientation (Orientation), His orientation
	 */
    private void moveOrientate(Orientation orientation){
	    
	    if(getOrientation() == orientation) move(ANIMATION_DURATION);
	    else orientate(orientation);
	}
    
    /**
     * Simulates a single time step for Story.
     * Note: Need to be Override
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    public void updateStory(float deltaTime) {
    	animations[getOrientation().ordinal()].setSpeedFactor(4);
    	
    	if(isDisplacementOccurs())
    		animations[getOrientation().ordinal()].update(deltaTime);
    	else 
    		animations[getOrientation().ordinal()].reset();
		
		super.update(deltaTime);
    }
	
    /**
     * Hurt a monster
     * @param lessHp (float), The HP to remove
     * @param dommageType (DommageType), The type of damage made 
     * @return (boolean), True if the monster is hurted
     */
	protected boolean hurted(float lessHp, DommageType dommageType) {
		if(immortal) return false;
		
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
		if(hp <= 0) isLiving = false;
		return true;
	}
	
	/**
	 * Tell if a monster lives
	 * @return (boolean), True if it is
	 */
	protected boolean isLiving() {
		return isLiving;
	}
	

}
