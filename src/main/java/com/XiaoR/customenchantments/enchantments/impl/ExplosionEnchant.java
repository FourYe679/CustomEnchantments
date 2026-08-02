package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
public class ExplosionEnchant extends CustomEnchantment {
    public ExplosionEnchant() {
        super("explosion");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        float power = (float) Math.min(2 + level, 8);
        world.playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, targetLoc, 10);
        applyExplosionDamage(player, targetLoc, power, world);
    }

    static void applyExplosionDamage(Player source, Location center, float power, World world) {
        CustomEnchantments plugin = CustomEnchantments.getInstance();
        source.setMetadata("applyingEnchantDamage", new FixedMetadataValue(plugin, true));
        try {
            double radius = power * 2.0;
            for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
                if (entity == source) continue;
                if (!(entity instanceof LivingEntity living)) continue;
                if (living.isDead() || living.getHealth() <= 0) continue;
                double distance = living.getLocation().distance(center);
                if (distance > radius) continue;
                double damage = power * 4.0 * (1.0 - distance / radius);
                if (damage > 0) {
                    living.damage(damage, source);
                    // Apply knockback away from explosion center
                    Vector knockback = living.getLocation().toVector().subtract(center.toVector());
                    if (knockback.lengthSquared() > 0) {
                        knockback.normalize().multiply(Math.min(power * 0.3, 1.0));
                        knockback.setY(0.2 + power * 0.05);
                        living.setVelocity(knockback);
                    }
                }
            }
        } finally {
            source.removeMetadata("applyingEnchantDamage", plugin);
        }
    }
}
