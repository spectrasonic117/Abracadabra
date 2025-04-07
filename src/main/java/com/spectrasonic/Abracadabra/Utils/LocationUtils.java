package com.spectrasonic.Abracadabra.Utils;

import org.bukkit.Location;

public class LocationUtils {

    private LocationUtils() {
        // Constructor privado para evitar instanciación
    }
    
    public static boolean isInRadius(Location center, Location target, double radius) {
        if (center.getWorld() != target.getWorld()) {
            return false;
        }
        
        return center.distance(target) <= radius;
    }
}
