package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.manager.CooldownManager;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import java.util.Map;
public class ArmorEffectListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final CooldownManager cooldownManager;
    private BukkitTask armorCheckTask;
    public ArmorEffectListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
        this.cooldownManager = plugin.getCooldownManager();
        startArmorCheckTask();
    }
    private void startArmorCheckTask() {
        armorCheckTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    checkArmorEffects(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 40L, 40L);
    }
    private void checkArmorEffects(Player player) {
        int speedLevel = 0;
        int jumpLevel = 0;
        boolean hasNightVision = false;
        int regenLevel = 0;
        int resistanceLevel = 0;
        boolean hasAquaAffinity = false;
        boolean hasFeatherFall = false;
        boolean hasInvisibility = false;
        int moltenLevel = 0;
        int thornsLevel = 0;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            Map<String, Integer> enchants = enchantManager.getAllEnchants(armorPiece);
            if (enchants.isEmpty()) continue;
            if (enchants.containsKey("speed_boost")) {
                int lvl = enchants.get("speed_boost");
                if (lvl > speedLevel) speedLevel = lvl;
            }
            if (enchants.containsKey("jump_boost")) {
                int lvl = enchants.get("jump_boost");
                if (lvl > jumpLevel) jumpLevel = lvl;
            }
            if (enchants.containsKey("night_vision")) {
                hasNightVision = true;
            }
            if (enchants.containsKey("regeneration")) {
                int lvl = enchants.get("regeneration");
                if (lvl > regenLevel) regenLevel = lvl;
            }
            if (enchants.containsKey("resistance")) {
                int lvl = enchants.get("resistance");
                if (lvl > resistanceLevel) resistanceLevel = lvl;
            }
            if (enchants.containsKey("aqua_affinity")) {
                hasAquaAffinity = true;
            }
            if (enchants.containsKey("feather_fall")) {
                hasFeatherFall = true;
            }
            if (enchants.containsKey("invisibility")) {
                hasInvisibility = true;
            }
            if (enchants.containsKey("molten_armor")) {
                int lvl = enchants.get("molten_armor");
                if (lvl > moltenLevel) moltenLevel = lvl;
            }
            if (enchants.containsKey("thorns_spike")) {
                int lvl = enchants.get("thorns_spike");
                if (lvl > thornsLevel) thornsLevel = lvl;
            }
        }
        if (speedLevel > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, speedLevel - 1, false, false));
        }
        if (jumpLevel > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 60, jumpLevel - 1, false, false));
        }
        if (hasNightVision) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, false, false));
        }
        if (regenLevel > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, regenLevel - 1, false, false));
        }
        if (resistanceLevel > 0) {
            int potionAmplifier = Math.min(resistanceLevel - 1, 4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, potionAmplifier, false, false));
        }
        if (hasAquaAffinity) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 80, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 80, 0, false, false));
        }
        if (hasFeatherFall) {
            if (player.getFallDistance() > 2) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0, false, false));
            }
        }
        if (hasInvisibility) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        org.bukkit.entity.Entity damagerEntity = event.getDamager();
        LivingEntity attacker = null;
        if (damagerEntity instanceof LivingEntity le) {
            attacker = le;
        } else if (damagerEntity instanceof org.bukkit.entity.Projectile proj
                && proj.getShooter() instanceof LivingEntity le) {
            attacker = le;
        }
        if (attacker == null) return;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        int moltenLevel = 0;
        int thornsLevel = 0;
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            Map<String, Integer> enchants = enchantManager.getAllEnchants(armorPiece);
            if (enchants.containsKey("molten_armor")) {
                int lvl = enchants.get("molten_armor");
                if (lvl > moltenLevel) moltenLevel = lvl;
            }
            if (enchants.containsKey("thorns_spike")) {
                int lvl = enchants.get("thorns_spike");
                if (lvl > thornsLevel) thornsLevel = lvl;
            }
        }
        if (moltenLevel > 0) {
            attacker.setFireTicks(moltenLevel * 40);
        }
        if (thornsLevel > 0) {
            double reflectedDamage = thornsLevel * 1.5;
            attacker.damage(reflectedDamage, player);
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            Map<String, Integer> enchants = enchantManager.getAllEnchants(armorPiece);
            if (enchants.containsKey("feather_fall")) {
                event.setDamage(0);
                return;
            }
        }
    }
    public void shutdown() {
        if (armorCheckTask != null) {
            armorCheckTask.cancel();
            armorCheckTask = null;
        }
    }
}
