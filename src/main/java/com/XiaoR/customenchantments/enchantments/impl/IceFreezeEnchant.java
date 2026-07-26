package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;
public class IceFreezeEnchant extends CustomEnchantment {
    public IceFreezeEnchant() {
        super("ice_freeze");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (!(target instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) target;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        List<Block> changedBlocks = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = world.getBlockAt(targetLoc.clone().add(x, y, z));
                    if (block.getType().isAir()) {
                        block.setType(Material.BLUE_ICE);
                        changedBlocks.add(block);
                    }
                }
            }
        }
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, level * 2 * 20, level));
        living.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, level * 2 * 20, -128));
        world.spawnParticle(Particle.SNOWFLAKE, targetLoc, 30, 0.5, 0.5, 0.5);
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 10 || living.isDead()) {
                    cancel();
                    return;
                }
                living.setVelocity(new Vector(0, living.getVelocity().getY(), 0));
                ticks++;
            }
        }.runTaskTimer(CustomEnchantments.getInstance(), 0L, 1L);
        int restoreDelay = level * 3 * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : changedBlocks) {
                    if (block.getType() == Material.BLUE_ICE) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(CustomEnchantments.getInstance(), restoreDelay);
    }
}
