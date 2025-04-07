package com.spectrasonic.Abracadabra.listeners;

import com.spectrasonic.Abracadabra.Main;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {

    private final Main plugin;

    public GameListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Si el juego está en curso y el jugador está en modo aventura, darle el arma
        if (plugin.getGameManager().isGameRunning() && player.getGameMode() == GameMode.ADVENTURE) {
            plugin.getGameManager().giveWeapon(player);
            plugin.getGameManager().getParticipants().add(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Eliminar al jugador de la lista de participantes si se desconecta
        plugin.getGameManager().getParticipants().remove(player);
    }
}
