package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class FeatherFallEnchant extends CustomEnchantment {
    public FeatherFallEnchant() {
        super("feather_fall");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (player == null) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, level * 60, 0));
    }
}
