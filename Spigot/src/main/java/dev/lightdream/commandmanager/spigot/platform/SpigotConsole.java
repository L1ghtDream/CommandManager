package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class SpigotConsole extends PlatformConsole<ConsoleCommandSender> {

    public SpigotConsole(ConsoleCommandSender consoleCommandSender) {
        super(consoleCommandSender);
    }
}
