package com.spectrasonic.Abracadabra.commands;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.Abracadabra.Main;

public class CommandManager {

    private final Main plugin;
    private PaperCommandManager commandManager;

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new AbracadabraCommand(plugin));
    }
}
