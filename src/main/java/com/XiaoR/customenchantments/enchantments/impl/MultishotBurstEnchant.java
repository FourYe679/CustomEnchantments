package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MultishotBurstEnchant extends CustomEnchantment {
    public MultishotBurstEnchant() {
        super("multishot_burst");
    }

    @Override
    public void onBowShoot(Player player, AbstractArrow arrow, int level, ItemStack item) {
        if (level < 1) return;
        int extraCount = Math.min(level, 5);
        Location eyeLoc = player.getEyeLocation();
        Vector baseDir = arrow.getVelocity().clone();
        double speed = baseDir.length();
        if (speed < 0.0001) return;
        Vector dir = baseDir.normalize();
        double originalDamage = arrow.getDamage();
        boolean flaming = arrow.getFireTicks() > 0;
        double spreadStep = 5.0;
        for (int i = 1; i <= extraCount; i++) {
            double angleDeg = ((i % 2 == 1) ? 1.0 : -1.0) * spreadStep * ((i + 1) / 2);
            double rad = Math.toRadians(angleDeg);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double dx = dir.getX();
            double dz = dir.getZ();
            double newX = dx * cos - dz * sin;
            double newZ = dx * sin + dz * cos;
            Vector newDir = new Vector(newX, dir.getY(), newZ).normalize();
            Arrow extra = player.getWorld().spawnArrow(eyeLoc, newDir, (float) speed, 0f);
            extra.setShooter(player);
            double factor = Math.max(0.5, 0.8 - (i * 0.06));
            extra.setDamage(originalDamage * factor);
            extra.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            extra.setPersistent(false);
            if (flaming) {
                extra.setFireTicks(2000);
            }
        }
    }
}
