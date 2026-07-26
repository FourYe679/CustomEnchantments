package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class ExecuteEnchant extends CustomEnchantment {
    public ExecuteEnchant() {
        super("execute");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        double maxHealth = living.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (maxHealth <= 0) return;
        if (living.getHealth() / maxHealth < 0.3) {
            living.damage(level * 5.0, player);
        }
    }
}
