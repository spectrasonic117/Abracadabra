package com.spectrasonic.Abracadabra;

import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import com.spectrasonic.Abracadabra.commands.CommandManager;
import com.spectrasonic.Abracadabra.config.ConfigManager;
import com.spectrasonic.Abracadabra.game.GameManager;
import com.spectrasonic.Abracadabra.listeners.GameListener;
import com.spectrasonic.Abracadabra.listeners.WeaponListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private GameManager gameManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.gameManager = new GameManager(this);
        this.commandManager = new CommandManager(this);

        registerCommands();
        registerEvents();
        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stopGame();
        }
        MessageUtils.sendShutdownMessage(this);
    }

    public void registerCommands() {
        commandManager.registerCommands();
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
    }
    
    public void reload() {
        this.reloadConfig();
        this.gameManager.loadConfig();
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }
}
