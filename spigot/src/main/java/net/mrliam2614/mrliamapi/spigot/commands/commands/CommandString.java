package net.mrliam2614.mrliamapi.spigot.commands.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class CommandString extends AdvancedCommand {
    public abstract List<String> tabComplete(CommandSender sender, String[] args);
}
