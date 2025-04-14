package com.spectrasonic.Abracadabra.config;

import com.spectrasonic.Abracadabra.Main;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class ConfigManager {

    private final Main plugin;
    private Location centerPoint;
    private int radius;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }

    private void loadConfig() {
        double x = plugin.getConfig().getDouble("points_zone.point.x");
        double y = plugin.getConfig().getDouble("points_zone.point.y");
        double z = plugin.getConfig().getDouble("points_zone.point.z");
        this.radius = plugin.getConfig().getInt("points_zone.radius", 16);
        
        if (!plugin.getServer().getWorlds().isEmpty()) {
            World world = plugin.getServer().getWorlds().get(0);
            this.centerPoint = new Location(world, x, y, z);
        }
    }

    public Location getRespawnPoint() {
        double x = plugin.getConfig().getDouble("respawn_pont.x");
        double y = plugin.getConfig().getDouble("respawn_pont.y");
        double z = plugin.getConfig().getDouble("respawn_pont.z");
        World world = plugin.getServer().getWorlds().get(0);
        return new Location(world, x, y, z);
    }
}
