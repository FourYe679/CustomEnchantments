package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.Collection;

public class HomingArrowEnchant extends CustomEnchantment {
    public HomingArrowEnchant() {
        super("homing_arrow");
    }

    @Override
    public void onBowShoot(Player player, Arrow arrow, int level, ItemStack item) {
        if (level < 1) return;
        final double speed = arrow.getVelocity().length();
        if (speed < 0.0001) return;
        final double range = level * 10.0;
        final int maxTicks = level * 40;
        final Player shooter = player;
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (arrow.isDead() || !arrow.isValid() || arrow.isOnGround()) {
                    cancel();
                    return;
                }
                if (ticks >= maxTicks) {
                    cancel();
                    return;
                }
                Location arrowLoc = arrow.getLocation();
                Collection<Entity> nearby = arrow.getWorld().getNearbyEntities(arrowLoc, range, range, range);
                LivingEntity nearest = null;
                double nearestDistSq = range * range;
                for (Entity e : nearby) {
                    if (!(e instanceof LivingEntity)) continue;
                    if (e.getUniqueId().equals(shooter.getUniqueId())) continue;
                    if (e instanceof Player) {
                        Player p = (Player) e;
                        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR
                                || p.getGameMode() == org.bukkit.GameMode.CREATIVE) continue;
                    }
                    if (e.isDead()) continue;
                    double distSq = e.getLocation().distanceSquared(arrowLoc);
                    if (distSq < nearestDistSq) {
                        nearestDistSq = distSq;
                        nearest = (LivingEntity) e;
                    }
                }
                if (nearest != null) {
                    Vector toTarget = nearest.getLocation().toVector().subtract(arrowLoc.toVector());
                    double len = toTarget.length();
                    if (len > 0.0001) {
                        toTarget.normalize().multiply(speed);
                        arrow.setVelocity(toTarget);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(CustomEnchantments.getInstance(), 1L, 1L);
    }
}
