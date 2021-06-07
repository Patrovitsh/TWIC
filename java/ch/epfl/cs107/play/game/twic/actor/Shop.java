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
import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Shop extends AreaEntity implements Inventory.Holder {
	
	private State state;
	
	private final ShopStatusGUI shopGUI;
	private final TWICInventory inventory;
    private TWICItem currentItem;
    private int currentIndex;
    
	private final Sprite sprite;
	private final Animation deadAnim;

	public Shop(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		// TODO Auto-generated constructor stub
		state = State.LIVE;
		
		inventory = new TWICInventory(500, 0);
		inventory.addItem(TWICItem.BOW, 1);
		inventory.addItem(TWICItem.ARROW, 30);
		inventory.addItem(TWICItem.BOMB, 10);
		inventory.addItem(TWICItem.HEART, 3);
		
		currentIndex = 1;
		currentItem = (TWICItem) inventory.getItem(currentIndex);
		shopGUI = new ShopStatusGUI(inventory, currentItem, currentIndex);
		
		sprite =  new Sprite("zelda/character", 1, 2, this,
				new RegionOfInterest(0, 64, 16, 32));

		Sprite[] deadSprite = new Sprite[7];
		for(int i = 0; i < deadSprite.length; ++i)
			deadSprite[i] = new RPGSprite("zelda/vanish", 1.5f, 1.5f, this,
					new RegionOfInterest(i*32, 0, 32, 32),
					new Vector(-0.25f, 0));
		deadAnim = new Animation(4, deadSprite, false);
	}
	
	/**
	 * @param show : true if you want to show the Inventory
	 */
	protected void showInventory(boolean show) {
		shopGUI.showInventory(show);
	}
	
	/**
	 * @return (int) the index corresponding to the current item
	 */
	protected int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * @return (int) inventory size
	 */
	protected int getInventorySize() {
		return inventory.getSize();
	}
	
	/**
	 * @param index : new current index
	 */
	protected void setCurrentIndex(int index) {
		currentIndex = index;
	}
	
	/**
	 * asks to buy an item and places it in the anInventory if it is purchased
	 * @param anInventory inventory
	 */
	protected void buyItem(TWICInventory anInventory) {
		int price = 0;
		if(state == State.LIVE)
			price = currentItem.getPrice();
		
		if(currentItem == null) return;
		
		if(anInventory.getMoney() >= price && anInventory.addItem(currentItem, 1)) {
			anInventory.addMoney(-price);
			inventory.removeItem(currentItem, 1);
			shopGUI.setBuy();
		}
	}
	
	@Override
	public boolean possess(InventoryItem item) {
		return inventory.isInInventory(item);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(state == State.LIVE)
			sprite.draw(canvas);
		else if(state == State.TO_DEAD)
			deadAnim.draw(canvas);
		
		shopGUI.draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		if(state == State.TO_DEAD) {
			deadAnim.update(deltaTime);
			
			if(deadAnim.isCompleted()) {
				state = State.DEAD;
				getOwnerArea().leaveAreaCells(this, Collections.singletonList(getCurrentMainCellCoordinates()));
			}
		}
		
		currentItem = (TWICItem) inventory.getItem(currentIndex);
		
		int price = 0;
		if(state == State.LIVE && currentItem != null)
			price = currentItem.getPrice();
		
		if(inventory.getSize() <= 0) {
			showInventory(false);
			return;
		}
		
		if(currentItem != null)
			shopGUI.update(currentItem, price);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		collection.add(getCurrentMainCellCoordinates());
		if(state == State.LIVE)
			collection.add(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
		return collection;
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
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}
	
	/**
	 * kill the merchant 
	 */
	public void hurted() {
		state = State.TO_DEAD;
	}
	
	/**
	 * @return true if the merchant is living
	 */
	protected boolean isLiving() {
		return (state == State.LIVE);
	}
	
	private enum State {
		LIVE,
		TO_DEAD,
		DEAD
	}

}
