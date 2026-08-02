package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class RegenerationArmorEnchant extends CustomEnchantment {
    public RegenerationArmorEnchant() {
        super("regeneration");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
    }
}
