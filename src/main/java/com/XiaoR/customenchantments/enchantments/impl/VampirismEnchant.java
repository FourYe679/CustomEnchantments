package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import io.papermc.paper.registry.keys.AttributeKeys;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class VampirismEnchant extends CustomEnchantment {
    public VampirismEnchant() {
        super("vampirism");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null) return;
        double healAmount = level * 4.0;
        double playerMaxHealth = player.getAttribute(Registry.ATTRIBUTE.get(AttributeKeys.MAX_HEALTH)).getValue();
        double newHealth = Math.min(player.getHealth() + healAmount, playerMaxHealth);
        player.setHealth(newHealth);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, level * 20, 0));
    }
}
