package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class WebShotEnchant extends CustomEnchantment {
    public WebShotEnchant() {
        super("web_shot");
    }

    @Override
    public void onProjectileHit(Player player, Entity target, int level, ItemStack item) {
        if (target == null) return;
        Location loc = target.getLocation();
        if (!loc.getChunk().isLoaded()) return;
        World world = loc.getWorld();
        if (world == null) return;
        int radius = Math.min(1 + level / 2, 3);
        Map<Location, Material> original = new HashMap<>();
        int bx = loc.getBlockX();
        int by = loc.getBlockY();
        int bz = loc.getBlockZ();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = world.getBlockAt(bx + x, by + y, bz + z);
                    if (isReplaceable(block)) {
                        original.put(block.getLocation(), block.getType());
                        block.setType(Material.COBWEB);
                    }
                }
            }
        }
        if (original.isEmpty()) return;
        long restoreDelay = level * 3 * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Location, Material> entry : original.entrySet()) {
                    Location restoreLoc = entry.getKey();
                    if (!restoreLoc.getChunk().isLoaded()) continue;
                    Block block = restoreLoc.getBlock();
                    if (block.getType() == Material.COBWEB) {
                        block.setType(entry.getValue());
                    }
                }
            }
        }.runTaskLater(CustomEnchantments.getInstance(), restoreDelay);
    }

    private boolean isReplaceable(Block block) {
        Material type = block.getType();
        return type.isAir() || type == Material.WATER || type == Material.LAVA;
    }
}
