package net.mrliam2614.mrliamapi.spigot.gui.items;

import net.mrliam2614.mrliamapi.spigot.gui.handler.PlayerGui;
import org.bukkit.inventory.ItemStack;

public class InventoryItem {
    private ItemClick itemClick;
    private ItemStack itemStack;
    private DisplayCondition displayCondition = null;
    private DisplayInfo displayInfo = null;

    public static InventoryItem create() {
        return new InventoryItem();
    }

    public InventoryItem itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public InventoryItem itemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
        return this;
    }

    public InventoryItem displayCondition(DisplayCondition displayCondition) {
        this.displayCondition = displayCondition;
        return this;
    }

    public InventoryItem displayInfo(DisplayInfo displayInfo) {
        this.displayInfo = displayInfo;
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public InventoryItem setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemClick getItemClick() {
        return itemClick;
    }

    public boolean shouldDisplay(PlayerGui gui) {
        return displayCondition == null || displayCondition.shouldDisplay(gui);
    }

    public boolean updateDisplay(PlayerGui gui) {
        if (displayInfo != null) {
            int preHash = itemStack.hashCode();
            displayInfo.displayInfo(gui, this);
            int postHas = itemStack.hashCode();
            return preHash != postHas;
        }
        return false;
    }
}
