package ch.epfl.cs107.play.game.twic.actor;

import java.awt.Color;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ShopStatusGUI implements Graphics {
	
	public static final float DEPTH = 10000f;
	private static final int NB_SLOT = 8;
	
	private ImageGraphics[] digitsDisplay;
	
	private final ImageGraphics inventoryDisplay;
	private final ImageGraphics inventoryEmptySlot;

	private final Animation slotAnimation;
	
	private TWICItem currentItem;
	private int currentIndexItem;
	private int price;
	
	private boolean showInventory = false;
	private final TWICInventory inventory;
	
	private boolean justBuy = false;
	
	public ShopStatusGUI(TWICInventory inventory, TWICItem currentItem, int currentIndex) {
		this.inventory = inventory;
		this.currentItem = currentItem;
		this.currentIndexItem = currentIndex;
		price = currentItem.getPrice();
		
		inventoryDisplay = new ImageGraphics(ResourcePath.getSprite("personalAdds/shop.background"),
				13f, 9f,
				new RegionOfInterest(0, 0, 240, 240), Vector.ZERO, 1, DEPTH);
		inventoryEmptySlot = new ImageGraphics(ResourcePath.getSprite("zelda/inventory.slot"), 3f, 2f,
				new RegionOfInterest(0, 0, 64, 64), Vector.ZERO, 1, DEPTH);


		Sprite[] selectedSprite = new Sprite[2];
		for (int i = 0; i < selectedSprite.length; ++i)
			selectedSprite[i] = new Sprite("zelda/inventory.selector", 3f, 2f, null,
					new RegionOfInterest(i * 64, 0, 64, 64),
					Vector.ZERO, 1, DEPTH);
		
		slotAnimation = new Animation(4, selectedSprite, false);
		
	}
	
	/**
	 * an item has just been purchased
	 */
	protected void setBuy() {
		justBuy = true;
	}
	
	/**
	 * @param show : true if you want to show the Inventory
	 */
	protected void showInventory(boolean show) {
		showInventory = show;
	}
	
	/**
	 * puts a new value on the current item and its price
	 */
	protected void update(TWICItem item, int price) {
		currentItem = item;
		this.price = price;
	}

	@Override
	public void draw(Canvas canvas) {
		if(!showInventory) return;
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));
		Vector slotVector = new Vector(1.5f, 6.6f);
		
		viewMoney(price);
		for(int i = 0; i < 3; ++i) {
			digitsDisplay[i].setAnchor(anchor.add(new Vector(10f + i*0.6f, 2.9f)));
			digitsDisplay[i].draw(canvas);
		}
		
		inventoryDisplay.setAnchor(anchor.add(new Vector(1f, 2f)));
		inventoryDisplay.setDepth(DEPTH);
		inventoryDisplay.draw(canvas);
		
		int indexItem = 0;
		TWICItem item;
		
		int cpt = NB_SLOT;
		do {
			++indexItem;
			item = (TWICItem) inventory.getItem(indexItem);
			--cpt;
		} while (item != currentItem && cpt >= 0);
		
		if(cpt < 0) 
			currentIndexItem = 1;
		else
			currentIndexItem = indexItem;
		
		for (int i = 1; i <= NB_SLOT; ++i) {
			Vector slotAnchor = anchor.add(slotVector).add(new Vector((i-1) / 2 * 3f, -(i-1) % 2 * 2.6f));
			//Slot's draws
			if (i == currentIndexItem) {
				slotAnimation.setAnchor(slotAnchor);
				slotAnimation.update(0f);
				if(!justBuy || slotAnimation.isCompleted()) {
					slotAnimation.reset();
					justBuy = false;
				}
				slotAnimation.draw(canvas);
				
			} else {
				inventoryEmptySlot.setAnchor(slotAnchor);
				inventoryEmptySlot.draw(canvas);
			}
			
			//Object's draws
			TWICItem myItem = (TWICItem) inventory.getItem(i);
			if (myItem == null) continue;
			Sprite itemSprite = (myItem == TWICItem.CASTLE_KEY) ? new Sprite("zelda/key", 0.75f, 0.75f, null, new RegionOfInterest(0, 0, 16, 16), 
					Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH+1) : myItem.getSprite();
			
			Vector itemAnchor = slotAnchor.add(new Vector(1f, 0.6f));
			if (myItem == TWICItem.ARROW_HEAD_RESEARCHER)
				itemAnchor = slotAnchor.add(new Vector(1f, 0f));
			
			
			itemSprite.setAnchor(itemAnchor);
			itemSprite.setDepth(DEPTH+2);
			itemSprite.draw(canvas);

			int itemQuantity = inventory.getQuantity(myItem);
			TextGraphics myQuantity = new TextGraphics(Integer.toString(itemQuantity), 0.6f, (itemQuantity < 6 && (myItem == TWICItem.BOMB || myItem == TWICItem.ARROW)) ? Color.RED : Color.BLACK);
			Vector quantityAnchor = slotAnchor.add(new Vector(1.85f, 0.25f));
			
			myQuantity.setAnchor(quantityAnchor);
			myQuantity.setDepth(DEPTH + 3);
			myQuantity.draw(canvas);
		}
		
	}
	
	/**
	 * allows to determine the digits of the amount of money to display them
	 */
	private void viewMoney(int money) {
		digitsDisplay = new ImageGraphics[3];
		int digit_1 = money / 100;
		int digit_2 = (money - digit_1 * 100) / 10;
		int digit_3 = money - digit_1 * 100 - digit_2 * 10;
		digitsDisplay[0] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.75f, 0.75f,
				getRegion(digit_1), Vector.ZERO, 1, DEPTH+1);
		digitsDisplay[1] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.75f, 0.75f,
				getRegion(digit_2), Vector.ZERO, 1, DEPTH+1);
		digitsDisplay[2] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.75f, 0.75f,
				getRegion(digit_3), Vector.ZERO, 1, DEPTH+1);
	}
	
	/**
	 * @return the RegionOfInterest corresponding to the digit
	 */
	private RegionOfInterest getRegion(int digit) {
		switch (digit) {
		case 1 : return new RegionOfInterest(0, 0, 16, 16);
		case 2 : return new RegionOfInterest(16, 0, 16, 16);
		case 3 : return new RegionOfInterest(32, 0, 16, 16);
		case 4 : return new RegionOfInterest(48, 0, 16, 16);
		case 5 : return new RegionOfInterest(0, 16, 16, 16);
		case 6 : return new RegionOfInterest(16, 16, 16, 16);
		case 7 : return new RegionOfInterest(32, 16, 16, 16);
		case 8 : return new RegionOfInterest(48, 16, 16, 16);
		case 9 : return new RegionOfInterest(0, 32, 16, 16);
		default : return new RegionOfInterest(16, 32, 16, 16);
		}
	}
	
}