package net.mrliam2614.mrliamapi.spigot.gui.handler;

import lombok.Getter;
import net.mrliam2614.mrliamapi.spigot.gui.GuiManager;
import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;
import net.mrliam2614.mrliamapi.spigot.gui.panels.InventoryBorders;
import net.mrliam2614.mrliamapi.spigot.gui.panels.InventoryFixed;
import net.mrliam2614.mrliamapi.spigot.gui.panels.InventoryPageable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PlayerGui {
    Inventory inventory;
    private BaseGui baseGui;
    @Getter
    private int page;

    private InventoryPageable inventoryPageable;

    public PlayerGui(BaseGui baseGui) {
        this.baseGui = baseGui;
        this.page = 0;

        initialize();
    }

    private void initialize() {
        Player holder = null;
        if (baseGui.isShared()) {
            holder = Bukkit.getPlayer(baseGui.getTitle().toLowerCase());
        }

        inventory = Bukkit.createInventory(holder, 9 * baseGui.getRows(), baseGui.getTitle());

        loadItems();
        if(baseGui.isLiveUpdate()){
            Bukkit.getScheduler().runTaskTimerAsynchronously(GuiManager.getGuiManagerInstance().getCorePlugin(), this::checkUpdate, 0, 5);
        }
    }

    private void checkUpdate() {
        if(inventoryPageable.hashCode() != baseGui.getInventoryPageable().hashCode()){
            reloadItems();
        }
    }

    public void reloadItems() {
        loadItems();
    }

    private void loadItems() {
        clearFixed();
        setFiller();
        setBorders();
        loadFixed();
        loadPageable();
    }

    private void setFiller() {
        if (baseGui.getItemFiller() == null) return;
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, baseGui.getItemFiller().getItemStack());
        }
    }

    private void clearFixed() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isInsidePageableArea(i)) continue;
            if (isInsideBorder(i)) continue;
            inventory.setItem(i, null);
        }
    }

    private void loadPageable() {
        inventoryPageable = baseGui.getInventoryPageable();
        if (inventoryPageable == null) return;
        List<InventoryItem> pageItems = inventoryPageable.getPageItems(page);
        for (int i = 0; i < inventoryPageable.getRows(); i++) {
            for (int j = 0; j < inventoryPageable.getColumns(); j++) {
                int slot = inventoryPageable.getStartSlot() + (i * 9) + j;
                int index = (i * inventoryPageable.getColumns()) + j;
                InventoryItem item = index < pageItems.size() ? pageItems.get(index) : null;
                if (item == null) {
                    inventory.setItem(slot, null);
                    continue;
                }
                ;

                boolean shouldDisplay = item.shouldDisplay(this);
                if (shouldDisplay) {
                    item.updateDisplay(this);
                    inventory.setItem(slot, item.getItemStack());
                }
            }
        }
    }

    private void loadFixed() {
        InventoryFixed inventoryFixed = baseGui.getInventoryFixed();
        if (inventoryFixed == null) return;
        for (int i = 0; i < inventory.getSize(); i++) {
            InventoryItem item = inventoryFixed.getItem(i);
            if (item == null) continue;

            boolean shouldDisplay = item.shouldDisplay(this);
            if (shouldDisplay && !isInsidePageableArea(i)) {
                item.updateDisplay(this);
                inventory.setItem(i, item.getItemStack());
            }
        }
    }

    private void setBorders() {
        InventoryBorders inventoryBorders = baseGui.getInventoryBorders();
        if (inventoryBorders == null) return;
        for (int i = 0; i < 9; i++) {
            //First Row
            inventory.setItem(i, inventoryBorders.getBorderItem().getItemStack());
            //Last Row
            inventory.setItem(i + ((baseGui.getRows() - 1) * 9), inventoryBorders.getBorderItem().getItemStack());
        }
        for (int i = 0; i < baseGui.getRows(); i++) {
            //First Column
            inventory.setItem(i * 9, inventoryBorders.getBorderItem().getItemStack());
            //Last Column
            inventory.setItem((i * 9) + 8, inventoryBorders.getBorderItem().getItemStack());
        }
    }

    private boolean isInsidePageableArea(int slot) {
        InventoryPageable inventoryPageable = baseGui.getInventoryPageable();
        if (inventoryPageable == null) return false;
        int rows = baseGui.getRows();
        int pageableRows = inventoryPageable.getRows();

        int columns = 9;
        int pageableColumns = inventoryPageable.getColumns();

        int startSlot = inventoryPageable.getStartSlot();

        for (int i = 0; i < pageableRows; i++) {
            //Controlla ogni riga della "pagina"
            for (int j = 0; j < pageableColumns; j++) {
                //Controlla ogni colonna della "pagina"
                int slotToCheck = startSlot + (i * 9) + j;
                if (slotToCheck == slot) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInsideBorder(int slot) {
        InventoryBorders inventoryBorders = baseGui.getInventoryBorders();
        if (inventoryBorders == null) return false;
        int rows = baseGui.getRows();
        int columns = 9;

        //Is slot is in first or last row or column return true
        int row = (slot + 1) / columns;
        int column = (slot + 1) % columns; //0 = last column

        if (row == 0 || row == rows - 1 || column == 0 || column == 1) {
            return true;
        }
        return false;
    }

    public InventoryItem clickedItem(int slot) {
        boolean isInsidePageableArea = isInsidePageableArea(slot);
        if (isInsidePageableArea) {
            return baseGui.getInventoryPageable().clickedItem(slot, page);
        } else {
            InventoryItem fixedItem = baseGui.getInventoryFixed().getItem(slot);
            if (fixedItem != null) {
                if (fixedItem.shouldDisplay(this)) {
                    return fixedItem;
                }
                if (isInsideBorder(slot)) {
                    return fixedItem;
                }
            }
        }
        return null;
    }

    public Inventory getInventory() {
        reloadItems();
        return inventory;
    }

    public boolean hasNext() {
        int maxPage = baseGui.getInventoryPageable().getPages();
        return page + 1 < maxPage;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public void nextPage() {
        if (hasNext()) {
            page++;
            loadItems();
        }
    }

    public void previousPage() {
        if (hasPrevious()) {
            page--;
            loadItems();
        }
    }
}