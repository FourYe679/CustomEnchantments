package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonArrowEnchant extends CustomEnchantment {
    public PoisonArrowEnchant() {
        super("poison_arrow");
    }

    @Override
    public void onProjectileHit(Player player, Entity target, int level, ItemStack item) {
        if (target instanceof LivingEntity) {
            int amplifier = Math.min(level - 1, 4);
            int duration = level * 60;
            ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
        }
    }
}
