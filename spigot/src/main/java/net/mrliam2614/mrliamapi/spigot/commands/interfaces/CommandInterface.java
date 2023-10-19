package net.mrliam2614.mrliamapi.spigot.commands.interfaces;

import net.mrliam2614.mrliamapi.spigot.commands.commands.AdvancedCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandInterface {
    void init();

    /**
     * Called when the command is executed
     *
     * @param sender The sender of the command
     * @param args   The arguments of the command
     */
    void execute(CommandSender sender, String[] args);

    void addArg(AdvancedCommand command);

    void addArgs(AdvancedCommand... commands);

    AdvancedCommand getArg(String name);

    List<AdvancedCommand> getArgs();

    boolean hasArgs();

    boolean hasPermission(CommandSender sender);

    String getName();

    String getPermission();

    String getDescription();

    void setDescription(String description);

    String[] getAliases();

    boolean isOnlyPlayers();

    String getUsage();

    void setUsage(String usage);

    boolean isRequireArgs();

    boolean isAcceptArgs();

    String getNoPermissionMessage();

    void setNoPermissionMessage(String noPermissionMessage);
}
