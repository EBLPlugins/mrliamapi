package net.mrliam2614.mrliamapi.spigot.gui.panels;

public class EmptyInventoryPageable extends InventoryPageable{
    public EmptyInventoryPageable() {
        super(0, 0, 0);
    }

    @Override
    public void updateItems() {
        // Do nothing
    }
    // override other necessary methods to do nothing or return a default value
}
