package com.spectrasonic.Abracadabra.config;

import com.spectrasonic.Abracadabra.Main;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigManager {

    private final Main plugin;
    private Location centerPoint;
    private int radius;
    private Map<Integer, Integer> addPointsConfig;
    private Map<Integer, Integer> subtractPointsConfig;


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

        addPointsConfig = loadPointsSection("add_points");
        subtractPointsConfig = loadPointsSection("substract_points");
    }

    private Map<Integer, Integer> loadPointsSection(String sectionName) {
        Map<Integer, Integer> pointsMap = new HashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(sectionName);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    String[] parts = key.split("_");
                    if (parts.length == 2 && parts[0].equalsIgnoreCase("round")) {
                        int round = Integer.parseInt(parts[1]);
                        int points = section.getInt(key);
                        pointsMap.put(round, points);
                    } else {
                        plugin.getLogger().warning("Invalid key format in config section '" + sectionName + "': " + key);
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid round number in config section '" + sectionName + "' key '" + key + "': " + e.getMessage());
                }
            }
        } else {
            plugin.getLogger().warning("Config section '" + sectionName + "' not found!");
        }
        return pointsMap;
    }

    public int getAddPoints(int round) {
        return addPointsConfig.getOrDefault(round, 1);
    }

    public int getSubtractPoints(int round) {
        return subtractPointsConfig.getOrDefault(round, 10);
    }

    public Location getRespawnPoint() {
        double x = plugin.getConfig().getDouble("respawn_pont.x");
        double y = plugin.getConfig().getDouble("respawn_pont.y");
        double z = plugin.getConfig().getDouble("respawn_pont.z");
        World world = plugin.getServer().getWorlds().get(0);
        return new Location(world, x, y, z);
    }

    public Location getGameStopTeleport() {
        double x = plugin.getConfig().getDouble("game_stop_teleport.x");
        double y = plugin.getConfig().getDouble("game_stop_teleport.y");
        double z = plugin.getConfig().getDouble("game_stop_teleport.z");
        World world = plugin.getServer().getWorlds().get(0);
        return new Location(world, x, y, z);
    }

    public MemorySection getConfig() {
        throw new UnsupportedOperationException("Unimplemented method 'getConfig'");
    }
}
