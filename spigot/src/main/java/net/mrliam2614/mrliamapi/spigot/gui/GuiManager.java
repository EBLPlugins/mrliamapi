package net.mrliam2614.mrliamapi.spigot.gui;

import lombok.Getter;
import net.mrliam2614.mrliamapi.spigot.gui.handler.BaseGui;
import net.mrliam2614.mrliamapi.spigot.gui.handler.PlayerGui;
import net.mrliam2614.mrliamapi.spigot.gui.items.InventoryItem;
import net.mrliam2614.mrliamapi.spigot.gui.items.ItemClick;
import net.mrliam2614.mrliamapi.spigot.gui.listener.InventoryListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class GuiManager {
    @Getter
    private static GuiManager guiManagerInstance;
    private HashMap<Player, PlayerGui> playerGui;
    public GuiManager(JavaPlugin plugin) {
        this.playerGui = new HashMap<>();
        guiManagerInstance = this;
        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    public PlayerGui openGui(Player player, BaseGui baseGui) {
        PlayerGui playerGui = new PlayerGui(baseGui);
        if(this.playerGui.containsKey(player)) {
            closeGui(player, true);
        }
        this.playerGui.put(player, playerGui);
        player.openInventory(playerGui.getInventory());
        return playerGui;
    }

    public void closeGui(Player player, boolean close) {
        this.playerGui.remove(player);
        if (close) player.closeInventory();
    }

    public void closeAll() {
        for (Player player : playerGui.keySet()) {
            player.closeInventory();
        }
        playerGui.clear();
    }

    public PlayerGui getPlayerGui(Player player) {
        return playerGui.get(player);
    }

    public ItemClick click(Player player, int slot) {
        InventoryItem item = playerGui.get(player).clickedItem(slot);
        if (item != null) {
            return item.getItemClick();
        }
        return null;
    }

    public boolean hasPlayerOpenGui(Player player) {
        return playerGui.containsKey(player);
    }
}
