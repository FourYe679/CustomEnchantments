package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GravityArrowEnchant extends CustomEnchantment {
    public GravityArrowEnchant() {
        super("gravity_arrow");
    }

    @Override
    public void onProjectileHit(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        Location targetLoc = living.getLocation();
        Location playerLoc = player.getLocation();
        Vector pull = playerLoc.toVector().subtract(targetLoc.toVector());
        double length = pull.length();
        if (length < 0.0001) return;
        double strength = 1.5 + level * 0.5;
        pull.normalize().multiply(strength);
        if (pull.getY() < 0.5) {
            pull.setY(0.5);
        }
        living.setVelocity(pull);
        targetLoc.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 30, 0.5, 0.5, 0.5, 0.1);
    }
}
