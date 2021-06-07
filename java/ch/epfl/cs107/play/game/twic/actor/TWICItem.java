package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;

public enum TWICItem implements InventoryItem {
	
	ARROW ("Arrow", 0.f, 20, "zelda/arrow.icon"),
	SWORD ("Sword", 0.f, 100, "zelda/sword.icon"),
	STAFF ("Staff", 0.f, 300, "zelda/staff_water.icon"), 
	BOW ("Bow", 0.f, 250, "zelda/bow.icon"),
	BOMB ("Bomb", 0.f, 50, "zelda/bomb"),
	CASTLE_KEY ("CastleKey", 0.f, 250, "zelda/key"),
	ARROW_HEAD_RESEARCHER ("ArrowHeadResearcher", 0.f, 400, "personalAdds/magicArrowIcon"),
	HEART ("Heart", 0, 75, "zelda/heart");
	
	private final String name;
	private final float weight;
	private final int price;
	private Sprite sprite;
	
	/**
	 * Default ARPGItem Constructor
	 * @param name (String), The name of the item
	 * @param weight (float), His weight
	 * @param price (int), His price
	 * @param spriteName (String), His sprite's name
	 */
	TWICItem(String name, float weight, int price, String spriteName) {
		this.name = name;
		this.weight = weight;
		this.price = price;
		sprite = new Sprite(spriteName, 1f, 1f, null, new RegionOfInterest(0, 0, 16, 16), 
				Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH+1);
		if(name.equals("ArrowHeadResearcher"))
			sprite = new Sprite("personalAdds/magicArrowIcon", 0.96f, 1.6f, null,
					new RegionOfInterest(0,0,96, 160),
					Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH+1);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getWeight() {
		return weight;
	}

	@Override
	public int getPrice() {
		return price;
	}
	
	/**
	 * Set the width and the height of the item's sprite
	 * @param width (float), Not null
	 * @param height (float), Not null
	 */
	protected void setSprite(float width, float height) {
		sprite.setWidth(width);
		sprite.setHeight(height);
	}
	
	/**
	 * Get an item's sprite
	 * @return (Sprite), His sprite
	 */
	protected Sprite getSprite() {
		return sprite;
	}

}
