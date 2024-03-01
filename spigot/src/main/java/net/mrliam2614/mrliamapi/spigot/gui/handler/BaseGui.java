package net.mrliam2614.mrliamapi.spigot.gui.handler;

import net.mrliam2614.mrliamapi.commons.exceptions.BadInventoryConfiguration;
import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;
import net.mrliam2614.mrliamapi.spigot.gui.panels.InventoryBorders;
import net.mrliam2614.mrliamapi.spigot.gui.panels.InventoryFixed;
import net.mrliam2614.mrliamapi.spigot.gui.panels.InventoryPageable;
import net.mrliam2614.mrliamapi.utils.MrliamColor;

public class BaseGui {
    private String title;
    private int rows;
    private boolean shared = false;
    private boolean closeable = true;
    private boolean liveUpdate = false;

    private InventoryItem itemFiller;
    private InventoryPageable inventoryPageable;
    private InventoryBorders inventoryBorders;
    private InventoryFixed inventoryFixed;

    private BaseGui(int rows) {
        this.rows = rows;
        if (rows < 1 || rows > 6) {
            throw new BadInventoryConfiguration("Rows must be between 1 and 6");
        }
    }

    public static BaseGui create(int rows) {
        return new BaseGui(rows);
    }

    public BaseGui title(String title) {
        this.title = MrliamColor.colorize(title);
        return this;
    }

    public BaseGui shared(boolean shared) {
        this.shared = shared;
        return this;
    }

    public BaseGui closeable(boolean closeable) {
        this.closeable = closeable;
        return this;
    }

    public BaseGui liveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }

    public BaseGui inventoryPageable(InventoryPageable inventoryPageable) {
        this.inventoryPageable = inventoryPageable;
        return this;
    }

    public BaseGui inventoryBorders(InventoryBorders inventoryBorders) {
        this.inventoryBorders = inventoryBorders;
        return this;
    }

    public BaseGui inventoryFixed(InventoryFixed inventoryFixed) {
        this.inventoryFixed = inventoryFixed;
        return this;
    }

    public BaseGui itemFiller(InventoryItem itemFiller) {
        this.itemFiller = itemFiller;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public boolean isShared() {
        return shared;
    }

    public boolean isCloseable() {
        return closeable;
    }

    public boolean isLiveUpdate() {
        return liveUpdate;
    }

    public InventoryPageable getInventoryPageable() {
        inventoryPageable.updateItems();
        return inventoryPageable;
    }

    public InventoryBorders getInventoryBorders() {
        return inventoryBorders;
    }

    public InventoryFixed getInventoryFixed() {
        return inventoryFixed;
    }

    public InventoryItem getItemFiller() {
        return itemFiller;
    }
}
