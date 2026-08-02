package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.manager.CooldownManager;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class ArmorEffectListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final CooldownManager cooldownManager;
    private BukkitTask armorCheckTask;
    private final Set<UUID> processingDamage = ConcurrentHashMap.newKeySet();
    // Tracks players who currently have the invisibility enchant active (hidden from others)
    private final Set<UUID> invisiblePlayers = ConcurrentHashMap.newKeySet();
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
        }, 20L, 10L);
    }
    private void checkArmorEffects(Player player) {
        int speedLevel = 0;
        int jumpLevel = 0;
        int nightVisionLevel = 0;
        int regenLevel = 0;
        int resistanceLevel = 0;
        int aquaAffinityLevel = 0;
        int featherFallLevel = 0;
        int invisibilityLevel = 0;
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
                int lvl = enchants.get("night_vision");
                if (lvl > nightVisionLevel) nightVisionLevel = lvl;
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
                int lvl = enchants.get("aqua_affinity");
                if (lvl > aquaAffinityLevel) aquaAffinityLevel = lvl;
            }
            if (enchants.containsKey("feather_fall")) {
                int lvl = enchants.get("feather_fall");
                if (lvl > featherFallLevel) featherFallLevel = lvl;
            }
            if (enchants.containsKey("invisibility")) {
                int lvl = enchants.get("invisibility");
                if (lvl > invisibilityLevel) invisibilityLevel = lvl;
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
            int duration = speedLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, speedLevel - 1, false, false));
        }
        if (jumpLevel > 0) {
            int duration = jumpLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, jumpLevel - 1, false, false));
        }
        if (nightVisionLevel > 0) {
            int duration = nightVisionLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0, false, false));
            if (nightVisionLevel >= 2) {
                int hasteAmplifier = Math.min(nightVisionLevel - 2, 3);
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, hasteAmplifier, false, false));
            }
        }
        if (regenLevel > 0) {
            int duration = regenLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, regenLevel - 1, false, false));
        }
        if (resistanceLevel > 0) {
            int potionAmplifier = Math.min(resistanceLevel - 1, 4);
            int duration = resistanceLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, potionAmplifier, false, false));
        }
        if (aquaAffinityLevel > 0) {
            int duration = aquaAffinityLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, 0, false, false));
            int hasteAmplifier = Math.min(aquaAffinityLevel - 1, 3);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, hasteAmplifier, false, false));
        }
        if (featherFallLevel > 0) {
            if (player.getFallDistance() > 2) {
                int duration = featherFallLevel * 200;
                int slowFallAmplifier = Math.min(featherFallLevel - 1, 4);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, slowFallAmplifier, false, false));
            }
        }
        if (invisibilityLevel > 0) {
            int duration = invisibilityLevel * 200;
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
            // setInvisible hides the body; hidePlayer hides armor + held items from other players
            player.setInvisible(true);
            // Only transition to hidden once (avoid re-sending packets every 10 ticks)
            if (!invisiblePlayers.contains(player.getUniqueId())) {
                invisiblePlayers.add(player.getUniqueId());
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other != player && other.canSee(player)) {
                        other.hidePlayer(plugin, player);
                    }
                }
            }
            if (invisibilityLevel >= 2) {
                int speedAmplifier = Math.min(invisibilityLevel - 2, 3);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, speedAmplifier, false, false));
            }
        } else {
            // Restore full visibility when enchant is no longer active
            player.setInvisible(false);
            if (invisiblePlayers.remove(player.getUniqueId())) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other != player) {
                        other.showPlayer(plugin, player);
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (processingDamage.contains(player.getUniqueId())) return;
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
            // Cap fire ticks to prevent excessive burn times (max 10 seconds = 200 ticks)
            int fireTicks = Math.min(moltenLevel * 40, 200);
            // Reduce fire duration if the attacker has Fire Protection enchantments
            if (attacker instanceof org.bukkit.entity.Player attackedPlayer) {
                int fireProtLevel = getFireProtectionLevel(attackedPlayer);
                if (fireProtLevel > 0) {
                    // Each level of Fire Protection reduces burn time by ~15%
                    double reduction = Math.min(fireProtLevel * 0.15, 0.95);
                    fireTicks = (int) (fireTicks * (1.0 - reduction));
                }
            }
            if (fireTicks > 0) {
                // Only set fire if it would be longer than current fire
                if (attacker.getFireTicks() < fireTicks) {
                    attacker.setFireTicks(fireTicks);
                }
            }
        }
        if (thornsLevel > 0) {
            double reflectedDamage = thornsLevel * 1.5;
            processingDamage.add(attacker.getUniqueId());
            try {
                attacker.damage(reflectedDamage, player);
            } finally {
                processingDamage.remove(attacker.getUniqueId());
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        int featherFallLevel = 0;
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            Map<String, Integer> enchants = enchantManager.getAllEnchants(armorPiece);
            if (enchants.containsKey("feather_fall")) {
                int lvl = enchants.get("feather_fall");
                if (lvl > featherFallLevel) featherFallLevel = lvl;
            }
        }
        if (featherFallLevel > 0) {
            double reduction = Math.min(featherFallLevel * 0.2, 1.0);
            double newDamage = event.getDamage() * (1.0 - reduction);
            event.setDamage(newDamage);
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // Totem of Undying clears all potion effects (vanilla behavior).
        // Re-apply armor effects immediately to minimize the buff gap.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                checkArmorEffects(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 2L);
    }
    private int getFireProtectionLevel(Player player) {
        int totalLevel = 0;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            ItemMeta meta = armorPiece.getItemMeta();
            if (meta == null) continue;
            // Check both direct enchants and stored enchants (for enchanted books applied via anvil)
            int level = meta.getEnchantLevel(Enchantment.FIRE_PROTECTION);
            totalLevel += level;
        }
        return totalLevel;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Hide all currently-invisible players from the newcomer
        Player joined = event.getPlayer();
        for (UUID invUuid : invisiblePlayers) {
            Player invisible = Bukkit.getPlayer(invUuid);
            if (invisible != null && invisible != joined && joined.canSee(invisible)) {
                joined.hidePlayer(plugin, invisible);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove from tracking; visibility will be re-evaluated when they rejoin
        invisiblePlayers.remove(event.getPlayer().getUniqueId());
    }

    public void shutdown() {
        if (armorCheckTask != null) {
            armorCheckTask.cancel();
            armorCheckTask = null;
        }
        // Restore visibility for all players who were hidden by the invisibility enchant
        for (UUID invUuid : invisiblePlayers) {
            Player invisible = Bukkit.getPlayer(invUuid);
            if (invisible != null) {
                invisible.setInvisible(false);
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other != invisible) {
                        other.showPlayer(plugin, invisible);
                    }
                }
            }
        }
        invisiblePlayers.clear();
    }
}
