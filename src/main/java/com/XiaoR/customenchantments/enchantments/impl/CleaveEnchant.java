package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
public class CleaveEnchant extends CustomEnchantment {
    public CleaveEnchant() {
        super("cleave");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        double radius = 2 + level;
        Collection<LivingEntity> nearby = target.getWorld().getNearbyLivingEntities(target.getLocation(), radius);
        for (LivingEntity entity : nearby) {
            if (entity == player || entity == target) continue;
            entity.damage(level * 2.0, player);
        }
        target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation(), 1);
    }
}
