package net.mrliam2614.mrliamapi.commands.interfaces;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.mrliam2614.mrliamapi.commands.AdvancedCommandHandler;
import net.mrliam2614.mrliamapi.commands.commands.AdvancedCommand;
import net.mrliam2614.mrliamapi.commands.commands.CommandString;

import java.util.ArrayList;
import java.util.Arrays;
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
            //TODO Implementare funzionalità agli alias degli args
            // AdvancedCommand cmd = getArgs().stream().filter(c -> c.isArg(cmdArg)).findFirst().orElse(null);
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

            String[] newArgs = new String[args.length -1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);

            calcCommand(sender, cmd, newArgs, false);
        } else {
            sender.sendMessage(Component.text("§4You do not have permission to execute this command!"));
        }
    }


    /**
     * Calculates the arguments for the command
     *
     * @param sender The sender of the command
     * @param cmd    The command
     * @param currentArgs   The arguments
     */
    default void calcCommand(CommandSource sender, AdvancedCommand cmd, String[] currentArgs, boolean isMain) {
        if (cmd == null) {
            sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
            return;
        }

        if (!cmd.hasPermission(sender)) {
            sender.sendMessage(Component.text(cmd.getNoPermissionMessage().replaceAll("&", "§")));
            return;
        }

        String[] nextArgs;
        if (currentArgs.length <= 1) {
            nextArgs = new String[0];
        } else {
            nextArgs = new String[currentArgs.length - 1];
            System.arraycopy(currentArgs, 1, nextArgs, 0, currentArgs.length - 1);
        }
        AdvancedCommand commandArg = null;
        if (nextArgs.length > 0) {
            if (cmd.getArgs().isEmpty()){
                cmd.execute(sender, nextArgs);
            }

            boolean found = false;
            for (AdvancedCommand stringCommand : cmd.getArgs()) {
                if (!(stringCommand instanceof CommandString)) continue;

                found = true;

                List<String> tabComplete = ((CommandString) stringCommand).tabComplete(sender, currentArgs);
                if (tabComplete == null)
                    throw new IllegalArgumentException("BlazeCommandString cannot return null! (" + stringCommand.getClass().getName() + ")!");

                for (String s : tabComplete) {
                    if (s.equalsIgnoreCase(nextArgs[0])) {
                        if (nextArgs.length > 1) {
                            String nArg = nextArgs[1];
                            commandArg = stringCommand.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(nArg)).findAny().orElse(null);
                        }
                    }
                }
            }

            if (!found)
                commandArg = cmd.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(currentArgs[0])).findAny().orElse(null);
        }

        if (commandArg != null) {
            calcCommand(sender, commandArg, nextArgs, false);
        } else {
            if (!cmd.isAcceptArgs() && nextArgs.length > 0) {
                sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
                sender.sendMessage(Component.text("&c&lNo &carguments accepted".replaceAll("&", "§")));
                return;
            }
            if (cmd.isRequireArgs() && nextArgs.length == 0) {
                sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
                sender.sendMessage(Component.text("&c&lNo &carguments found".replaceAll("&", "§")));
                return;
            }

            if (isMain) {
                if (cmd.isEnabledFromMain()) {
                    cmd.execute(sender, currentArgs);
                } else {
                    sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
                }
            } else {
                cmd.execute(sender, currentArgs);
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
    boolean isArg(String fromArg);

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
