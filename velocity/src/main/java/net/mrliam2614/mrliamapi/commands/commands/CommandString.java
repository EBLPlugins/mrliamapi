package net.mrliam2614.mrliamapi.commands.commands;

import com.velocitypowered.api.command.CommandSource;

import java.util.List;

public abstract class CommandString extends AdvancedCommand {
    public abstract List<String> tabComplete(CommandSource sender, String[] args);

    public void execute(CommandSource sender, String[] args) {
        return;
    }

    ;
}
