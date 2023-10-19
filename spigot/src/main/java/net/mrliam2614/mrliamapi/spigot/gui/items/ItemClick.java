package net.mrliam2614.mrliamapi.spigot.gui.items;

import net.mrliam2614.mrliamapi.spigot.gui.handler.PlayerGui;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ItemClick {
    void onClick(InventoryClickEvent event, PlayerGui playerGui);
}
