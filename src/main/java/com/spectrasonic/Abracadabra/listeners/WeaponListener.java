package com.spectrasonic.Abracadabra.listeners;

import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import com.spectrasonic.Abracadabra.Utils.SoundUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import java.util.Map;
import java.util.WeakHashMap;

public class WeaponListener implements Listener {

    private final Main plugin;
    // Track Snowball -> ItemDisplay
    private final Map<Snowball, ItemDisplay> snowballDisplays = new WeakHashMap<>();

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

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(player.getLocation().getDirection().multiply(2));

        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
        SoundUtils.playerSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);

        player.setCooldown(Material.PAPER, 20);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }

        event.setCancelled(true);

        Location impactLocation = event.getEntity().getLocation();

        impactLocation.getWorld().spawnParticle(Particle.EXPLOSION, impactLocation, 1);
        impactLocation.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, impactLocation, 30, 1, 1, 1, 0.2);
        impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        double radius = plugin.getConfig().getDouble("weapon.knockback_radius", 7.0);
        double power = plugin.getConfig().getDouble("weapon.knockback_power", 4.0);

        for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, radius, radius, radius)) {
            // Exclude the snowball entity itself from knockback
            if (entity.equals(event.getEntity())) {
                continue;
            }

            if (entity instanceof Player) {
                Player target = (Player) entity;

                Vector pushVector = target.getLocation().toVector().subtract(impactLocation.toVector());

                if (pushVector.length() > 0) {
                    pushVector.normalize().multiply(power);
                    pushVector.setY(0.7); // Maintain upward velocity

                    target.setVelocity(pushVector);
                }
            }
        }

        // Remove the associated ItemDisplay if present
        Snowball snowball = (Snowball) event.getEntity();
        ItemDisplay display = snowballDisplays.remove(snowball);
        if (display != null && !display.isDead()) {
            display.remove();
        }

        event.getEntity().remove();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // Snowballs don't cause explosions by default, this event handler might not be necessary for Snowballs
        // but keeping it just in case another plugin or modification causes them to explode.
        if (event.getEntity() instanceof Snowball) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }

        if (event.getEntity().getType() == EntityType.SNOWBALL) {
            Snowball snowball = (Snowball) event.getEntity();

            // Set the snowball's item to AIR to remove its default model
            snowball.setItem(new ItemStack(Material.AIR));
            snowball.setGravity(false);

            ItemStack displayItem = new ItemStack(Material.IRON_NUGGET);
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(3);
                displayItem.setItemMeta(meta);
            }

            ItemDisplay itemDisplay = (ItemDisplay) snowball.getWorld().spawnEntity(
                    snowball.getLocation(),
                    EntityType.ITEM_DISPLAY);
            itemDisplay.setItemStack(displayItem);

            // Get the shooter and apply rotation if it's a player
            if (snowball.getShooter() instanceof Player) {
                Player shooter = (Player) snowball.getShooter();
                // Apply the player's yaw and pitch to the ItemDisplay model
                itemDisplay.setRotation(shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
            }

            // Mount the ItemDisplay as a passenger of the snowball
            snowball.addPassenger(itemDisplay);

            // Store the association
            snowballDisplays.put(snowball, itemDisplay);
        }
    }
}
