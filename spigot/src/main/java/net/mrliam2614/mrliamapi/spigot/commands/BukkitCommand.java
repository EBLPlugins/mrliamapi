package net.mrliam2614.mrliamapi.spigot.commands;

import lombok.NonNull;
import net.mrliam2614.mrliamapi.spigot.commands.commands.AdvancedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BukkitCommand extends Command {
    private final AdvancedCommand executor;

    protected BukkitCommand(@NonNull String name, @NonNull AdvancedCommand executor) {
        super(name);
        this.executor = executor;
    }

    @Override
    public boolean execute(@NonNull CommandSender sender, @NonNull String commandLabel, @NonNull String[] args) {
        executor.execute(sender, args);
        return false;
    }
}
