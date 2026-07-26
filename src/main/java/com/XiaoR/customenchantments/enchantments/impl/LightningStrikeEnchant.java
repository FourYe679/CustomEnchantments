package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Random;
public class LightningStrikeEnchant extends CustomEnchantment {
    private final Random random = new Random();
    public LightningStrikeEnchant() {
        super("lightning_strike");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        int lightningCount = level;
        for (int i = 0; i < lightningCount; i++) {
            Location strikeLoc;
            if (i == 0) {
                strikeLoc = targetLoc.clone();
            } else {
                double offsetX = (random.nextDouble() - 0.5) * level * 2;
                double offsetZ = (random.nextDouble() - 0.5) * level * 2;
                strikeLoc = targetLoc.clone().add(offsetX, 0, offsetZ);
            }
            world.strikeLightning(strikeLoc);
        }
    }
}
