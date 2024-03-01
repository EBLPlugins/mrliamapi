package net.mrliam2614.mrliamapi.spigot.commands.commands;

import net.mrliam2614.mrliamapi.spigot.messages.Logger;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class CommandString extends AdvancedCommand {
    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Logger.error("A Command String should not be executed!");
    }
}
