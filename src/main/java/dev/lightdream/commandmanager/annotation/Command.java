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

}
