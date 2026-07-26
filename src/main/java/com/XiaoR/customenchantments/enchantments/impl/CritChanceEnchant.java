package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Random;
public class CritChanceEnchant extends CustomEnchantment {
    private final Random random = new Random();
    public CritChanceEnchant() {
        super("crit_chance");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null) return;
        double chance = Math.min(0.1 + level * 0.05, 0.6);
        if (random.nextDouble() < chance) {
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).damage(level * 3.0, player);
            }
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation(), 20);
        }
    }
}
