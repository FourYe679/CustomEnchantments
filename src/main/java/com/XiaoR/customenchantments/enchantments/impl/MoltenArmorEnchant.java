package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class MoltenArmorEnchant extends CustomEnchantment {
    public MoltenArmorEnchant() {
        super("molten_armor");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target != null && target instanceof LivingEntity) {
            target.setFireTicks(level * 40);
        }
    }
}
