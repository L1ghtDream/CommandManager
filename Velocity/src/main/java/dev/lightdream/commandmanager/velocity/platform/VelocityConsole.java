package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.logger.Logger;

public class VelocityConsole extends PlatformConsole {

    public VelocityConsole(ConsoleCommandSource consoleCommandSender, VelocityAdapter adapter) {
        super(consoleCommandSender, adapter);
    }

}
