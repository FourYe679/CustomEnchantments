package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class LootingMasterEnchant extends CustomEnchantment {
    public LootingMasterEnchant() {
        super("looting_master");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
    }
}
