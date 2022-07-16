package dev.lightdream.commandmanager.command;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.dto.CommandSpecWrap;
import dev.lightdream.lambda.LambdaExecutor;
import dev.lightdream.lambda.ScheduleUtils;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command implements CommandExecutor {

    private final CommandMain main;
    public CommandSpecWrap spec;
    public List<String> aliases;
    private boolean runAsync = false;

    public Command(CommandMain main) {
        this.main = main;
        this.spec = CommandSpecWrap.builder().build();


        if (!getClass().isAnnotationPresent(dev.lightdream.commandmanager.annotation.Command.class)) {
            Logger.warn("Command " + getClass().getName() + " is not annotated with @Command!");
            return;
        }

        dev.lightdream.commandmanager.annotation.Command command = getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class);

        runAsync = command.async();

        String permission = command.permission();
        if (!permission.equals("")) {
            spec.spec.permission(permission);
        }
        spec.onlyForConsole = command.onlyForConsole();
        spec.onlyForPlayers = command.onlyForPlayers();
        this.aliases = new ArrayList<>(Arrays.asList(command.aliases()));

        if (command.parent() == Void.class) {
            Sponge.getCommandManager().register(main, getCommandSpec(), command.aliases());
        }

    }

    public final String getMainAlias() {
        return aliases.get(0);
    }

    public CommandSpec getCommandSpec() {
        getSubCommands().forEach(command -> spec.spec.child(command.getCommandSpec(), command.aliases));
        spec.spec.executor(this);
        spec.spec.arguments(getArgs().toArray(new CommandElement[0]));
        return spec.spec.build();
    }

    public List<CommandElement> getArgs() {
        return new ArrayList<>();
    }

    @Override
    public final @NotNull CommandResult execute(@NotNull CommandSource src, @NotNull CommandContext args) {
        LambdaExecutor.NoReturnNoArgLambdaExecutor executor = () -> {
            if (spec.onlyForConsole) {
                if (!(src instanceof ConsoleSource)) {
                    src.sendMessage(Text.of(main.getLang().onlyForConsole.parse()));
                    return;
                }
                exec((ConsoleSource) src, args);
                return;
            }
            if (spec.onlyForPlayers) {
                if (!(src instanceof Player)) {
                    src.sendMessage(Text.of(main.getLang().onlyForPlayers.parse()));
                    return;
                }
                Player player = (Player) src;
                exec(player, args);
                return;
            }

            exec(src, args);
        };

        if (runAsync) {
            ScheduleUtils.runTaskAsync(executor);
        } else {
            executor.execute();
        }

        return CommandResult.success();
    }

    public void exec(@NotNull CommandSource source, @NotNull CommandContext args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + args.createSnapshot() + " for " + source.getName() + ", but the command is not implemented. Exec type: CommandSource, CommandContext");
        }

        source.sendMessages(getSubCommandsHelpMessage(source));
    }

    public void exec(@NotNull ConsoleSource console, @NotNull CommandContext args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + args.createSnapshot() + " for " + console.getName() + ", but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        console.sendMessages(getSubCommandsHelpMessage(console));
    }

    public void exec(@NotNull Player player, @NotNull CommandContext args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + args.createSnapshot() + " for " + player.getName() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        player.sendMessages(getSubCommandsHelpMessage(player));
    }

    private List<Text> getSubCommandsHelpMessage(CommandSource source) {
        List<Text> output = new ArrayList<>();
        getSubCommands().forEach(command -> output.add(
                Text.of(
                        "/" + this.aliases.get(0) + " " + command.aliases.get(0) + " " +
                                command.getCommandSpec().getUsage(source).toPlain()
                )
        ));
        return output;
    }


    private List<Command> getSubCommands() {
        List<Command> subCommands = new ArrayList<>();

        new Reflections(main.getPackageName()).getTypesAnnotatedWith(dev.lightdream.commandmanager.annotation.Command.class).forEach(aClass -> {
            if (aClass.getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).parent().getName().equals(getClass().getName())) {
                try {
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

                    subCommands.add((Command) obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        return subCommands;
    }

    public final @Nullable Command getSubCommand(String name) {
        for (Command command : getSubCommands()) {
            if (command.aliases.contains(name)) {
                return command;
            }
        }
        return null;
    }


}