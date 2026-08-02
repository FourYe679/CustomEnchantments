package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
public class InvisibilityCloakEnchant extends CustomEnchantment {
    public InvisibilityCloakEnchant() {
        super("invisibility");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (player == null) return;
        int duration = level * 100;
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
                }
            }
        }.runTaskLater(CustomEnchantments.getInstance(), duration);
    }
}
