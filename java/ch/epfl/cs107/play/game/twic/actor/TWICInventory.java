package ch.epfl.cs107.play.game.twic.actor;

import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;

public class TWICInventory extends Inventory {
	private int money;
	private int fortune;
	
	/**
	 * Default ARPG Inventory Constructor
	 * @param weight (float), not null
	 * @param money (int), not null
	 */
	public TWICInventory (float weight, int money) {
		super(weight);
		this.money = money;
		this.fortune = money + getPrices();
	}
	
	/**
	 * Set the global fortune
	 */
	private void setFortune() {
		fortune = money + getPrices();
	}
	
	/**
	 * Get the money of the inventory
	 * @return (int), The money
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * Update and returns the global fortune
	 * @return (int), The fortune
	 */
	public int getFortune() {
		setFortune();
		return fortune;
	}

	/**
	 * Add money to the inventory and update the fortune
	 * @param nb (int), The money to add, not null
	 */
	protected void addMoney(int nb) {
		money += nb;
		setFortune();
	}
	
	@Override
	protected boolean addItem(InventoryItem item, int nb) {
		return super.addItem(item, nb);
	}
	
	@Override
	protected boolean removeItem(InventoryItem item, int nb) {
		return super.removeItem(item, nb);
	}
	
	@Override 
	protected InventoryItem getItem(int index) {
		return super.getItem(index);
	}
	
	@Override
	protected int getQuantity(InventoryItem item) {
		return super.getQuantity(item);
	}
	
	@Override
	protected int getSize() {
		return super.getSize();
	}

}
