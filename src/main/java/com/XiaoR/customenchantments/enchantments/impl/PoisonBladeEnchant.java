package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class PoisonBladeEnchant extends CustomEnchantment {
    public PoisonBladeEnchant() {
        super("poison_blade");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        int amplifier = Math.min(level - 1, 4);
        int duration = level * 3 * 20;
        living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
    }
}
