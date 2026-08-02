package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class JumpBoostArmorEnchant extends CustomEnchantment {
    public JumpBoostArmorEnchant() {
        super("jump_boost");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
    }
}
