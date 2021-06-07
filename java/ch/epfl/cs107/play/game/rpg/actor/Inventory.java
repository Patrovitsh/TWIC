package ch.epfl.cs107.play.game.rpg.actor;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
	private float maxWeight;
	private Map<InventoryItem, Integer> inventory;
	
	protected Inventory(float maxWeight) {
		this.maxWeight = maxWeight;
		inventory = new HashMap<>();
	}
	
	public boolean isInInventory(InventoryItem item) {
		for(InventoryItem unItem : inventory.keySet()) {
			if(unItem.equals(item)) return true;
		}
		return false;
	}
	
	protected boolean addItem(InventoryItem item, int nb) {
		if(getWeightOfInventory() + item.getWeight() * nb > maxWeight) return false;
		if(isInInventory(item)) {
			int valeur = inventory.remove(item);
			inventory.put(item, valeur + nb);
		} else {
			inventory.put(item, nb);
		}
		return true;
	}
	
	protected boolean removeItem(InventoryItem item, int nb) {
		if(isInInventory(item)) {
			int valeur = inventory.get(item) - nb;
			if(valeur > 0) {
				inventory.remove(item);
				inventory.put(item, valeur);
				return true;
			} else if(valeur == 0) {
				inventory.remove(item);
				return true;
			}
		}
		return false;
	}
	
	private float getWeightOfInventory() {
		float weightTotal = 0;
		for(InventoryItem unItem : inventory.keySet()) {
			weightTotal += unItem.getWeight();
		}
		return weightTotal;
	}
	
	protected int getPrices() {
		int prices = 0;
		for(InventoryItem unItem : inventory.keySet()) {
			prices += unItem.getPrice() * inventory.get(unItem);
		}
		return prices;
	}
	
	protected InventoryItem getItem(int index) {
		int cpt = 1;
		for(InventoryItem unItem : inventory.keySet()) {
			if(index == cpt) return unItem;
			++cpt;
		}
		return null;
	}
	
	protected int getQuantity(InventoryItem item) {
		if(isInInventory(item))
			return inventory.get(item);
		return 0;
	}
	
	protected int getSize() {
		return inventory.size();
	}
	
	public interface Holder {
		public boolean possess(InventoryItem item);
	}
	
}
