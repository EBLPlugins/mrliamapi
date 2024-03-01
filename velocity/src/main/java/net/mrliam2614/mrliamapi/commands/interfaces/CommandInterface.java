package net.mrliam2614.mrliamapi.commands.interfaces;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.mrliam2614.mrliamapi.commands.BlazeCommandHandler;
import net.mrliam2614.mrliamapi.commands.commands.BlazeCommand;
import net.mrliam2614.mrliamapi.commands.commands.BlazeCommandString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface CommandInterface extends SimpleCommand {
    void init();

    /**
     * Called when the command is executed
     */

    default void execute(Invocation invocation) {
        if(invocation.source().hasPermission(getPermission()))
            execute(invocation.source(), invocation.arguments());
        else
            invocation.source().sendMessage(Component.text("§4You do not have permission to use this command!"));
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
            for (BlazeCommand bc : getArgs()) {
                if (bc instanceof BlazeCommandString) {
                    if(player.hasPermission(bc.getPermission()))
                        return ((BlazeCommandString) bc).tabComplete(invocation.source(), invocation.arguments());
                }
                if(player.hasPermission(bc.getPermission()))
                    nextArgs.add(bc.getName());
            }
            return nextArgs;
        }

        if (args.length == 1) {
            for (BlazeCommand bc : getArgs()) {
                if (bc instanceof BlazeCommandString) {
                    if(player.hasPermission(bc.getPermission()))
                        return ((BlazeCommandString) bc).tabComplete(invocation.source(), invocation.arguments());
                }
            }
        }

        String commandName = args[0];
        BlazeCommand cmd = getArgs().stream().filter(c -> c.getName().equalsIgnoreCase(commandName)).findAny().orElse(null);
        if (args.length == 1) {
            List<BlazeCommand> allCommands = getArgs().stream().filter(c -> c.getName().contains(commandName)).collect(Collectors.toList());
            for (BlazeCommand cmda : allCommands) {
                if(player.hasPermission(cmda.getPermission()))
                    nextArgs.add(cmda.getName());
            }
            return nextArgs;
        }
        if (cmd == null) {
            return nextArgs;
        }
        List<String> getArgs = BlazeCommandHandler.getInstance().calcArgs(player, cmd, args, new ArrayList<>());

        for (String gotArg : getArgs) {
            if (gotArg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                nextArgs.add(gotArg);
        }

        return nextArgs;
    }

    void execute(CommandSource sender, String[] args);

    void addArg(BlazeCommand command);

    void addArgs(BlazeCommand... commands);

    BlazeCommand getArg(String name);

    List<BlazeCommand> getArgs();

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
