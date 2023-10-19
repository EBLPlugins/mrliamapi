package net.mrliam2614.mrliamapi.logger;

import org.bukkit.Bukkit;

public class ConsoleLogger {
    public static void warn(String message) {
        Bukkit.getLogger().warning(message);
    }

    public static void info(String message) {
        Bukkit.getLogger().info(message);
    }

    public static void severe(String message) {
        Bukkit.getLogger().severe(message);
    }

}
