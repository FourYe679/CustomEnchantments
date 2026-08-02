package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
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
        if (target instanceof Villager) return;
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
            // Use visual-only lightning to prevent nearby villagers from turning into witches
            world.strikeLightningEffect(strikeLoc);
        }
        // Apply lightning damage directly to the target (villagers already excluded above)
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.damage(level * 3.0, player);
            livingTarget.setFireTicks(80);
        }
    }
}
