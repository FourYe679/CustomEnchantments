package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.Random;

public class TNTBurstEnchant extends CustomEnchantment {
    private final Random random = new Random();

    public TNTBurstEnchant() {
        super("tnt_burst");
    }

    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        final World world = target.getWorld();
        Location targetLoc = target.getLocation();
        final Player shooter = player;
        for (int i = 0; i < level; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2;
            double offsetZ = (random.nextDouble() - 0.5) * 2;
            Location spawnLoc = targetLoc.clone().add(offsetX, 0, offsetZ);
            final TNTPrimed tnt = world.spawn(spawnLoc, TNTPrimed.class);
            tnt.setFuseTicks(100);
            tnt.setVelocity(new Vector(0, 0.5, 0));
            final float power = 4.0f;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc = tnt.getLocation();
                    if (!tnt.isDead()) {
                        tnt.remove();
                    }
                    double prevHealth = shooter.getHealth();
                    int prevFireTicks = shooter.getFireTicks();
                    world.createExplosion(loc, power, false, false);
                    if (shooter.getHealth() < prevHealth) {
                        shooter.setHealth(prevHealth);
                    }
                    shooter.setFireTicks(prevFireTicks);
                    world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    world.spawnParticle(Particle.EXPLOSION, loc, 10);
                }
            }.runTaskLater(CustomEnchantments.getInstance(), 20L);
        }
    }
}
