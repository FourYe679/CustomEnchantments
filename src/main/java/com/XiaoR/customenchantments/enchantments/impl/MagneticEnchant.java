package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
public class MagneticEnchant extends CustomEnchantment {
    public MagneticEnchant() {
        super("magnetic");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        Location playerLoc = player.getLocation();
        double radius = 3 + level * 2;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Item) {
                Location itemLoc = entity.getLocation();
                Vector velocity = playerLoc.toVector().subtract(itemLoc.toVector());
                if (velocity.lengthSquared() == 0) continue;
                velocity.normalize().multiply(0.5);
                entity.setVelocity(velocity);
            }
        }
    }
}
