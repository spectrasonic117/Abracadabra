package com.spectrasonic.Abracadabra.game;

import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.ItemBuilder;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import com.spectrasonic.Abracadabra.game.tasks.LavaWatchdogTask;
import com.spectrasonic.Abracadabra.game.tasks.ParticleTask;
import com.spectrasonic.Abracadabra.game.tasks.PointsTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Entity;

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
    private LavaWatchdogTask lavaWatchdogTask;
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
        
        // Execute itemdrop false command as the player who initiated the command
        Player initiator = plugin.getServer().getPlayer(plugin.getConfig().getString("initiator_player", ""));
        if (initiator != null) {
            initiator.performCommand("itemdrop false");
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                giveWeapon(player);
                participants.add(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
            }
        }
        
        pointsTask = new PointsTask(plugin);
        pointsTask.runTaskTimer(plugin, 0L, 40L);
        
        particleTask = new ParticleTask(plugin);
        particleTask.runTaskTimer(plugin, 0L, 5L);
        
        lavaWatchdogTask = new LavaWatchdogTask(plugin);
        lavaWatchdogTask.runTaskTimer(plugin, 0L, 20L);

        // MessageUtils.broadcastTitle("<gold>¡Abracadabra!</gold>", "<yellow>¡El juego ha comenzado!</yellow>", 1, 3, 1);
        // MessageUtils.sendBroadcastMessage("<gold>¡El juego ha comenzado! Mantente en la zona para ganar puntos.</gold>");
        // SoundUtils.broadcastPlayerSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
    }
    
    public void stopGame() {
        if (gameState == GameState.STOPPED) return;
        gameState = GameState.STOPPED;
        
        // Execute itemdrop true command as the player who initiated the command
        Player initiator = plugin.getServer().getPlayer(plugin.getConfig().getString("initiator_player", ""));
        if (initiator != null) {
            initiator.performCommand("itemdrop true");
        }
        
        // Clean up magia tagged entities
        Bukkit.getWorlds().forEach(world -> {
            world.getEntities().stream()
                .filter(entity -> entity.getScoreboardTags().contains("magia"))
                .forEach(Entity::remove);
        });
        
        if (pointsTask != null) pointsTask.cancel();
        if (particleTask != null) particleTask.cancel();
        if (lavaWatchdogTask != null) lavaWatchdogTask.cancel();

        Location gameStopLoc = plugin.getConfigManager().getGameStopTeleport();

        for (Player player : participants) {
            if (!player.isOnline()) continue;
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            player.setFireTicks(0);
            
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(gameStopLoc);
                player.getInventory().clear();
            } else if (player.getGameMode() == GameMode.ADVENTURE) {
                player.teleport(gameStopLoc);
                player.getInventory().clear();
            }
        }
        participants.clear();

    }
    
    public void giveWeapon(Player player) {
        ItemStack Weapon = ItemBuilder.setMaterial("PAPER")
                .setName("<red><b>Varita Magica</b></red>")
                .setLore(
                        "<gray>Dispara la magia",
                        "<gray>capaz de empujar a tus enemigos",
                        "",
                        "<yellow>Click derecho para disparar</yellow>"
                )
                .setCustomModelData(1124)
                .build();
        
        player.getInventory().addItem(Weapon);
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
