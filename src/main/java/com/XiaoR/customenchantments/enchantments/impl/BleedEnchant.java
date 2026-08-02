package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
public class BleedEnchant extends CustomEnchantment {
    public BleedEnchant() {
        super("bleed");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, level * 40, 0));
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (living.isDead()) {
                    cancel();
                    return;
                }
                living.damage(level * 1.0, player);
                count++;
                if (count >= level) {
                    cancel();
                }
            }
        }.runTaskTimer(CustomEnchantments.getInstance(), 20L, 20L);
    }
}
