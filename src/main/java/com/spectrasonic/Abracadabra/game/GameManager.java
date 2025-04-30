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
    private int currentRound;
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
        plugin.getConfigManager().reload();
        this.centerPoint = plugin.getConfigManager().getCenterPoint();
        this.radius = plugin.getConfigManager().getRadius();
    }
    public boolean isGameRunning() {
        return gameState == GameState.RUNNING;
    }
    
    public void startGame(int round) {
        if (isGameRunning()) {
            return;
        }
        
        if (round < 1 || round > 3) {
            plugin.getLogger().warning("Attempted to start game with invalid round: " + round);
            return;
        }

        this.currentRound = round;
        gameState = GameState.RUNNING;
        
<<<<<<< HEAD
        // Execute itemdrop false command as the player who initiated the command
        Player initiator = plugin.getServer().getPlayer(plugin.getConfig().getString("initiator_player", ""));
        if (initiator != null) {
            initiator.performCommand("itemdrop false");
        }
        
=======
        participants.clear();
>>>>>>> 5806f05 (✨ add round-based game start and points configuration)
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
        // MessageUtils.sendBroadcastMessage("<gold>¡El juego de la ronda <white>" + currentRound + "</white> ha comenzado! Mantente en la zona para ganar puntos.</gold>");
    }
    public void stopGame() {
        if (gameState == GameState.STOPPED) return;
        gameState = GameState.STOPPED;
<<<<<<< HEAD
        
        // Execute itemdrop true command as the player who initiated the command
        Player initiator = plugin.getServer().getPlayer(plugin.getConfig().getString("initiator_player", ""));
        if (initiator != null) {
            initiator.performCommand("itemdrop true");
        }
        
        // Clean up magia tagged entities
=======

>>>>>>> 5806f05 (✨ add round-based game start and points configuration)
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

        this.currentRound = 0;

        // MessageUtils.broadcastTitle("<red>Juego Detenido</red>", "<gray>La partida ha terminado.</gray>", 1, 3, 1);
        // MessageUtils.sendBroadcastMessage("<red>El juego de Abracadabra ha sido detenido.</red>");
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
        if (centerPoint == null || loc.getWorld() == null || !loc.getWorld().equals(centerPoint.getWorld())) {
             return false;
        }

        double distanceSquared = loc.distanceSquared(centerPoint);
        return distanceSquared <= radius * radius;
    }
}
