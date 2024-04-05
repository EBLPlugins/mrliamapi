package net.mrliam2614.mrliamapi.spigot.gui.panels;

import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InventoryFixed {
    private HashMap<Integer, InventoryItem> items;

    public InventoryFixed() {
        this.items = new HashMap<>();
    }

    public InventoryFixed addItem(Integer slot, InventoryItem item) {
        this.items.put(slot, item);
        return this;
    }

    public InventoryFixed removeItem(Integer slot) {
        this.items.remove(slot);
        return this;
    }

    public InventoryFixed clearItems() {
        this.items.clear();
        return this;
    }

    public InventoryItem getItem(Integer slot) {
        return this.items.getOrDefault(slot, null);
    }

    private HashMap<Integer, InventoryItem> getItems() {
        return this.items;
    }

    public Set<Integer> itemSlots(){
        return items.keySet();
    }
}
