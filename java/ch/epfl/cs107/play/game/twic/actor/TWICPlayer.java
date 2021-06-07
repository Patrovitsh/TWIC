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
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.twic.actor.Monster.DommageType;
import ch.epfl.cs107.play.game.twic.area.Grotte;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class TWICPlayer extends Player implements Inventory.Holder{
	/// Animation duration in frame number
    private static final int ANIMATION_DURATION = 8;
    private int SPEED = 8;

	private final Animation[] animationsIDLE;
	private final Animation[] animationsSWORD;
	private final Animation[] animationsBOW;
	private final Animation[] animationsSTAFF;
	private final Animation[] animationsKing;
	private final Animation deadAnim;

	private final Sprite reducedVision;
    
    private boolean iAmKing = false;
    
    private final TWICPlayerStatusGUI statusGUI;
    private State state;
    
    private final TWICPlayerHandler handler;
    private final DommageType[] vulnerability;
	private final DommageType attackType;
	private final Shots shotTest;
	
	private final int maxHP;
    private float hp;
    private boolean invincible = false;
    private boolean storyMode = false;
    
    private final TWICInventory inventory;
    private TWICItem currentItem;
    private int currentIndex;
    
    private Shop shop;
    private boolean collect = false;
    
    private boolean press_E;
    private float cptDeTemps = 0f;
    private int cptDeTempsPourSprint = 0;
    private boolean afficher = false;

	/**
	 * Default ARPGPlayer Constructor
	 * @param area (Area), An area, not null
	 * @param orientation (Orientation), An orientation, not null
	 * @param coordinates (DiscreteCoordinates), The coordinates, not null
	 */
	public TWICPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates) {
		super(area, orientation, coordinates);
		this.handler = new TWICPlayerHandler();
		this.maxHP = 10;
		this.hp = this.maxHP;
		
		vulnerability = new DommageType[] {DommageType.FIRE, DommageType.MAGIC, DommageType.PHYSIC};
		attackType = DommageType.PHYSIC;
		
		inventory = new TWICInventory(500, 100);
		inventory.addItem(TWICItem.SWORD, 1);
		setItem();
		
		statusGUI = new TWICPlayerStatusGUI(hp, maxHP, currentItem, inventory.getMoney());
		state = State.IDLE;

		Sprite[][] spritesIDLE = RPGSprite.extractSprites("zelda/player", 4, 1, 2,
				this, 16, 32,
				new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
		animationsIDLE = RPGSprite.createAnimations(ANIMATION_DURATION/2, spritesIDLE);

		Sprite[][] spritesSWORD = RPGSprite.extractSprites("zelda/player.sword", 4, 2, 2,
				this, 32, 32, new Vector(-0.5f, 0),
				new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
		animationsSWORD = RPGSprite.createAnimations(2, spritesSWORD, false);

		Sprite[][] spritesBOW = RPGSprite.extractSprites("zelda/player.bow", 4, 2, 2,
				this, 32, 32, new Vector(-0.5f, 0),
				new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
		animationsBOW = RPGSprite.createAnimations(3, spritesBOW, false);

		Sprite[][] spritesSTAFF = RPGSprite.extractSprites("zelda/player.staff_water", 4, 2,
				2, this, 32, 32, new Vector(-0.5f, 0),
				new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
		animationsSTAFF = RPGSprite.createAnimations(3, spritesSTAFF, false);
		
		reducedVision =  new Sprite("personalAdds/ReducedVision", 32, 32, this,
				new RegionOfInterest(0, 0, 640, 640),
				new Vector(-15.5f, -15f), 1f, TWICPlayerStatusGUI.DEPTH-2);

		Sprite[][] spritesKing = RPGSprite.extractSprites("personalAdds/playerKing", 4, 1,
				2, this, 16, 32,
				new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
		animationsKing = RPGSprite.createAnimations(ANIMATION_DURATION/2, spritesKing);

		Sprite[] deadSprite = new Sprite[7];
		for(int i = 0; i < deadSprite.length; ++i)
			deadSprite[i] = new RPGSprite("zelda/vanish", 1.5f, 1.5f, this,
					new RegionOfInterest(i*32, 0, 32, 32),
					new Vector(-0.25f, 0.f));
		deadAnim = new Animation(4, deadSprite, false);
		
		shotTest = new Shots(area, orientation, coordinates, 0, 0);
	}

	@Override
	public void update(float deltaTime) {
		storyMode = false;
		
		if (isDisplacementOccurs() || (state != State.IDLE && state != State.CHECK_INVENTORY
				&& state != State.LOOK_SHOP))
	        getAnimation(state).update(deltaTime);
	    else if(state == State.IDLE || state == State.CHECK_INVENTORY)
	        getAnimation(state).reset(); 
	    
	    if(invincible) {
	    	cptDeTemps += deltaTime;
	    	if(cptDeTemps > 1.25f) {
	    		invincible = false;
	    		cptDeTemps = 0.f;
	    	}
	    }
		
	    Area area = getOwnerArea();
	    
		switch(state) {
		case IDLE : idle();
			break;
		case CHECK_INVENTORY : checkInventory();
			break;
		case LOOK_SHOP : lookShop();
			break;
		case ATT_SWORD: 
			if(getAnimation(state).isCompleted()) {
				getAnimation(state).reset();
				state = State.IDLE;
			}
			break;
		case ATT_BOW : 
			if(getAnimation(state).isCompleted()) {
				getAnimation(state).reset();
				area.registerActor(new Arrow(area, getOrientation(), 
							getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 9, 50));
				state = State.IDLE;
			}
			break;
		case ATT_STAFF: 
			if(getAnimation(state).isCompleted()) {
				getAnimation(state).reset();
				area.registerActor(new MagicWaterProjectile(area, getOrientation(), 
							getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 7, 50));
				state = State.IDLE;
			}
			break;
		case DEAD : deadAnim.update(deltaTime);
		    if(deadAnim.isCompleted()) 
		    	area.unregisterActor(this);
			break;
		}
		
	    switch(currentItem) {
	    case BOW : statusGUI.update(hp, currentItem, inventory.getMoney(), inventory.getQuantity(TWICItem.ARROW));
	    	break;
	    case BOMB : statusGUI.update(hp, currentItem, inventory.getMoney(), inventory.getQuantity(TWICItem.BOMB));
	    	break;
	    default : statusGUI.update(hp, currentItem, inventory.getMoney());
	    }
	    
	    super.update(deltaTime);
	          
	}
	
	/**
	 * Move the ARPGPlayer when he's in IDLE mode
	 */
	private void idle() {
		Keyboard keyboard = getOwnerArea().getKeyboard();
	    moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
	    moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
	    moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
	    moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
	    
	    if(keyboard.get(Keyboard.I).isPressed()) {
	    	state = State.CHECK_INVENTORY;
	    	statusGUI.showInventory(inventory);
	    }

		press_E = keyboard.get(Keyboard.E).isPressed();
	    
	    if(keyboard.get(Keyboard.TAB).isPressed()) setItem();
	    
	    if(keyboard.get(Keyboard.SPACE).isPressed()) useItem();
	    
	    sprint(keyboard);
	    
	}
	
	/*
	 * the player looks at his inventory
	 */
	private void checkInventory() {
		Keyboard keyboard = getOwnerArea().getKeyboard();
		
		if((keyboard.get(Keyboard.I).isPressed()) || (keyboard.get(Keyboard.ENTER).isPressed())) {
			state = State.IDLE;
			statusGUI.showInventory(inventory);
		}
		
		currentIndex = displacementInventory(keyboard, inventory.getSize(), currentIndex);
		currentItem = (TWICItem) inventory.getItem(currentIndex);
	}
	
	/**
	 * the player looks at the shop
	 */
	private void lookShop() {
		Keyboard keyboard = getOwnerArea().getKeyboard();
		if(keyboard.get(Keyboard.I).isPressed()) {
			shop.showInventory(false);
			state = State.IDLE;
		}
		
		if(keyboard.get(Keyboard.ENTER).isPressed()) 
			shop.buyItem(inventory);
		
		shop.setCurrentIndex(displacementInventory(keyboard, shop.getInventorySize() , shop.getCurrentIndex()));
	}
	
	/**
	 * allows to change the item select
	 */
	private int displacementInventory(Keyboard keyboard, int inventorySize, int index) {
		
		if (keyboard.get(Keyboard.RIGHT).isPressed()) {
			if(index > inventorySize-2) index = 0;
			index += 2;
		}
		
		if (keyboard.get(Keyboard.LEFT).isPressed()) {
			if (index - 2 <= 0) index = inventorySize+2;
			index -= 2;
		}
		
		if (keyboard.get(Keyboard.DOWN).isPressed()) {
			if(index > inventorySize-1) index = 0;
			++index;
		}
		
		if (keyboard.get(Keyboard.UP).isPressed()) {
			if (index - 1 <= 0) index = inventorySize+1;
			--index;
		}
		
		return index;
	}
	
	/**
	 * Makes the ARPGPlayer sprint
	 * @param keyboard (Keyboard), Not null
	 */
	private void sprint(Keyboard keyboard) {
		if(keyboard.get(Keyboard.S).isPressed()) {
	    	SPEED = 4;
			for (Animation animation : animationsIDLE) animation.setSpeedFactor(2);
	        
	    } else if (SPEED != 8 && !isDisplacementOccurs()){
	    	++cptDeTempsPourSprint;
	    	if(cptDeTempsPourSprint > 10) {
	    		SPEED = 8;
				for (Animation animation : animationsIDLE) animation.setSpeedFactor(1);
	    		cptDeTempsPourSprint = 0;
	    	}
	    }
	    
	    if(SPEED == 4 && isDisplacementOccurs())
	    	cptDeTempsPourSprint = 0;
	}

	/**
	 * Orientate or Move this player in the given orientation if the given button is down
	 * @param orientation (Orientation): given orientation, not null
	 * @param b (Button): button corresponding to the given orientation, not null
	 */
    private void moveOrientate(Orientation orientation, Button b){
	    
	    if(b.isDown()) {
	    	if(getOrientation() == orientation) move(SPEED);
	        else orientate(orientation);
	    }
	}
    
    /**
     * Simulates a single time step for Story.
     * Note: Need to be Override
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    public void updateStory(float deltaTime) {
    	storyMode = true;
    	
    	SPEED = 8;
		for(int i = 0; i < 4; ++i) 
			animationsIDLE[i].setSpeedFactor(1);
    	
    	if(isDisplacementOccurs())
    		getAnimation(State.IDLE).update(deltaTime);
    	else 
    		getAnimation(State.IDLE).reset();
		
		super.update(deltaTime);
    }
    
    /**
     * will allow to convert the animation of the player into animation of the king
     */
    public void setMeKing() {
    	iAmKing = true;
    }
    
    private Animation getAnimation(State state) {
		switch(state) {
		case CHECK_INVENTORY :
		case IDLE : if(iAmKing) return animationsKing[getOrientation().ordinal()];
			return animationsIDLE[getOrientation().ordinal()];
		case ATT_SWORD : return animationsSWORD[getOrientation().ordinal()];
		case ATT_BOW : return animationsBOW[getOrientation().ordinal()];
		case ATT_STAFF : return animationsSTAFF[getOrientation().ordinal()];
		case DEAD : return deadAnim;
		default : return getAnimation(State.IDLE);
		}
	}
    
	@Override
	public void draw(Canvas canvas) {
		statusGUI.draw(canvas);
		if(getOwnerArea() instanceof Grotte) reducedVision.draw(canvas);
		
		if(storyMode) {
			getAnimation(state).draw(canvas);
			return;
		}
		
		if(invincible) {
			afficher = !afficher;
			if(afficher) return;
		}
		getAnimation(state).draw(canvas);
	}

	@Override
	public boolean takeCellSpace() {
		return true;
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
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList (getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}

	@Override
	public boolean wantsCellInteraction() {
		return true;
	}

	@Override
	public boolean wantsViewInteraction() {
		return press_E || state == State.ATT_SWORD;
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	@Override
	public boolean possess(InventoryItem item) {
		return inventory.isInInventory(item);
	}
	
	/**
	 * Set an item as a current item in the inventory
	 */
	private void setItem() {
		if(inventory.getSize() > currentIndex) 
			++currentIndex;
		else 
			currentIndex = 1;
		
		currentItem = (TWICItem) inventory.getItem(currentIndex);
		
		if(currentItem == TWICItem.ARROW) setItem();
	}
	
	/**
	 * Use an item in the inventory
	 */
	private void useItem() {
		int currentQuantity = inventory.getQuantity(currentItem); 
		if(currentQuantity < 1) return;
		
		Area area = getOwnerArea();
		DiscreteCoordinates coordonnees = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
		List<DiscreteCoordinates> collection = new ArrayList<>();
    	collection.add(coordonnees);
		
		TWICItem item = currentItem;
		boolean objectUsed = false;
    	
		switch (currentItem) {
		case SWORD : state = State.ATT_SWORD;
			break;
		case BOW : 
			item = TWICItem.ARROW;
			if(inventory.getQuantity(item ) > 0 && area.canEnterAreaCells(shotTest, collection)) {
				objectUsed = true;
				state = State.ATT_BOW;
			}
		    break;
		case STAFF : 
			if(area.canEnterAreaCells(shotTest, collection))
				state = State.ATT_STAFF;
			break;
		case BOMB : 
        	if (area.canEnterAreaCells(this, collection)) {
        		Bomb bomb = new Bomb(area, coordonnees, 72);
	        	objectUsed = area.registerActor(bomb);
        	}
			break;
		case ARROW_HEAD_RESEARCHER :
			if(area.canEnterAreaCells(shotTest, collection)) {
				ArrowHeadResearcher yaka = new ArrowHeadResearcher(area, getOrientation(), coordonnees, 50, 
						getCurrentMainCellCoordinates());
				objectUsed = area.registerActor(yaka);
				collect = false;
			}
			break;
		case HEART : objectUsed = true;
			if(hp == maxHP) 
				objectUsed = false;
			else if(hp + 1 > maxHP) hp = maxHP;
		    else hp += 1;
			break;
		default : break;
		}
		
		if(objectUsed) {
			inventory.removeItem(item, 1); 
			currentQuantity = inventory.getQuantity(item);
			if(currentQuantity == 0) setItem();
		}
	}
	
	/**
	 * Hurt an ARPGPlayer
	 * @param lessHp (float), The HP to remove
	 * @param dommageType (DommageType), The type of damage
	 * @return (Boolean) True if the player is hurted
	 */
	protected boolean hurted(float lessHp, DommageType dommageType) {
		if(storyMode) return false;
		
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
		if(hp <= 0) {
			hp = 0;
			state = State.DEAD;
		}
		return true;
	}
	
	/**
	 * Handle all the interactions with all the actors
	 */
	private class TWICPlayerHandler implements TWICInteractionVisitor {
		public void interactWith(Door door) {
			setIsPassingADoor(door);
		}
		
		public void interactWith(Grass grass) {
			if(state == State.ATT_SWORD)
				grass.cutGrass();
		}
		
		public void interactWith(TWICCollectableAreaEntity collectableAreaEntity) {
			switch(collectableAreaEntity.collect()) {
			case COIN : inventory.addMoney(50);
				break;
			case HEART : 
				if(hp + 1 > maxHP) hp = maxHP;
			    else hp += 1;
				break;
			case STAFF_WATER :
				inventory.addItem(TWICItem.STAFF, 1);
				currentItem = TWICItem.STAFF;
				break;
			case ARROW_HEAD_RESEARCHER :
				inventory.addItem(TWICItem.ARROW_HEAD_RESEARCHER, 1);
				currentItem = TWICItem.ARROW_HEAD_RESEARCHER;
				break;
			}
		}
		
		public void interactWith(CastleDoor castleDoor) {
			if(state != State.IDLE) return;
			
			if(castleDoor.isOpen()) {
				setIsPassingADoor(castleDoor);
				castleDoor.setSignal(Logic.FALSE);
			} else if(possess(TWICItem.CASTLE_KEY)) {
				castleDoor.setSignal(Logic.TRUE);
		    }
		}
		
		public void interactWith(CastleKey castleKey) {
			inventory.addItem(castleKey.collect(), 1);
		}
		
		public void interactWith(Monster monster) {	
			if(state == State.ATT_SWORD)
				monster.hurted(2.f, attackType);
		}
		
		public void interactWith(StoryPersonnage harpy) {
			if(state == State.ATT_SWORD)
				harpy.hurted();
		}
		
		public void interactWith(Personnage personnage) {
			if(state == State.ATT_SWORD)
				personnage.hurted(2.f, attackType);
			else 
				personnage.displayMessage();
		}
		
		public void interactWith(ArrowHeadResearcher arrowHeadResearcher) {
			if(collect) return;
			TWICItem item = arrowHeadResearcher.collect();
			inventory.addItem(item, 1);
			currentItem = item;
			collect = true;
		}
		
		public void interactWith(Shop aShop) {
			if(state == State.ATT_SWORD) {
				aShop.hurted();
			} else if(state == State.IDLE) {
				shop = aShop;
				shop.showInventory(true);
				state = State.LOOK_SHOP;
				press_E = false;
			}
		}
		
	}
	
	private enum State {
		IDLE, 
		CHECK_INVENTORY,
		LOOK_SHOP,
		ATT_SWORD,
		ATT_BOW,
		ATT_STAFF,
		DEAD
	}
	
}
