package net.mrliam2614.mrliamapi.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.mrliam2614.mrliamapi.commands.commands.BlazeCommand;
import net.mrliam2614.mrliamapi.commands.commands.BlazeCommandString;
import net.mrliam2614.mrliamapi.commands.commands.NoArgsFunction;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BlazeCommandHandler extends BlazeCommand {
    @Getter
    public static BlazeCommandHandler instance;
    @Getter
    private final List<BlazeCommand> commandList;

    @Getter
    @Setter
    private NoArgsFunction noArgsFunc;

    private final Logger logger;

    private final ProxyServer proxy;

    /**
     *
     */
    public BlazeCommandHandler(Logger logger, ProxyServer proxy, String permission, String commandName, String... aliases) {
        instance = this;
        this.logger = logger;
        this.proxy = proxy;
        this.commandList = new ArrayList<>();
        setPermission(permission);

        this.proxy.getCommandManager().register(commandName, this, aliases);
    }

    /**
     * @param command The command to register
     *                This will register the command and all of its aliases
     */
    public void registerCommand(BlazeCommand command) {
        commandList.add(command);
        if (command.getAliases().length > 0) {
            String[] subAliases = new String[command.getAliases().length - 1];
            System.arraycopy(command.getAliases(), 1, subAliases, 0, command.getAliases().length - 1);

            this.proxy.getCommandManager().register(command.getAliases()[0], command, subAliases);
        }
        if (command.isEnabledFromMain()) {
            this.addArg(command);
        }
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (sender.hasPermission(getPermission())) {
            if (args.length == 0) {
                if (noArgsFunc != null)
                    noArgsFunc.execute(sender);
                return;
            }

            String cmdArg = args[0];
            BlazeCommand cmd = commandList.stream().filter(c -> c.getName().equalsIgnoreCase(cmdArg)).findFirst().orElse(null);

            if (!(sender instanceof Player)) {
                assert cmd != null;
                if (cmd.isOnlyPlayers()) {
                    sender.sendMessage(Component.text("§4Only players can execute this command!"));
                    return;
                }
            }

            calcCommand(sender, cmd, args, true);
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
    private void calcCommand(CommandSource sender, BlazeCommand cmd, String[] args, boolean isMain) {
        if (cmd != null) {
            if (cmd.getPermission() != null && !cmd.hasPermission(sender)) {

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

            BlazeCommand nextArg = null;
            if (arguments.length > 0) {
                if (!cmd.getArgs().isEmpty()) {
                    boolean found = false;
                    for (BlazeCommand cmdString : cmd.getArgs()) {
                        if (cmdString instanceof BlazeCommandString) {
                            found = true;

                            List<String> tabComplete = ((BlazeCommandString) cmdString).tabComplete(sender, args);
                            if (tabComplete == null)
                                throw new IllegalArgumentException("BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");

                            for (String s : tabComplete) {
                                if (s.equalsIgnoreCase(arguments[0])) {
                                    if (arguments.length > 1) {
                                        String nArg = arguments[1];
                                        nextArg = cmdString.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(nArg)).findAny().orElse(null);
                                    }
                                }
                            }
                        }
                    }

                    if (!found)
                        nextArg = cmd.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(arguments[0])).findAny().orElse(null);
                }
            }

            if (nextArg != null) {
                calcCommand(sender, nextArg, arguments, false);
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
                    if (cmd.isEnabledFromMain())
                        cmd.execute(sender, arguments);
                    else
                        sender.sendMessage(Component.text(cmd.getUsage().replaceAll("&", "§")));
                } else {
                    cmd.execute(sender, arguments);
                }
            }
        }
    }

    /**
     * @param player  The player
     * @param cmd     The command
     * @param args    The arguments
     * @param argList The list of arguments
     * @return The list of arguments
     */
    public List<String> calcArgs(Player player, BlazeCommand cmd, String[] args, List<String> argList) {
        if (argList == null) {
            argList = new ArrayList<>();
        }
        if (cmd != null) {
            if (cmd.getPermission() != null && !player.hasPermission(cmd.getPermission())) {
                return argList;
            }
            String[] arguments = new String[args.length - 1];

            System.arraycopy(args, 1, arguments, 0, args.length - 1);

            BlazeCommand nextArg = null;
            if (arguments.length > 0) {
                if (cmd.getArgs() == null) {
                    return argList;
                }
                if (!cmd.getArgs().isEmpty()) {
                    boolean found = false;
                    for (BlazeCommand cmdString : cmd.getArgs()) {
                        if (cmdString instanceof BlazeCommandString) {
                            found = true;

                            List<String> tabComplete = ((BlazeCommandString) cmdString).tabComplete(player, args);
                            if (tabComplete == null) {
                                logger.error("BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");
                                throw new IllegalArgumentException("BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");
                            }

                            for (String s : tabComplete) {
                                if (s.equalsIgnoreCase(arguments[0])) {
                                    nextArg = cmdString;
                                    break;
                                }

                            }
                        }
                    }

                    if (!found)
                        nextArg = cmd.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(arguments[0])).findAny().orElse(null);
                }

            }
            calcSubArgs(player, nextArg, arguments, argList, cmd, args);
        } else {
            argList.clear();
        }
        return argList;
    }

    private void calcSubArgs(Player player, BlazeCommand nextArg, String[] arguments, List<String> argList, BlazeCommand cmd, String[] args) {
        if (nextArg != null) {
            calcArgs(player, nextArg, arguments, argList);
        } else {

            if (cmd.getArgs() == null)
                return;

            if (args.length == 0) {
                return;
            }
            if (args.length > 2) {
                return;
            }
            for (BlazeCommand arg : cmd.getArgs()) {
                if (arg instanceof BlazeCommandString) {
                    List<String> tabComplete = ((BlazeCommandString) arg).tabComplete(player, args);
                    if (tabComplete != null) argList.addAll(tabComplete);
                    else {
                        logger.error("The return value of tabComplete cannot be null! (" + arg.getClass().getName() + ")");
                        throw new UnsupportedOperationException("The return value of tabComplete cannot be null! (" + arg.getClass().getName() + ")");
                    }
                } else {
                    argList.add(arg.getName());
                }
            }
        }
    }

    /**
     * The tab completer from bukkit
     *
     * @return The list of arguments
     */
    @Override
    public List<String> suggest(Invocation invocation) {
        CommandSource commandSender = invocation.source();
        String[] args = invocation.arguments();
        List<String> nextArgs = new ArrayList<>();
        if (!(commandSender instanceof Player player)) {
            return nextArgs;
        }

        if (args.length < 1) {
            for (BlazeCommand bc : commandList) {
                if (bc instanceof BlazeCommandString) {
                    if (player.hasPermission(bc.getPermission()))
                        return ((BlazeCommandString) bc).tabComplete(invocation.source(), invocation.arguments());
                }
                if (bc.isEnabledFromMain())
                    if (player.hasPermission(bc.getPermission()))
                        nextArgs.add(bc.getName());
            }
            return nextArgs;
        }

        if (args.length == 1) {
            for (BlazeCommand bc : getArgs()) {
                if (bc instanceof BlazeCommandString) {
                    if (player.hasPermission(bc.getPermission()))
                        return ((BlazeCommandString) bc).tabComplete(invocation.source(), invocation.arguments());
                }
            }
        }

        String commandName = args[0];
        BlazeCommand cmd = commandList.stream().filter(c -> c.getName().equalsIgnoreCase(commandName)).findAny().orElse(null);

        if (args.length == 1) {
            List<BlazeCommand> allCommands = commandList.stream().filter(c -> c.getName().contains(commandName)).toList();
            for (BlazeCommand cmda : allCommands) {
                if (cmda.isEnabledFromMain())
                    if (player.hasPermission(cmda.getPermission()))
                        nextArgs.add(cmda.getName());
            }
            return nextArgs;
        }
        if (cmd == null) {
            return nextArgs;
        }
        List<String> getArgs = calcArgs(player, cmd, args, new ArrayList<>());

        for (String gotArg : getArgs) {
            if (gotArg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                nextArgs.add(gotArg);
        }

        return nextArgs;
    }
}