package net.mrliam2614.mrliamapi.spigot.gui.panels;

import lombok.Getter;
import lombok.Setter;
import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;
import net.mrliam2614.mrliamapi.spigot.gui.panels.handlers.PageableItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryPageable {
    private int rows;
    private int columns;

    private int startSlot;

    private List<InventoryItem> items;

    @Setter
    private PageableItems pageableItems;


    public InventoryPageable(int rows, int columns, int startSlot) {
        this.rows = rows;
        this.columns = columns;
        this.startSlot = startSlot;
        items = new ArrayList<>();
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

    public InventoryPageable addItem(InventoryItem item) {
        this.items.add(item);
        return this;
    }

    public InventoryPageable clearItems() {
        this.items.clear();
        return this;
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

    public void updateItems() {
        if(pageableItems == null) return;
        items = pageableItems.updateItems();
    }

    public String objectHash(){
        int itemsHash = items.hashCode();
        int itemSize = items.size();
        int lastItem = (items.size() > 0) ? items.get(items.size()-1).hashCode() : 0;

        return ""+itemsHash+itemSize+lastItem;
    }
}

