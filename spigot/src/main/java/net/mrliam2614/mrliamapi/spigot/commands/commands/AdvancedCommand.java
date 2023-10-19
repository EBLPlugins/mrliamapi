package net.mrliam2614.mrliamapi.spigot.commands.commands;

import lombok.Getter;
import lombok.Setter;
import net.mrliam2614.mrliamapi.spigot.commands.interfaces.CommandInterface;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class AdvancedCommand implements CommandInterface {
    private String name;
    private String permission;
    @Setter
    private String description;
    private String[] aliases;
    private boolean onlyPlayers;

    private List<AdvancedCommand> args;
    @Setter
    private String usage;
    private boolean requireArgs;
    private boolean acceptArgs;
    @Setter
    private String noPermissionMessage;

    protected AdvancedCommand() {
        loadProps();
        init();
    }

    @Override
    public void init() {
    }

    /**
     * Load the properties from the annotation
     */
    private void loadProps() {
        CommandMeta commandMeta = this.getClass().getAnnotation(CommandMeta.class);

        if (commandMeta == null) {
            return;
        }

        this.name = commandMeta.name();
        this.permission = commandMeta.permission();
        this.description = commandMeta.description();
        this.aliases = commandMeta.aliases();
        this.onlyPlayers = commandMeta.onlyPlayers();
        this.usage = commandMeta.usage();
        this.requireArgs = commandMeta.requireArgs();
        this.acceptArgs = commandMeta.acceptArgs();
        this.noPermissionMessage = commandMeta.noPermissionMessage();

        this.args = new ArrayList<>();
    }

    /**
     * Add a sub command to this command
     *
     * @param command The argument - sub command to add
     */
    @Override
    public final void addArg(AdvancedCommand command) {
        if (args == null) {
            args = new ArrayList<>();
        }
        args.add(command);
    }

    /**
     * Add multiple sub commands to this command
     *
     * @param commands The arguments - sub commands to add
     */
    @Override
    public final void addArgs(AdvancedCommand... commands) {
        if (args == null) {
            args = new ArrayList<>();
        }
        args.addAll(Arrays.asList(commands));
    }


    /**
     * Get a sub command by name
     *
     * @param name The name of the sub command
     * @return The sub command
     */
    @Override
    public final AdvancedCommand getArg(String name) {
        for (AdvancedCommand command : args) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Get all sub commands
     *
     * @return The sub commands
     */
    @Override
    public final List<AdvancedCommand> getArgs() {
        return args;
    }

    /**
     * Get the string args of this command
     *
     * @return The string args
     * <p>
     * /**
     * Check if this command has sub commands
     * @return If this command has sub commands
     */
    @Override
    public final boolean hasArgs() {
        if (args == null) {
            return false;
        }
        return !args.isEmpty();
    }

    /**
     * Check if the sender has permission to use this command
     *
     * @param sender The sender of the command
     * @return If the sender has permission to use this command
     */
    @Override
    public final boolean hasPermission(CommandSender sender) {
        if (permission == null)
            return true;
        if (permission.isEmpty())
            return true;
        return sender.hasPermission(permission);
    }
}
