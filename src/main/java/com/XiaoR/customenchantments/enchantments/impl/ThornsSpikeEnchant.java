package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class ThornsSpikeEnchant extends CustomEnchantment {
    public ThornsSpikeEnchant() {
        super("thorns_spike");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).damage(level * 1.5, player);
        }
    }
}
