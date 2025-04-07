package com.spectrasonic.Abracadabra.game.tasks;

import com.spectrasonic.Abracadabra.Main;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTask extends BukkitRunnable {

    private final Main plugin;
    private double angle = 0;

    public ParticleTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getGameManager().isGameRunning()) {
            this.cancel();
            return;
        }

        Location center = plugin.getGameManager().getCenterPoint();
        World world = center.getWorld();
        int radius = plugin.getGameManager().getRadius();

        // Crear un anillo de partículas
        for (int i = 0; i < 10; i++) {
            double currentAngle = angle + (i * (Math.PI * 2) / 10);
            double x = center.getX() + (radius * Math.cos(currentAngle));
            double z = center.getZ() + (radius * Math.sin(currentAngle));
            
            // Partículas en el suelo
            world.spawnParticle(
                Particle.FLAME, 
                new Location(world, x, center.getY(), z), 
                1, 0, 0, 0, 0
            );
            
            // Partículas en el aire (columnas)
            for (int y = 0; y < 5; y++) {
                world.spawnParticle(
                    Particle.PORTAL, 
                    new Location(world, x, center.getY() + y, z), 
                    1, 0, 0, 0, 0
                );
            }
        }

        // Incrementar el ángulo para la animación
        angle += 0.05;
        if (angle >= Math.PI * 2) {
            angle = 0;
        }
    }
}
