package net.mrliam2614.mrliamapi.spigot.commands;

import lombok.NonNull;
import lombok.Setter;
import net.mrliam2614.mrliamapi.spigot.commands.commands.AdvancedCommand;
import net.mrliam2614.mrliamapi.spigot.commands.commands.CommandString;
import net.mrliam2614.mrliamapi.spigot.commands.interfaces.NoArgsFunction;
import net.mrliam2614.mrliamapi.spigot.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class AdvancedCommandHandler implements CommandExecutor, TabCompleter {
    private List<AdvancedCommand> commandList;
    private PluginCommand mainCommand;

    @Setter
    private NoArgsFunction noArgsFunc;

    /**
     * @param command The command name
     * @param plugin  The plugin
     */
    public AdvancedCommandHandler(String command, JavaPlugin plugin) {
        this.commandList = new ArrayList<>();
        PluginCommand cmd = plugin.getCommand(command);
        if(cmd == null) {
            throw new IllegalArgumentException("Command " + command + " does not exist in plugin.yml!");
        }
        mainCommand = cmd;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    /**
     * @param command The command to register
     *                This will register the command and all of its aliases
     */
    public void registerCommand(AdvancedCommand command) {
        this.commandList.add(command);
        for (String alias : command.getAliases()) {
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                commandMap.register(mainCommand.getName(), new BukkitCommand(alias, command));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param parentCommand The parent command
     * @param arg           The command to add
     */
    public void addArg(AdvancedCommand parentCommand, AdvancedCommand arg) {
        parentCommand.addArg(arg);
    }

    /**
     * @param sender  The sender of the command
     * @param command The command
     * @param label   The label
     * @param args    The arguments
     * @return The result of the command
     */
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 0) {
            if (noArgsFunc != null)
                noArgsFunc.execute(sender);
            return false;
        }

        String cmdArg = args[0];
        AdvancedCommand cmd = commandList.stream().filter(c -> c.getName().equalsIgnoreCase(cmdArg)).findFirst().orElse(null);

        if (!(sender instanceof Player) && cmd.isOnlyPlayers()) {
            sender.sendMessage(ChatColor.RED + "This command is only for players!");
            return true;
        }

        calcCommand(sender, cmd, args);
        return false;
    }

    /**
     * Calculates the arguments for the command
     *
     * @param sender The sender of the command
     * @param cmd    The command
     * @param args   The arguments
     */
    private void calcCommand(CommandSender sender, AdvancedCommand cmd, String[] args) {
        if (cmd != null) {
            if (cmd.getPermission() != null && !cmd.hasPermission(sender)) {

                Messages.sendMessage(sender, cmd.getNoPermissionMessage());
                return;
            }
            String[] arguments;
            if (args.length == 1) {
                arguments = new String[0];
            } else {
                arguments = new String[args.length - 1];
                System.arraycopy(args, 1, arguments, 0, args.length - 1);
            }

            AdvancedCommand nextArg = null;
            if (arguments.length > 0) {
                if (!cmd.getArgs().isEmpty()) {
                    boolean found = false;
                    for (AdvancedCommand cmdString : cmd.getArgs()) {
                        if (cmdString instanceof CommandString) {
                            found = true;
                            if (cmd.getArgs().size() > 1)
                                throw new IllegalArgumentException("BlazeCommandString cannot be used with multiple arguments (" + cmdString.getClass().getName() + ")!");

                            List<String> tabComplete = ((CommandString) cmdString).tabComplete(sender, args);
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
                calcCommand(sender, nextArg, arguments);
            } else {
                if (!cmd.isAcceptArgs() && arguments.length > 0) {
                    Messages.sendMessage(sender, cmd.getUsage());
                    return;
                }
                if (cmd.isRequireArgs() && arguments.length == 0) {
                    Messages.sendMessage(sender, cmd.getUsage());
                    return;
                }
                cmd.execute(sender, arguments);
            }
        }
    }

    /**
     * @param player  The player
     * @param cmd     The command
     * @param args    The arguments
     * @param argList The list of arguments
     */
    private void calcArgs(Player player, AdvancedCommand cmd, String[] args, List<String> argList) {
        argList = initializeArgList(argList);
        if (cmd != null) {
            if(!cmd.hasPermission(player)){
                return;
            }
            String[] arguments = getArguments(args);
            AdvancedCommand nextArg = getNextArg(player, cmd, arguments);
            calcSubArgs(player, nextArg, arguments, argList, cmd, args);
        } else {
            argList.clear();
        }
    }

    private List<String> initializeArgList(List<String> argList) {
        if (argList == null) {
            argList = new ArrayList<>();
        }
        return argList;
    }

    private String[] getArguments(String[] args) {
        String[] arguments = new String[args.length - 1];
        System.arraycopy(args, 1, arguments, 0, args.length - 1);
        return arguments;
    }

    private AdvancedCommand getNextArg(Player player, AdvancedCommand cmd, String[] arguments) {
        AdvancedCommand nextArg = null;
        if (arguments.length > 1) {
            if (cmd.getArgs() == null) {
                return null;
            }
            if (!cmd.getArgs().isEmpty()) {
                nextArg = findNextArg(player, cmd, arguments);
            }
        }
        return nextArg;
    }

    private AdvancedCommand findNextArg(Player player, AdvancedCommand cmd, String[] arguments) {
        AdvancedCommand nextArg = null;
        boolean found = false;
        for (AdvancedCommand cmdString : cmd.getArgs()) {
            if (cmdString instanceof CommandString) {
                found = true;
                validateCommandString(cmd, cmdString);
                nextArg = getCommandStringArg(player, cmdString, arguments);
            }
        }
        if (!found) {
            nextArg = getArgFromStream(cmd, arguments, player);
        }
        return nextArg;
    }

    private void validateCommandString(AdvancedCommand cmd, AdvancedCommand cmdString) {
        if (cmd.getArgs().size() > 1) {
            Bukkit.getLogger().log(Level.SEVERE, "BlazeCommandString cannot be used with multiple arguments (" + cmdString.getClass().getName() + ")!");
            throw new IllegalArgumentException("BlazeCommandString cannot be used with multiple arguments (" + cmdString.getClass().getName() + ")!");
        }
    }

    private AdvancedCommand getCommandStringArg(Player player, AdvancedCommand cmdString, String[] arguments) {
        AdvancedCommand nextArg = null;
        List<String> tabComplete = ((CommandString) cmdString).tabComplete(player, arguments);
        validateTabComplete(cmdString, tabComplete);
        for (String s : tabComplete) {
            if (s.equalsIgnoreCase(arguments[0])) {
                nextArg = cmdString;
                break;
            }
        }
        return nextArg;
    }

    private void validateTabComplete(AdvancedCommand cmdString, List<String> tabComplete) {
        if (tabComplete == null) {
            Bukkit.getLogger().log(Level.SEVERE, "BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");
            throw new IllegalArgumentException("BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");
        }
    }

    private AdvancedCommand getArgFromStream(AdvancedCommand cmd, String[] arguments, Player player) {
        AdvancedCommand nextArg = cmd.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(arguments[0])).findAny().orElse(null);
        if (nextArg == null || !nextArg.hasPermission(player)) {
            nextArg = null;
        }
        return nextArg;
    }

    private void calcSubArgs(Player player, AdvancedCommand nextArg, String[] arguments, List<String> argList, AdvancedCommand cmd, String[] args) {
        if (nextArg != null) {
            calcArgs(player, nextArg, arguments, argList);
        } else {
            if (cmd.getArgs() == null || args.length == 0 || args.length > 2)
                return;
            addArgsToArgList(player, argList, cmd, args);
        }
    }

    private void addArgsToArgList(Player player, List<String> argList, AdvancedCommand cmd, String[] args) {
        for (AdvancedCommand arg : cmd.getArgs()) {
            if (arg instanceof CommandString) {
                List<String> tabComplete = ((CommandString) arg).tabComplete(player, args);
                validateTabComplete(arg, tabComplete);
                argList.addAll(tabComplete);
            } else {
                argList.add(arg.getName());
            }
        }
    }

    /**
     * The tab completer from bukkit
     *
     * @param sender The sender of the command
     * @param command       The command
     * @param alias             The label
     * @param args          The arguments
     * @return The list of arguments
     */
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }
        List<String> argList = new ArrayList<>();

        if (args.length == 1) {
            handleFirstLevelTabCompletion(player, args, argList);
        } else {
            handleSubsequentLevelTabCompletion(player, args, argList);
        }

        return argList;
    }

    private void handleFirstLevelTabCompletion(Player player, String[] args, List<String> argList) {
        for (AdvancedCommand cmd : commandList) {
            if (cmd.getName().toLowerCase().startsWith(args[0].toLowerCase()) && cmd.hasPermission(player)) {
                argList.add(cmd.getName());
            }
        }
    }

    private void handleSubsequentLevelTabCompletion(Player player, String[] args, List<String> argList) {
        String cmdArg = args[0];
        AdvancedCommand cmd = commandList.stream().filter(c -> c.getName().equalsIgnoreCase(cmdArg)).findFirst().orElse(null);
        if (cmd != null && cmd.hasPermission(player)) {
            calcArgs(player, cmd, args, argList);
        }
    }
}