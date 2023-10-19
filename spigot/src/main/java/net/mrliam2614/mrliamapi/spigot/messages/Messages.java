package net.mrliam2614.mrliamapi.spigot.messages;

import net.mrliam2614.mrliamapi.utils.MrliamColor;
import org.bukkit.command.CommandSender;

public class Messages {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(MrliamColor.colorize(message));
    }
}
