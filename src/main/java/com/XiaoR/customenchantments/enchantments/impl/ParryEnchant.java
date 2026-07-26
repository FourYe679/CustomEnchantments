package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class ParryEnchant extends CustomEnchantment {
    public ParryEnchant() {
        super("parry");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        int amplifier = Math.min(level - 1, 4);
        int duration = level * 20;
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, amplifier));
    }
}
