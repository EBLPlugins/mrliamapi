package net.mrliam2614.mrliamapi.commands.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandMeta {
    String name();

    String permission() default "";

    String description() default "";

    String[] aliases() default {};

    boolean onlyPlayers() default true;

    String usage() default "&cYou are using the command incorrectly!";

    String noPermissionMessage() default "&4You do not have permission to use this command!";

    boolean requireArgs() default false;

    boolean acceptArgs() default true;

    boolean enabledFromMain() default false;
}
