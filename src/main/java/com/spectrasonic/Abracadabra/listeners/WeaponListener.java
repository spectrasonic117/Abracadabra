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
        
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.PAPER) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != 1124) {
            return;
        }
        
        event.setCancelled(true);
        fireWeapon(player);
    }
    
    private void fireWeapon(Player player) {
        if (player.getCooldown(Material.PAPER) > 0) {
            MessageUtils.sendActionBar(player, "<red><b>Recargando Mágia");
            return;
        }
        
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setYield(0);
        fireball.setIsIncendiary(false);
        fireball.setVelocity(player.getLocation().getDirection().multiply(2));

        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
        SoundUtils.playerSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);

        player.setCooldown(Material.PAPER, 20);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Fireball)) {
            return;
        }

        event.setCancelled(true);

        Location impactLocation = event.getEntity().getLocation();

        impactLocation.getWorld().spawnParticle(Particle.EXPLOSION, impactLocation, 1);
        impactLocation.getWorld().spawnParticle(Particle.FLAME, impactLocation, 30, 1, 1, 1, 0.2);
        impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        double radius = plugin.getConfig().getDouble("weapon.knockback_radius", 7.0);
        double power = plugin.getConfig().getDouble("weapon.knockback_power", 4.0);
        
        for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, radius, radius, radius)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;

                Vector pushVector = target.getLocation().toVector().subtract(impactLocation.toVector());

                if (pushVector.length() > 0) {
                    pushVector.normalize().multiply(power);
                    pushVector.setY(0.7);

                    target.setVelocity(pushVector);
                }
            }
        }

        event.getEntity().remove();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball) {
            event.setCancelled(true);
        }
    }
}
