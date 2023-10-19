package net.mrliam2614.mrliamapi.spigot.gui.panels;


import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;

//Items here can be overwritten by the InventoryFixed
public class InventoryBorders {
    private InventoryItem borderItem;

    public InventoryBorders(InventoryItem borderItem) {
        this.borderItem = borderItem;
    }

    public InventoryItem getBorderItem() {
        return borderItem;
    }
}
