package net.mrliam2614.mrliamapi.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.mrliam2614.mrliamapi.commands.commands.BlazeCommand;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitCommand implements SimpleCommand {
    private final BlazeCommand executor;

    protected BukkitCommand(@NonNull String name, @NonNull BlazeCommand executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Invocation invocation) {
        executor.execute(invocation.source(), invocation.arguments());
    }
}
