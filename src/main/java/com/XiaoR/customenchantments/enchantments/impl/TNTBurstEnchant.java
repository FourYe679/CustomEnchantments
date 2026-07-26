package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import java.util.Random;
public class TNTBurstEnchant extends CustomEnchantment {
    private final Random random = new Random();
    public TNTBurstEnchant() {
        super("tnt_burst");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        for (int i = 0; i < level; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2;
            double offsetZ = (random.nextDouble() - 0.5) * 2;
            Location spawnLoc = targetLoc.clone().add(offsetX, 0, offsetZ);
            TNTPrimed tnt = world.spawn(spawnLoc, TNTPrimed.class);
            tnt.setFuseTicks(20);
            tnt.setVelocity(new Vector(0, 0.5, 0));
        }
    }
}
