package net.mrliam2614.mrliamapi.spigot.commands.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandMeta {
    String name();

    String permission() default "";

    String description();

    String[] aliases() default {};

    boolean onlyPlayers() default false;

    String usage() default "";

    String noPermissionMessage() default "&4You do not have permission to use this command!";

    boolean requireArgs() default false;

    boolean acceptArgs() default true;
}
