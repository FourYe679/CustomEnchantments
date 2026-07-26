package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        double previousHealth = player.getHealth();
        int previousFireTicks = player.getFireTicks();
        world.createExplosion(targetLoc, power, false, false);
        if (player.getHealth() < previousHealth) {
            player.setHealth(previousHealth);
        }
        player.setFireTicks(previousFireTicks);
        world.playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, targetLoc, 10);
    }
}
