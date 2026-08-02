package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class ExperienceBoostEnchant extends CustomEnchantment {
    public ExperienceBoostEnchant() {
        super("experience_boost");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null) return;
        int bonusExp = level * 3;
        player.giveExp(bonusExp);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}
