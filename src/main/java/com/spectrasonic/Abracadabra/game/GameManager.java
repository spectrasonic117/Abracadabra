package com.spectrasonic.Abracadabra.game;

import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.ItemBuilder;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import com.spectrasonic.Abracadabra.Utils.SoundUtils;
import com.spectrasonic.Abracadabra.game.tasks.ParticleTask;
import com.spectrasonic.Abracadabra.game.tasks.PointsTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@Getter
public class GameManager {

    private final Main plugin;
    private GameState gameState;
    private Location centerPoint;
    private int radius;
    private int height;
    private PointsTask pointsTask;
    private ParticleTask particleTask;
    private final Set<Player> participants = new HashSet<>();
    
    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.gameState = GameState.STOPPED;
        loadConfig();
    }
    
    public void loadConfig() {
        double x = plugin.getConfig().getDouble("points_zone.point.x");
        double y = plugin.getConfig().getDouble("points_zone.point.y");
        double z = plugin.getConfig().getDouble("points_zone.point.z");
        this.radius = plugin.getConfig().getInt("points_zone.radius", 16);
        this.height = plugin.getConfig().getInt("points_zone.height", 5);
        
        if (Bukkit.getWorlds().isEmpty()) {
            MessageUtils.sendConsoleMessage("<red>No hay mundos cargados para establecer el punto central.</red>");
            return;
        }
        
        this.centerPoint = new Location(Bukkit.getWorlds().get(0), x, y, z);
    }
    
    public boolean isGameRunning() {
        return gameState == GameState.RUNNING;
    }
    
    public void startGame() {
        if (isGameRunning()) {
            return;
        }
        
        gameState = GameState.RUNNING;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                giveWeapon(player);
                participants.add(player);
            }
        }
        
        pointsTask = new PointsTask(plugin);
        pointsTask.runTaskTimer(plugin, 0L, 40L);
        
        particleTask = new ParticleTask(plugin);
        particleTask.runTaskTimer(plugin, 0L, 5L);
        
        MessageUtils.broadcastTitle("<gold>¡Abracadabra!</gold>", "<yellow>¡El juego ha comenzado!</yellow>", 1, 3, 1);
        MessageUtils.sendBroadcastMessage("<gold>¡El juego ha comenzado! Mantente en la zona para ganar puntos.</gold>");
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
    }
    
    public void stopGame() {
        if (!isGameRunning()) {
            return;
        }
        
        gameState = GameState.STOPPED;
        
        if (pointsTask != null) {
            pointsTask.cancel();
            pointsTask = null;
        }
        
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        
        for (Player player : participants) {
            if (player.isOnline()) {
                player.getInventory().clear();
            }
        }
        
        participants.clear();
        
        MessageUtils.broadcastTitle("<red>¡Fin del juego!</red>", "<yellow>El juego ha terminado</yellow>", 1, 3, 1);
        MessageUtils.sendBroadcastMessage("<red>¡El juego ha terminado!</red>");
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
    }
    
    public void giveWeapon(Player player) {
        ItemStack weapon = ItemBuilder.setMaterial("PAPER")
                .setName("<red><b>Lanzagranadas</b></red>")
                .setLore(
                        "<gray>Un arma poderosa que dispara cargas ígneas",
                        "<gray>capaces de empujar a tus enemigos",
                        "<gray>fuera de la zona de control.",
                        "",
                        "<yellow>Click derecho para disparar</yellow>"
                )
                .setCustomModelData(1)
                .build();
        
        player.getInventory().addItem(weapon);
    }
    
    public boolean isInZone(Location loc) {
        if (centerPoint == null) return false;

        double dx = loc.getX() - centerPoint.getX();
        double dz = loc.getZ() - centerPoint.getZ();
        if ((dx * dx + dz * dz) > (radius * radius)) {
            return false;
        }
        
        double dy = Math.abs(loc.getY() - centerPoint.getY());
        return dy <= height / 2.0;
    }
}
