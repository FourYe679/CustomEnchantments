package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.metadata.FixedMetadataValue;
import java.util.ArrayList;
import java.util.List;
public class FireStormEnchant extends CustomEnchantment {
    public FireStormEnchant() {
        super("fire_storm");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        int radius = Math.min(2 + level, 12);
        int duration = Math.min(level * 2, 20) * 20;
        long fireDurationTicks = 200L;
        List<Location> fireBlocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    Block block = world.getBlockAt(targetLoc.clone().add(x, 0, z));
                    if (block.getType().isAir()) {
                        block.setType(Material.FIRE);
                        fireBlocks.add(block.getLocation());
                    }
                }
            }
        }
        if (!fireBlocks.isEmpty()) {
            // Give the player temporary fire resistance so they can walk through their own fire safely
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (int) fireDurationTicks, 0, false, false, true));
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Location fireLoc : fireBlocks) {
                        if (!fireLoc.getChunk().isLoaded()) continue;
                        Block fireBlock = fireLoc.getBlock();
                        if (fireBlock.getType() == Material.FIRE) {
                            fireBlock.setType(Material.AIR);
                        }
                    }
                }
            }.runTaskLater(CustomEnchantments.getInstance(), fireDurationTicks);
        }
        world.spawnParticle(Particle.FLAME, targetLoc, 50, radius, radius / 2.0, radius);
        CustomEnchantments plugin = CustomEnchantments.getInstance();
        player.setMetadata("applyingEnchantDamage", new FixedMetadataValue(plugin, true));
        try {
            for (LivingEntity entity : world.getNearbyLivingEntities(targetLoc, radius)) {
                if (entity != player) {
                    if (entity.isDead() || entity.getHealth() <= 0) continue;
                    int fireTicks = duration;
                    // Reduce fire duration if the entity has Fire Protection
                    if (entity instanceof Player p) {
                        int fireProtLevel = getFireProtectionLevel(p);
                        if (fireProtLevel > 0) {
                            double reduction = Math.min(fireProtLevel * 0.15, 0.95);
                            fireTicks = (int) (fireTicks * (1.0 - reduction));
                        }
                    }
                    if (fireTicks > 0 && entity.getFireTicks() < fireTicks) {
                        entity.setFireTicks(fireTicks);
                    }
                    // Deal instant fire damage in addition to the burn effect
                    entity.damage(level * 2.0, player);
                }
            }
        } finally {
            player.removeMetadata("applyingEnchantDamage", plugin);
        }
    }
    private int getFireProtectionLevel(Player player) {
        int totalLevel = 0;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            ItemMeta meta = armorPiece.getItemMeta();
            if (meta == null) continue;
            totalLevel += meta.getEnchantLevel(Enchantment.FIRE_PROTECTION);
        }
        return totalLevel;
    }
}
