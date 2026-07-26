package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class WitherTouchEnchant extends CustomEnchantment {
    public WitherTouchEnchant() {
        super("wither_touch");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        int amplifier = Math.min(level - 1, 3);
        int duration = level * 2 * 20;
        living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, amplifier));
    }
}
