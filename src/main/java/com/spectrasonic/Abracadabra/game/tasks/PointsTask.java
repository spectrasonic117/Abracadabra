package com.spectrasonic.Abracadabra.game.tasks;

import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import com.spectrasonic.Abracadabra.Utils.PointsManager;
import com.spectrasonic.Abracadabra.Utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PointsTask extends BukkitRunnable {

    private final Main plugin;
    private final PointsManager pointsManager;

    public PointsTask(Main plugin) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
    }

    @Override
    public void run() {
        if (!plugin.getGameManager().isGameRunning()) {
            this.cancel();
            return;
        }

        int playersInZone = 0;
        int currentRound = plugin.getGameManager().getCurrentRound();
        int pointsToAdd = plugin.getConfigManager().getAddPoints(currentRound);

        for (Player player : plugin.getGameManager().getParticipants()) {
            if (player.getGameMode() != GameMode.ADVENTURE) {
                continue;
            }
            if (plugin.getGameManager().isInZone(player.getLocation())) {
                playersInZone++;
                pointsManager.addPoints(player, pointsToAdd);
                MessageUtils.sendActionBar(player, "<green><b>+" + pointsToAdd + " Puntos</green>");
                SoundUtils.playerSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.6f);

                Bukkit.getScheduler().runTaskLater(plugin, () ->
                    MessageUtils.sendActionBar(player, ""), 20L);
            }
        }

        // if (playersInZone > 0) {
        //     MessageUtils.sendConsoleMessage("<yellow>Hay " + playersInZone + " jugadores en la zona recibiendo puntos.</yellow>");
        // }
    }
}