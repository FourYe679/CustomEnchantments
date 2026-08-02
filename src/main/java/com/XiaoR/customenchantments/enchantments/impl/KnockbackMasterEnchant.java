package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
public class KnockbackMasterEnchant extends CustomEnchantment {
    public KnockbackMasterEnchant() {
        super("knockback_master");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector());
        if (direction.lengthSquared() == 0) return;
        direction.normalize();
        double strength = 1.5 + level * 0.5;
        living.setVelocity(direction.multiply(strength));
    }
}
