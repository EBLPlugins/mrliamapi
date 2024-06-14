package net.mrliam2614.mrliamapi.commands.interfaces;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.mrliam2614.mrliamapi.commands.AdvancedCommandHandler;
import net.mrliam2614.mrliamapi.commands.commands.AdvancedCommand;
import net.mrliam2614.mrliamapi.commands.commands.CommandString;
import net.mrliam2614.mrliamapi.commands.commands.NoArgsFunction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface CommandInterface extends SimpleCommand {
    void init();

    /**
     * Called when the command is executed
     */
    @Override
    default void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (hasPermission(sender)){
            if (args.length == 0) {
                execute(sender, args);
                return;
            }

            String cmdArg = args[0];
            AdvancedCommand cmd = getArgs().stream().filter(c -> c.getName().equalsIgnoreCase(cmdArg)).findFirst().orElse(null);

            if(cmd == null) {
                execute(sender, args);
                return;
            }

            if (!(sender instanceof Player)) {
                if (cmd.isOnlyPlayers()) {
                    sender.sendMessage(Component.text("§4Only players can execute this command!"));
                    return;
                }
            }

            calcCommand(sender, cmd, args, false);
        } else {
            sender.sendMessage(Component.text("§4You do not have permission to execute this command!"));
        }
    }


    /**
     * Calculates the arguments for the command
     *
     * @param sender The sender of the command
     * @param cmd    The command
     * @param args   The arguments
     */
    default void calcCommand(CommandSource sender, AdvancedCommand cmd, String[] args, boolean isMain) {
        if (cmd == null) return;

        if (!cmd.hasPermission(sender)) {
            sender.sendMessage(Component.text(cmd.getNoPermissionMessage().replaceAll("&", "§")));
            return;
        }

        String[] arguments;
        if (args.length == 1) {
            arguments = new String[0];
        } else {
            arguments = new String[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, args.length - 1);
        }
        AdvancedCommand commandArg = null;
        if (arguments.length > 0) {
            if (cmd.getArgs().isEmpty()){
                cmd.execute(sender, arguments);
            }

            boolean found = false;
            for (AdvancedCommand stringCommand : cmd.getArgs()) {
                if (!(stringCommand instanceof CommandString)) continue;

                found = true;

                List<String> tabComplete = ((CommandString) stringCommand).tabComplete(sender, args);
                if (tabComplete == null)
                    throw new IllegalArgumentException("BlazeCommandString cannot return null! (" + stringCommand.getClass().getName() + ")!");

                for (String s : tabComplete) {
                    if (s.equalsIgnoreCase(arguments[0])) {
                        if (arguments.length > 1) {
                            String nArg = arguments[1];
                            commandArg = stringCommand.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(nArg)).findAny().orElse(null);
                        }
                    }
                }
            }

            if (!found)
                commandArg = cmd.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(arguments[0])).findAny().orElse(null);
        }

        if (commandArg != null) {
            calcCommand(sender, commandArg, arguments, false);
        } else {
            if (!cmd.isAcceptArgs() && arguments.length > 0) {
                sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
                return;
            }
            if (cmd.isRequireArgs() && arguments.length == 0) {
                sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
                return;
            }

            if (isMain) {
                if (cmd.isEnabledFromMain()) {
                    cmd.execute(sender, args);
                } else
                    sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
            } else {
                cmd.execute(sender, args);
            }
        }
    }

    @Override
    default List<String> suggest(Invocation invocation) {
        CommandSource commandSender = invocation.source();
        String[] args = invocation.arguments();
        List<String> nextArgs = new ArrayList<>();
        if (!(commandSender instanceof Player)) {
            return nextArgs;
        }

        Player player = (Player) commandSender;

        if (args.length < 1) {
            for (AdvancedCommand bc : getArgs()) {
                if (bc instanceof CommandString) {
                    if(hasPermission(player))
                        return ((CommandString) bc).tabComplete(invocation.source(), invocation.arguments());
                }
                if(hasPermission(player))
                    nextArgs.add(bc.getName());
            }
            return nextArgs;
        }

        if (args.length == 1) {
            for (AdvancedCommand bc : getArgs()) {
                if (bc instanceof CommandString) {
                    if(hasPermission(player))
                        return ((CommandString) bc).tabComplete(invocation.source(), invocation.arguments());
                }
            }
        }

        String commandName = args[0];
        AdvancedCommand cmd = getArgs().stream().filter(c -> c.getName().equalsIgnoreCase(commandName)).findAny().orElse(null);
        if (args.length == 1) {
            List<AdvancedCommand> allCommands = getArgs().stream().filter(c -> c.getName().contains(commandName)).collect(Collectors.toList());
            for (AdvancedCommand cmda : allCommands) {
                if(cmda.hasPermission(player))
                    nextArgs.add(cmda.getName());
            }
            return nextArgs;
        }
        if (cmd == null) {
            return nextArgs;
        }
        List<String> getArgs = AdvancedCommandHandler.getInstance().calcArgs(player, cmd, args, new ArrayList<>());

        for (String gotArg : getArgs) {
            if (gotArg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                nextArgs.add(gotArg);
        }

        return nextArgs;
    }

    void execute(CommandSource sender, String[] args);

    void addArg(AdvancedCommand command);

    void addArgs(AdvancedCommand... commands);

    AdvancedCommand getArg(String name);

    List<AdvancedCommand> getArgs();

    boolean hasArgs();

    boolean hasPermission(CommandSource sender);

    String getName();

    String getPermission();

    String getDescription();

    void setDescription(String description);

    boolean isOnlyPlayers();

    String getUsage();

    void setUsage(String usage);

    boolean isRequireArgs();

    boolean isAcceptArgs();

    String getNoPermissionMessage();

    void setNoPermissionMessage(String noPermissionMessage);
}
