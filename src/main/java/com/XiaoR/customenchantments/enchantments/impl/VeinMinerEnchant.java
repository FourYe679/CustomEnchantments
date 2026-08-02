package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VeinMinerEnchant extends CustomEnchantment {
    public VeinMinerEnchant() {
        super("vein_miner");
    }

    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
    }

    @Override
    public void onProjectileHit(Player player, Entity target, int level, ItemStack item) {
    }

    @Override
    public void onBowShoot(Player player, org.bukkit.entity.AbstractArrow arrow, int level, ItemStack item) {
    }
}
