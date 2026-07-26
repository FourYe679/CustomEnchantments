package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class FireStormEnchant extends CustomEnchantment {
    public FireStormEnchant() {
        super("fire_storm");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        int radius = Math.min(2 + level, 12);
        int duration = Math.min(level * 2, 20) * 20;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    Block block = world.getBlockAt(targetLoc.clone().add(x, 0, z));
                    if (block.getType().isAir()) {
                        block.setType(Material.FIRE);
                    }
                }
            }
        }
        world.spawnParticle(Particle.FLAME, targetLoc, 50, radius, radius / 2.0, radius);
        for (LivingEntity entity : world.getNearbyLivingEntities(targetLoc, radius)) {
            if (entity != player && entity != target) {
                entity.setFireTicks(duration);
            }
        }
    }
}
