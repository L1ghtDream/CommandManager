package dev.lightdream.commandmanager.manager;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.annotation.Command;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    public List<dev.lightdream.commandmanager.commands.Command> commands = new ArrayList<>();

    public CommandManager(CommandMain main) {
        new Reflections(main.getPackageName()).getTypesAnnotatedWith(Command.class).forEach(aClass -> {
            try {
                for (dev.lightdream.commandmanager.commands.Command command : commands) {
                    if (command.getClass().getName().equals(aClass.getName())) {
                        return;
                    }
                }
                Object obj;
                Debugger.info(aClass.getName() + " constructors: ");
                for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                    StringBuilder parameters = new StringBuilder();
                    for (Class<?> parameter : constructor.getParameterTypes()) {
                        parameters.append(parameter.getName()).append(" ");
                    }
                    if (parameters.toString().equals("")) {
                        Debugger.info("    - zero argument");
                    } else {
                        Debugger.info("    - " + parameters);
                    }
                }
                if (aClass.getDeclaredConstructors()[0].getParameterCount() == 0) {
                    obj = aClass.getDeclaredConstructors()[0].newInstance();
                } else if (aClass.getDeclaredConstructors()[0].getParameterCount() == 1) {
                    obj = aClass.getDeclaredConstructors()[0].newInstance(main);
                } else {
                    Logger.error("Class " + aClass.getName() + " does not have a valid constructor");
                    return;
                }
                if (obj instanceof dev.lightdream.commandmanager.commands.Command) {
                    commands.add((dev.lightdream.commandmanager.commands.Command) obj);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

}
