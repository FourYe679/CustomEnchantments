package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
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
        // Apply slowness and freeze the target in place (no blue ice blocks)
        int duration = level * 2 * 20;
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, level));
        living.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, -128));
        living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, level));
        world.spawnParticle(Particle.SNOWFLAKE, targetLoc, 30, 0.5, 0.5, 0.5);
        // Freeze target in place for a short duration
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
    }
}
