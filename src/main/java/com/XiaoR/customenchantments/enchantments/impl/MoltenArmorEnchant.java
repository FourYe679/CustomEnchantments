package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public class MoltenArmorEnchant extends CustomEnchantment {
    public MoltenArmorEnchant() {
        super("molten_armor");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target != null && target instanceof LivingEntity livingTarget) {
            // Cap fire ticks to prevent excessive burn times (max 10 seconds = 200 ticks)
            int fireTicks = Math.min(level * 40, 200);
            // Reduce fire duration if the target has Fire Protection enchantments
            if (target instanceof Player targetPlayer) {
                int fireProtLevel = getFireProtectionLevel(targetPlayer);
                if (fireProtLevel > 0) {
                    double reduction = Math.min(fireProtLevel * 0.15, 0.95);
                    fireTicks = (int) (fireTicks * (1.0 - reduction));
                }
            }
            if (fireTicks > 0 && livingTarget.getFireTicks() < fireTicks) {
                livingTarget.setFireTicks(fireTicks);
            }
        }
    }
    private int getFireProtectionLevel(Player player) {
        int totalLevel = 0;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            ItemMeta meta = armorPiece.getItemMeta();
            if (meta == null) continue;
            totalLevel += meta.getEnchantLevel(Enchantment.FIRE_PROTECTION);
        }
        return totalLevel;
    }
}
