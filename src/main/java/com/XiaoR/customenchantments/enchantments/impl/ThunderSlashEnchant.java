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
public class ThunderSlashEnchant extends CustomEnchantment {
    private final Random random = new Random();
    public ThunderSlashEnchant() {
        super("thunder_slash");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        if (target instanceof Villager) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        double radius = 2 + level;
        world.strikeLightning(targetLoc);
        for (int i = 0; i < level; i++) {
            double offsetX = (random.nextDouble() * 2 - 1) * radius;
            double offsetZ = (random.nextDouble() * 2 - 1) * radius;
            Location strikeLoc = targetLoc.clone().add(offsetX, 0, offsetZ);
            world.strikeLightning(strikeLoc);
        }
    }
}
