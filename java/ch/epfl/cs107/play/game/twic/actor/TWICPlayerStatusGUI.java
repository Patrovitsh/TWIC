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

public class TWICPlayerStatusGUI implements Graphics {
	
	public static final float DEPTH = 10000f;
	private static final int NB_SLOT = 8;
	
	private final ImageGraphics gearDisplay;
	private ImageGraphics itemDisplay;
	
	private final ImageGraphics gearSubItem;
	private final ImageGraphics subItem;
	private TextGraphics[] textQuantity;
	
	private ImageGraphics[] heartsDisplay;
	
	private final ImageGraphics coinsDisplay;
	private ImageGraphics[] digitsDisplay;
	
	private final ImageGraphics inventoryDisplay;
	private final ImageGraphics inventoryEmptySlot;

	private final Animation slotAnimation;
	
	private final int maxHP;
	private TWICItem currentItem;

	private boolean showInventory = false;
	private TWICInventory myInventory;
	
	/**
	 * Default ARPGPlayerStatusGUI Constructor
	 * @param hp (float), The current HP
	 * @param maxHP (int), The max HP
	 * @param currentItem (ARPGItem), The current item
	 * @param money (int), The money
	 */
	public TWICPlayerStatusGUI(float hp, int maxHP, TWICItem currentItem, int money) {
		this.maxHP = maxHP;
		
		gearDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"), 1.5f, 1.5f, 
				new RegionOfInterest(0, 0, 32, 32), Vector.ZERO, 1, DEPTH);
		coinsDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/coinsDisplay"), 3.5f, 1.75f,
				new RegionOfInterest(0, 0, 64, 32), Vector.ZERO, 1, DEPTH);
		inventoryDisplay = new ImageGraphics(ResourcePath.getSprite("personalAdds/inventory.background"), 13f, 9f,
				new RegionOfInterest(0, 0, 240, 240), Vector.ZERO, 1, DEPTH);
		inventoryEmptySlot = new ImageGraphics(ResourcePath.getSprite("zelda/inventory.slot"), 3f, 2f,
				new RegionOfInterest(0, 0, 64, 64), Vector.ZERO, 1, DEPTH);


		Sprite[] selectedSprite = new Sprite[2];
		for (int i = 0; i < selectedSprite.length; ++i)
			selectedSprite[i] = new Sprite("zelda/inventory.selector", 3f, 2f, null, new RegionOfInterest(i * 64, 0, 64, 64), 
					Vector.ZERO, 1, DEPTH);
		
		slotAnimation = new Animation(8, selectedSprite);
		
		update(hp, currentItem, money);
		
		// For Arrows
		gearSubItem = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"), 1.5f, 1.5f, 
				new RegionOfInterest(0, 0, 32, 32), Vector.ZERO, 1, DEPTH-1);
		
		TWICItem.ARROW.setSprite(0.62f, 0.62f);
		subItem = TWICItem.ARROW.getSprite();
		subItem.setDepth(DEPTH);
	}

	@Override
	public void draw(Canvas canvas) {
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2)); 
		
		for(int i = 0; i < maxHP; ++i) {
			heartsDisplay[i].setAnchor(anchor.add(new Vector(1.75f + (i), height - 1.5f)));
			heartsDisplay[i].draw(canvas);
		}
		
		gearDisplay.setAnchor(anchor.add(new Vector(0, height - 1.75f)));
		gearDisplay.draw(canvas);
		
		if(currentItem == TWICItem.ARROW_HEAD_RESEARCHER)
			itemDisplay.setAnchor(anchor.add(new Vector(0.28f, height - 2.10f)));
		else
			itemDisplay.setAnchor(anchor.add(new Vector(0.39f, height - 1.36f)));
		itemDisplay.draw(canvas);
		
		coinsDisplay.setAnchor(anchor.add(new Vector(0.1f, 0)));
		coinsDisplay.draw(canvas);
		
		for(int i = 0; i < 3; ++i) {
			digitsDisplay[i].setAnchor(anchor.add(new Vector(1.4f + i*0.6f, 0.53f)));
			digitsDisplay[i].draw(canvas);
		}
		
		switch(currentItem) {
		case BOW : showArrowQuantity(canvas, anchor, height);
			break;
		case BOMB: showBombQuantity(canvas, anchor, height);
			break;
		default : break;
		}
		
		if(showInventory) 
			displayInventory(canvas);
		
	}
	
	/**
	 * display inventory
	 */
	private void displayInventory(Canvas canvas) {
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));
		Vector slotVector = new Vector(1.5f, 6.6f);
		
		inventoryDisplay.setAnchor(anchor.add(new Vector(1f, 3f)));
		inventoryDisplay.setDepth(DEPTH);
		inventoryDisplay.draw(canvas);
		
		int indexItem = 0;
		TWICItem item;
		
		do {
			++indexItem;
			item = (TWICItem) myInventory.getItem(indexItem);
		} while (item != currentItem);

		int currentIndexItem = indexItem;
		
		for (int i = 1; i <= NB_SLOT; ++i) {
			Vector slotAnchor = anchor.add(slotVector).add(new Vector((i-1) / 2 * 3f, -(i-1) % 2 * 2.6f));
			//Slot's draws
			if (i == currentIndexItem) {
				slotAnimation.setAnchor(slotAnchor);
				slotAnimation.update(0f);
				slotAnimation.draw(canvas);
				
			} else {
				inventoryEmptySlot.setAnchor(slotAnchor);
				inventoryEmptySlot.draw(canvas);
			}
			
			//Object's draws
			TWICItem myItem = (TWICItem) myInventory.getItem(i);
			if (myItem == null) continue;
			Sprite itemSprite = (myItem == TWICItem.CASTLE_KEY) ? new Sprite("zelda/key", 0.75f, 0.75f, null, new RegionOfInterest(0, 0, 16, 16), 
					Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH+1) : myItem.getSprite();
			
			Vector itemAnchor = slotAnchor.add(new Vector(1f, 0.6f));
			if (myItem == TWICItem.ARROW_HEAD_RESEARCHER)
				itemAnchor = slotAnchor.add(new Vector(1f, 0f));
			
			
			itemSprite.setAnchor(itemAnchor);
			itemSprite.setDepth(DEPTH+2);
			itemSprite.draw(canvas);

			int itemQuantity = myInventory.getQuantity(myItem);
			TextGraphics myQuantity = new TextGraphics(Integer.toString(itemQuantity), 0.6f, (itemQuantity < 6 && (myItem == TWICItem.BOMB || myItem == TWICItem.ARROW)) ? Color.RED : Color.BLACK);
			Vector quantityAnchor = slotAnchor.add(new Vector(1.85f, 0.25f));
			
			myQuantity.setAnchor(quantityAnchor);
			myQuantity.setDepth(DEPTH + 3);
			myQuantity.draw(canvas);
		}
	}
	
	
	public void showInventory(TWICInventory inventory) {
		showInventory = !showInventory;
		myInventory = inventory;
	}
	
	/**
	 * Show the quantity of arrow
	 * @param canvas (Canvas), Not null
	 * @param anchor (Vector), Not null
	 * @param height (float), Not null
	 */
	private void showArrowQuantity(Canvas canvas, Vector anchor, float height) {
		gearSubItem.setAnchor(anchor.add(new Vector(0.5f, height - 2.75f)));
		gearSubItem.draw(canvas);
		
		subItem.setAnchor(anchor.add(new Vector(0.86f, height - 2.25f)));
		subItem.draw(canvas);
		
		for(int i = 0; i < textQuantity.length; ++i) {
			textQuantity[i].setAnchor(anchor.add(new Vector(1.33f-i*0.17f, height - 2.36f)));
			textQuantity[i].setDepth(DEPTH+2);
			textQuantity[i].draw(canvas);
		}
	}
	
	/**
	 * Show the quantity of bombs
	 * @param canvas (Canvas), Not null
	 * @param anchor (Vector), Not null
	 * @param height (float), Not null
	 */
	private void showBombQuantity(Canvas canvas, Vector anchor, float height) {
		for(int i = 0; i < textQuantity.length; ++i) {
			textQuantity[i].setAnchor(anchor.add(new Vector(0.9f-i*0.17f, height - 1.36f)));
			textQuantity[i].setDepth(DEPTH+2);
			textQuantity[i].draw(canvas);
		}
	}
	
	/**
	 * Update the ARPGPlayerStatusGui
	 */
	protected void update(float hp, TWICItem currentItem, int money) {
		this.currentItem = currentItem;
		viewHP(hp);
		viewCurrentItem();
		viewMoney(money);
	}
	
	/**
	 * Update the ARPGPlayerStatusGui
	 */
	protected void update(float hp, TWICItem currentItem, int money, int quantity) {
		update(hp, currentItem, money);
		
		int chiffre_1 = quantity / 10;
		int chiffre_2 = (quantity - chiffre_1 * 10);
		
		if(chiffre_1 == 0) 
			textQuantity = new TextGraphics[1];
		else {
			textQuantity = new TextGraphics[2];
			textQuantity[1] = new TextGraphics(Integer.toString(chiffre_1), 0.3f, Color.WHITE);
		}
		textQuantity[0] = new TextGraphics(Integer.toString(chiffre_2), 0.3f, Color.WHITE);
		
		if(quantity <= 5) 
			textQuantity[0].setFillColor(Color.RED);
	}
	
	/**
	 * Display the number of heart in the status
	 * @param hp (float), The player's HP
	 */
	private void viewHP(float hp) {
		heartsDisplay = new ImageGraphics[maxHP];
		
		for(int i = 0; i < (int)hp; ++i) {
			heartsDisplay[i] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1f, 1f,
					new RegionOfInterest(32, 0, 16, 16), Vector.ZERO, 1, DEPTH);
		}
		
		if(hp == maxHP) return;
		
		if(hp % 1 != 0) heartsDisplay[(int)hp] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 
				1f, 1f, new RegionOfInterest(16, 0, 16, 16), Vector.ZERO, 1, DEPTH);
		else heartsDisplay[(int)hp] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 
				1f, 1f, new RegionOfInterest(0, 0, 16, 16), Vector.ZERO, 1, DEPTH);
		
		for(int i = (int)(hp) + 1; i < maxHP; ++i) 
			heartsDisplay[i] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1f, 1f,
					new RegionOfInterest(0, 0, 16, 16), Vector.ZERO, 1, DEPTH);
	}
	
	/**
	 * Display the current item
	 */
	private void viewCurrentItem() {
		if(currentItem == TWICItem.ARROW_HEAD_RESEARCHER) {
			itemDisplay = currentItem.getSprite();
			return;
		} else if(currentItem == TWICItem.CASTLE_KEY) {
			itemDisplay = new Sprite("zelda/key", 0.75f, 0.75f, null, new RegionOfInterest(0, 0, 16, 16), 
					Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH+1);
			return;
		}
		currentItem.setSprite(0.75f, 0.75f);
		itemDisplay = currentItem.getSprite();
	}
	
	/**
	 * Display the money
	 * @param money (int)
	 */
	private void viewMoney(int money) {
		digitsDisplay = new ImageGraphics[3];
		int chiffre_1 = money / 100;
		int chiffre_2 = (money - chiffre_1 * 100) / 10;
		int chiffre_3 = money - chiffre_1 * 100 - chiffre_2 * 10;
		digitsDisplay[0] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.75f, 0.75f,
				getRegion(chiffre_1), Vector.ZERO, 1, DEPTH+1);
		digitsDisplay[1] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.75f, 0.75f,
				getRegion(chiffre_2), Vector.ZERO, 1, DEPTH+1);
		digitsDisplay[2] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.75f, 0.75f,
				getRegion(chiffre_3), Vector.ZERO, 1, DEPTH+1);
	}
	
	/**
	 * @return the RegionOfInterest corresponding to the digit
	 */
	private RegionOfInterest getRegion(int chiffre) {
		switch (chiffre) {
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
