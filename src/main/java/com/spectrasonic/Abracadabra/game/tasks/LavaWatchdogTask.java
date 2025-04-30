package com.spectrasonic.Abracadabra.game.tasks;

import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.PointsManager;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LavaWatchdogTask extends BukkitRunnable {

    private final Main plugin;
    private final PointsManager pointsManager;

    public LavaWatchdogTask(Main plugin) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
    }

    @Override
    public void run() {
        if (!plugin.getGameManager().isGameRunning()) {
            this.cancel();
            return;
        }

        int currentRound = plugin.getGameManager().getCurrentRound();
        int pointsToSubtract = plugin.getConfigManager().getSubtractPoints(currentRound);

        for (Player player : plugin.getGameManager().getParticipants()) {
            if (!player.isOnline()) continue;

            if (player.getGameMode() != GameMode.ADVENTURE) continue;

            Material below = player.getLocation().clone().subtract(0, 1, 0).getBlock().getType();
            if (below == Material.LAVA || below.name().contains("LAVA")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "multiwarp tp 3_10 " + player.getName());
                player.setFireTicks(0);
                pointsManager.subtractPoints(player, pointsToSubtract);
                MessageUtils.sendActionBar(player, "<red><b>-" + pointsToSubtract + " Puntos</red>");
            }
        }
    }
}