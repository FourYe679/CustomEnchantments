package com.XiaoR.customenchantments.listener;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.CooldownManager;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantListener implements Listener {

    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final CooldownManager cooldownManager;

    private static final Set<String> BOW_SHOOT_ENCHANTS = Set.of("homing_arrow", "multishot_burst");
    private static final Set<String> PROJECTILE_HIT_ENCHANTS = Set.of(
            "web_shot", "poison_arrow", "gravity_arrow", "explosive_arrow"
    );
    private static final Set<String> DEATH_TRIGGER_ENCHANTS = Set.of(
            "vampirism", "soul_reap", "experience_boost", "looting_master", "magnetic"
    );
    private static final Set<String> PASSIVE_TRIGGER_ENCHANTS = Set.of("parry");

    // Equipment item suffixes — drops matching these are NOT duplicated by Looting Master
    private static final Set<String> EQUIPMENT_SUFFIXES = Set.of(
            "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS",
            "_SWORD", "_PICKAXE", "_SHOVEL", "_HOE", "_AXE",
            "_HORSE_ARMOR", "_SHULKER_BOX"
    );
    // Standalone equipment materials not caught by suffix matching
    private static final Set<Material> EQUIPMENT_MATERIALS = Set.of(
            Material.BOW, Material.CROSSBOW, Material.TRIDENT,
            Material.SHIELD, Material.ELYTRA, Material.FISHING_ROD,
            Material.SHEARS, Material.FLINT_AND_STEEL,
            Material.SADDLE, Material.ENCHANTED_BOOK,
            Material.BUNDLE
    );

    public EnchantListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
        ItemStack weapon = event.getBow();
        if (weapon == null || weapon.getType().isAir()) return;
        Map<String, Integer> enchants = enchantManager.getAllEnchants(weapon);
        if (enchants.isEmpty()) return;
        // Store the bow on the arrow so projectile-hit enchantments use the bow's enchants,
        // not whatever the player is holding when the arrow lands (fixes weapon-switching & off-hand issues)
        arrow.setMetadata("sourceBow", new FixedMetadataValue(plugin, weapon.clone()));
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String enchantId = entry.getKey();
            int level = entry.getValue();
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant == null || !enchant.isEnabled()) continue;
            if (BOW_SHOOT_ENCHANTS.contains(enchantId)) {
                if (cooldownManager.checkAndTrigger(player, enchantId, enchant.getCooldown())) {
                    enchant.onBowShoot(player, arrow, level, weapon);
                }
            }
        }
        updateCooldownLore(player, weapon, enchants);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        Player player = null;
        if (damager instanceof Player p) {
            player = p;
        } else if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player p) {
            player = p;
        }
        if (player == null) return;
        // Skip enchantment processing when damage is applied by an enchantment effect (explosion, fire storm, etc.)
        // to prevent recursive triggers that cause fake death / invincible state
        if (player.hasMetadata("applyingEnchantDamage")) return;
        boolean isRanged = damager instanceof Projectile;
        if (isRanged) return;
        if (!(victim instanceof LivingEntity)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon == null || weapon.getType().isAir()) {
            weapon = player.getInventory().getItemInOffHand();
            if (weapon == null || weapon.getType().isAir()) return;
        }
        if (weapon.getType() == Material.BOW || weapon.getType() == Material.CROSSBOW) return;
        Map<String, Integer> enchants = enchantManager.getAllEnchants(weapon);
        if (enchants.isEmpty()) return;
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String enchantId = entry.getKey();
            int level = entry.getValue();
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant == null || !enchant.isEnabled()) continue;
            if (BOW_SHOOT_ENCHANTS.contains(enchantId)) continue;
            if (PROJECTILE_HIT_ENCHANTS.contains(enchantId)) continue;
            if (DEATH_TRIGGER_ENCHANTS.contains(enchantId)) continue;
            if (PASSIVE_TRIGGER_ENCHANTS.contains(enchantId)) continue;
            if (cooldownManager.checkAndTrigger(player, enchantId, enchant.getCooldown())) {
                enchant.onActivate(player, victim, level, weapon);
            }
        }
        updateCooldownLore(player, weapon, enchants);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) return;
        if (!(projectile instanceof AbstractArrow arrow)) return;
        // Retrieve the bow stored on the arrow at shoot time (fixes weapon-switching & off-hand issues)
        ItemStack weapon = null;
        if (arrow.hasMetadata("sourceBow")) {
            Object stored = arrow.getMetadata("sourceBow").get(0).value();
            if (stored instanceof ItemStack bowItem && !bowItem.getType().isAir()) {
                weapon = bowItem;
            }
        }
        // Fallback: read from current hand (for arrows shot by other plugins or before this fix)
        if (weapon == null || weapon.getType().isAir()) {
            weapon = player.getInventory().getItemInMainHand();
            if (weapon == null || weapon.getType().isAir()) {
                weapon = player.getInventory().getItemInOffHand();
                if (weapon == null || weapon.getType().isAir()) return;
            }
        }
        Map<String, Integer> enchants = enchantManager.getAllEnchants(weapon);
        if (enchants.isEmpty()) return;
        Entity hitEntity = event.getHitEntity();
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String enchantId = entry.getKey();
            int level = entry.getValue();
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant == null || !enchant.isEnabled()) continue;
            if (BOW_SHOOT_ENCHANTS.contains(enchantId)) continue;
            if (DEATH_TRIGGER_ENCHANTS.contains(enchantId)) continue;
            if (PASSIVE_TRIGGER_ENCHANTS.contains(enchantId)) continue;
            if (PROJECTILE_HIT_ENCHANTS.contains(enchantId)) {
                if (cooldownManager.checkAndTrigger(player, enchantId, enchant.getCooldown())) {
                    enchant.onProjectileHit(player, hitEntity != null ? hitEntity : arrow, level, weapon);
                }
            } else {
                if (hitEntity != null && hitEntity instanceof LivingEntity) {
                    if (cooldownManager.checkAndTrigger(player, enchantId, enchant.getCooldown())) {
                        enchant.onActivate(player, hitEntity, level, weapon);
                    }
                }
            }
        }
        updateCooldownLore(player, weapon, enchants);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (weapon == null || weapon.getType().isAir()) return;
        Map<String, Integer> enchants = enchantManager.getAllEnchants(weapon);
        if (enchants.isEmpty()) return;
        Integer lootingLevel = enchants.get("looting_master");
        if (lootingLevel != null && lootingLevel > 0) {
            CustomEnchantment lootingEnchant = enchantManager.getEnchant("looting_master");
            if (lootingEnchant != null && lootingEnchant.isEnabled()) {
                if (cooldownManager.checkAndTrigger(killer, "looting_master", lootingEnchant.getCooldown())) {
                    List<ItemStack> drops = event.getDrops();
                    List<ItemStack> bonusDrops = new ArrayList<>();
                    for (ItemStack drop : drops) {
                        if (drop == null || drop.getType().isAir()) continue;
                        // Skip equipment — Looting Master only duplicates natural loot, not worn gear
                        if (isEquipment(drop.getType())) continue;
                        ItemStack bonus = drop.clone();
                        bonus.setAmount(lootingLevel);
                        bonusDrops.add(bonus);
                    }
                    drops.addAll(bonusDrops);
                    killer.playSound(killer.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String enchantId = entry.getKey();
            int level = entry.getValue();
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant == null || !enchant.isEnabled()) continue;
            if (!DEATH_TRIGGER_ENCHANTS.contains(enchantId)) continue;
            if (enchantId.equals("looting_master")) continue;
            if (cooldownManager.checkAndTrigger(killer, enchantId, enchant.getCooldown())) {
                enchant.onActivate(killer, victim, level, weapon);
            }
        }
        updateCooldownLore(killer, weapon, enchants);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon == null || weapon.getType().isAir()) return;
        Map<String, Integer> enchants = enchantManager.getAllEnchants(weapon);
        if (enchants.isEmpty()) return;
        Integer parryLevel = enchants.get("parry");
        if (parryLevel == null || parryLevel <= 0) return;
        CustomEnchantment parryEnchant = enchantManager.getEnchant("parry");
        if (parryEnchant == null || !parryEnchant.isEnabled()) return;
        if (cooldownManager.checkAndTrigger(player, "parry", parryEnchant.getCooldown())) {
            parryEnchant.onActivate(player, event.getDamager(), parryLevel, weapon);
            updateCooldownLore(player, weapon, enchants);
        }
    }

    private boolean isEquipment(Material material) {
        if (material == null || material.isAir()) return false;
        if (EQUIPMENT_MATERIALS.contains(material)) return true;
        String name = material.name();
        for (String suffix : EQUIPMENT_SUFFIXES) {
            if (name.endsWith(suffix)) return true;
        }
        return false;
    }

    private void updateCooldownLore(Player player, ItemStack item, Map<String, Integer> enchants) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> ChatColor.stripColor(line).startsWith("\u2726 "));
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String enchantId = entry.getKey();
            int level = entry.getValue();
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant == null) continue;
            String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
            if (item.getType() == Material.ENCHANTED_BOOK) {
                int cd = enchant.getCooldown();
                String statusLine;
                if (cd <= 0) {
                    statusLine = ChatColor.GREEN + "\u2726 " + displayName + " " + ChatColor.GRAY + "Lv." + level + " " + ChatColor.GREEN + "[\u5C31\u7EEA]";
                } else if (cooldownManager.isOnCooldown(player, enchantId)) {
                    long remaining = cooldownManager.getRemainingCooldown(player, enchantId);
                    statusLine = ChatColor.RED + "\u2726 " + displayName + " " + ChatColor.GRAY + "Lv." + level + " " + ChatColor.RED + "[CD: " + remaining + "s]";
                } else {
                    statusLine = ChatColor.GREEN + "\u2726 " + displayName + " " + ChatColor.GRAY + "Lv." + level + " " + ChatColor.GREEN + "[\u5C31\u7EEA]";
                }
                lore.add(statusLine);
            } else {
                String line = ChatColor.LIGHT_PURPLE + "\u2726 " + displayName + " " + ChatColor.GRAY + "Lv." + level;
                lore.add(line);
            }
        }
        meta.setLore(lore.isEmpty() ? null : lore);
        item.setItemMeta(meta);
    }
}
