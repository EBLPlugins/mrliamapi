package net.mrliam2614.mrliamapi.spigot.gui.listener;

import net.mrliam2614.mrliamapi.spigot.gui.GuiManager;
import net.mrliam2614.mrliamapi.spigot.gui.items.ItemClick;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryListener implements Listener {
    private GuiManager guiManager;

    public InventoryListener() {
        this.guiManager = GuiManager.getGuiManagerInstance();
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return; // If the event is not a player, return (do nothing
        Player p = (Player) event.getWhoClicked();

        if (!isPlayerInInventory(p)) return; // If the player is not in an inventory, return (do nothing
        event.setCancelled(true);

        if (event.getClickedInventory() == null)
            return;

        if (event.getClickedInventory().getHolder() instanceof Player)
            return;

        ItemClick itemClick = guiManager.click(p, event.getSlot());
        if (itemClick != null) {
            itemClick.onClick(event, guiManager.getPlayerGui(p));
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return; // If the event is not a player, return (do nothing
        Player p = (Player) event.getPlayer();

        if (!isPlayerInInventory(p)) return; // If the player is not in an inventory, return (do nothing
        guiManager.closeGui(p, false);
    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return; // If the event is not a player, return (do nothing
        Player p = (Player) event.getWhoClicked();

        event.setCancelled(true);
    }

    private boolean isPlayerInInventory(Player p) {
        return guiManager.hasPlayerOpenGui(p);
    }
}
