package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class ExplosiveArrowEnchant extends CustomEnchantment {
    public ExplosiveArrowEnchant() {
        super("explosive_arrow");
    }
    @Override
    public void onProjectileHit(Player player, Entity target, int level, ItemStack item) {
        if (target == null) return;
        Location loc = target.getLocation();
        World world = loc.getWorld();
        if (world == null) return;
        float power = (float) Math.min(2 + level, 8);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, loc, 10);
        ExplosionEnchant.applyExplosionDamage(player, loc, power, world);
    }
}
