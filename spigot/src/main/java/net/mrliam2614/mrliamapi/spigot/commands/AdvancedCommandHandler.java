package net.mrliam2614.mrliamapi.spigot.commands;

import lombok.Getter;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AdvancedCommandHandler implements CommandExecutor, TabCompleter {
    @Getter
    private List<AdvancedCommand> commandList;

    @Getter
    @Setter
    private NoArgsFunction noArgsFunc;

    /**
     * @param command The command name
     * @param plugin  The plugin
     */
    public AdvancedCommandHandler(String command, JavaPlugin plugin) {
        this.commandList = new ArrayList<>();
        plugin.getCommand(command).setExecutor(this);
        plugin.getCommand(command).setTabCompleter(this);
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

                commandMap.register("seen", new BukkitCommand(alias, command));
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
                if (cmd.getArgs().size() > 0) {
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
     * @return The list of arguments
     */
    private List<String> calcArgs(Player player, AdvancedCommand cmd, String[] args, List<String> argList) {
        if (argList == null) {
            argList = new ArrayList<>();
        }
        if (cmd != null) {
            if (cmd.getPermission() != null && !player.hasPermission(cmd.getPermission())) {
                return argList;
            }
            String[] arguments = new String[args.length - 1];

            System.arraycopy(args, 1, arguments, 0, args.length - 1);

            AdvancedCommand nextArg = null;
            if (arguments.length > 0) {
                if (cmd.getArgs() == null) {
                    return argList;
                }
                if (cmd.getArgs().size() > 0) {
                    boolean found = false;
                    for (AdvancedCommand cmdString : cmd.getArgs()) {
                        if (cmdString instanceof CommandString) {
                            found = true;
                            if (cmd.getArgs().size() > 1) {
                                Bukkit.getLogger().log(Level.SEVERE, "BlazeCommandString cannot be used with multiple arguments (" + cmdString.getClass().getName() + ")!");
                                throw new IllegalArgumentException("BlazeCommandString cannot be used with multiple arguments (" + cmdString.getClass().getName() + ")!");
                            }

                            List<String> tabComplete = ((CommandString) cmdString).tabComplete(player, args);
                            if (tabComplete == null) {
                                Bukkit.getLogger().log(Level.SEVERE, "BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");
                                throw new IllegalArgumentException("BlazeCommandString cannot return null! (" + cmdString.getClass().getName() + ")!");
                            }

                            for (String s : tabComplete) {
                                if (s.equalsIgnoreCase(arguments[0])) nextArg = cmdString;

                            }
                        }
                    }

                    if (!found){
                        nextArg = cmd.getArgs().stream().filter(arg -> arg.getName().equalsIgnoreCase(arguments[0])).findAny().orElse(null);
                        if(nextArg == null) return argList;
                        if(!nextArg.hasPermission(player)){
                            nextArg = null;
                        }
                    }
                }

            }
            calcSubArgs(player, nextArg, arguments, argList, cmd, args);
        } else {
            argList.clear();
        }
        return argList;
    }

    private void calcSubArgs(Player player, AdvancedCommand nextArg, String[] arguments, List<String> argList, AdvancedCommand cmd, String[] args) {
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
            for (AdvancedCommand arg : cmd.getArgs()) {
                if (arg instanceof CommandString) {
                    List<String> tabComplete = ((CommandString) arg).tabComplete(player, args);
                    if (tabComplete != null) argList.addAll(tabComplete);
                    else {
                        Bukkit.getLogger().log(Level.SEVERE, "The return value of tabComplete cannot be null! (" + arg.getClass().getName() + ")");
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
     * @param commandSender The sender of the command
     * @param command       The command
     * @param s             The label
     * @param args          The arguments
     * @return The list of arguments
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> nextArgs = new ArrayList<>();
        if (!(commandSender instanceof Player)) {
            return nextArgs;
        }

        Player player = (Player) commandSender;

        if (args.length < 1) {
            return nextArgs;
        }

        String commandName = args[0];
        AdvancedCommand cmd = commandList.stream().filter(c -> c.getName().equalsIgnoreCase(commandName)).findAny().orElse(null);

        if (args.length == 1) {
            List<AdvancedCommand> allCommands = commandList.stream().filter(c -> c.getName().contains(commandName)).collect(Collectors.toList());
            for (AdvancedCommand cmda : allCommands) {
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