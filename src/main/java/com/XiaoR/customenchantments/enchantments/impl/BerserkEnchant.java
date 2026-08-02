package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import io.papermc.paper.registry.keys.AttributeKeys;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class BerserkEnchant extends CustomEnchantment {
    public BerserkEnchant() {
        super("berserk");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        double maxHealth = player.getAttribute(Registry.ATTRIBUTE.get(AttributeKeys.MAX_HEALTH)).getValue();
        double currentHealth = player.getHealth();
        double healthPercent = currentHealth / maxHealth;
        double bonusDamage = 0;
        if (healthPercent < 0.25) {
            bonusDamage = level * 4;
        } else if (healthPercent < 0.5) {
            bonusDamage = level * 2;
        }
        if (bonusDamage > 0) {
            ((LivingEntity) target).damage(bonusDamage, player);
        }
    }
}
