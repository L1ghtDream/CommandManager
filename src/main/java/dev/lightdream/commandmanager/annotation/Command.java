package dev.lightdream.commandmanager.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    Class<?> parent() default Void.class;

    String[] aliases();

    String permission() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

    /**
     * @return true if you want the command to NOT block the main thread while executing or false if you want the command to block the main thread while executing
     */
    boolean async() default false;

}
