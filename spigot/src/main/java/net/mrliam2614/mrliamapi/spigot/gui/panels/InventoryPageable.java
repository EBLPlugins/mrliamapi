package net.mrliam2614.mrliamapi.spigot.gui.panels;

import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryPageable {
    private int rows;
    private int columns;

    private int startSlot;

    private List<InventoryItem> items;


    public InventoryPageable(int rows, int columns, int startSlot) {
        this.rows = rows;
        this.columns = columns;
        this.startSlot = startSlot;
        items = new ArrayList<>();
    }

    public void addItem(InventoryItem item) {
        items.add(item);
    }

    public void removeItem(InventoryItem item) {
        items.remove(item);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public void clearItems() {
        items.clear();
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }

    public void insertItem(int index, InventoryItem item) {
        items.add(index, item);
    }

    public List<InventoryItem> getPageItems(int page) {
        int itemsPerPage = rows * columns;
        int startIndex = page * itemsPerPage;
        int endIndex = startIndex + itemsPerPage;
        if (endIndex > items.size()) {
            endIndex = items.size();
        }
        if (startIndex > endIndex) {
            startIndex = endIndex;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex < 0) {
            endIndex = 0;
        }
        return items.subList(startIndex, endIndex);
    }

    public int getPages() {
        int itemsPerPage = rows * columns;
        return (int) Math.ceil((double) items.size() / itemsPerPage);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public InventoryItem clickedItem(int slot, int page) {
        int pageableItemIndex = 0;
        forRow:
        for (int i = 0; i < rows; i++) {
            //Controlla ogni riga della "pagina"
            for (int j = 0; j < columns; j++) {
                //Controlla ogni colonna della "pagina"
                int slotToCheck = startSlot + (i * 9) + j;
                if (slotToCheck == slot) {
                    break forRow;
                }
                pageableItemIndex++;
            }
        }

        if(getPageItems(page).size() <= pageableItemIndex) return null;

        return getPageItems(page).get(pageableItemIndex);
    }
}

