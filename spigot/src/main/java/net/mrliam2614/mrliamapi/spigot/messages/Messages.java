package net.mrliam2614.mrliamapi.spigot.messages;

import net.kyori.adventure.text.Component;
import net.mrliam2614.mrliamapi.utils.MrliamColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messages {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(MrliamColor.colorize(message));
    }
    public static void sendMessage(Player player, Component component){
        player.sendMessage(component);
    }
}
