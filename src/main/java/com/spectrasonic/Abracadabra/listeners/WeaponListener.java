package com.spectrasonic.Abracadabra.listeners;

import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import com.spectrasonic.Abracadabra.Utils.SoundUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class WeaponListener implements Listener {

    private final Main plugin;

    public WeaponListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Verificar si el juego está en curso
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        // Verificar si es click derecho
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // Verificar el item en la mano
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.PAPER) {
            return;
        }
        
        // Verificar si tiene meta y custom model data
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != 1) {
            return;
        }
        
        // Disparar la carga ígnea
        event.setCancelled(true);
        fireWeapon(player);
    }
    
    private void fireWeapon(Player player) {
        // Crear la bola de fuego
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setYield(0); // Sin daño de explosión
        fireball.setIsIncendiary(false); // No prende fuego
        fireball.setVelocity(player.getLocation().getDirection().multiply(2));
        
        // Efectos de disparo
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
        SoundUtils.playerSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
        
        // Cooldown
        player.setCooldown(Material.PAPER, 20); // 1 segundo de cooldown
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Fireball)) {
            return;
        }
        
        // Cancelar la explosión por defecto
        event.setCancelled(true);
        
        // Obtener la ubicación del impacto
        Location impactLocation = event.getEntity().getLocation();
        
        // Efectos visuales y sonoros
        impactLocation.getWorld().spawnParticle(Particle.EXPLOSION, impactLocation, 1);
        impactLocation.getWorld().spawnParticle(Particle.FLAME, impactLocation, 30, 1, 1, 1, 0.2);
        impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        
        // Empujar a los jugadores cercanos
        double radius = 5.0;
        double power = 2.0;
        
        for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, radius, radius, radius)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                
                // Calcular vector de empuje
                Vector pushVector = target.getLocation().toVector().subtract(impactLocation.toVector());
                
                // Normalizar y aplicar potencia
                if (pushVector.length() > 0) {
                    pushVector.normalize().multiply(power);
                    pushVector.setY(0.5); // Añadir un poco de elevación
                    
                    // Aplicar el empuje
                    target.setVelocity(pushVector);
                    
                    // Mensaje y efecto
                    MessageUtils.sendActionBar(target, "<red>¡Has sido empujado por una carga ígnea!</red>");
                }
            }
        }
        
        // Eliminar la entidad
        event.getEntity().remove();
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball) {
            // Cancelar la explosión para evitar daños al terreno
            event.setCancelled(true);
        }
    }
}
