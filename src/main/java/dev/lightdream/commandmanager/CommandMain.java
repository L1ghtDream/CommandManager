package dev.lightdream.commandmanager;

import dev.lightdream.commandmanager.dto.CommandLang;
import dev.lightdream.commandmanager.manager.CommandManager;

public interface CommandMain {

    CommandLang getLang();

    @SuppressWarnings("unused")
    CommandManager getCommandManager();

    String getPackageName();


}