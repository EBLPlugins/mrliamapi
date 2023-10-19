package net.mrliam2614.mrliamapi.utils;

import org.bukkit.ChatColor;

public class MrliamColor {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
