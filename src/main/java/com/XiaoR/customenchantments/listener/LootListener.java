package com.XiaoR.customenchantments.listener;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * 在战利品箱子（村庄箱子、遗迹、地牢等建筑内的宝箱）中
 * 按概率生成自定义附魔书。
 */
public class LootListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final NamespacedKey enchantIdKey;
    private final Random random = new Random();

    public LootListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
        this.enchantIdKey = new NamespacedKey(plugin, "custom_enchant");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("loot-chest.enabled", false)) return;

        // Check world whitelist
        List<String> worlds = config.getStringList("loot-chest.enabled-worlds");
        if (worlds != null && !worlds.isEmpty()) {
            boolean allowed = false;
            String worldName = event.getEntity().getWorld().getName();
            for (String w : worlds) {
                if (w.equalsIgnoreCase(worldName)) { allowed = true; break; }
            }
            if (!allowed) return;
        }

        double chance = config.getDouble("loot-chest.chance", 0.08);
        if (random.nextDouble() >= chance) return;

        // Only add to container-based loot (chests, barrels, etc.)
        if (!(event.getInventoryHolder() instanceof InventoryHolder)) return;

        List<String> whitelist = config.getStringList("loot-chest.whitelist");
        List<CustomEnchantment> candidates = getValidEnchants(whitelist);
        if (candidates.isEmpty()) return;

        CustomEnchantment selected = candidates.get(random.nextInt(candidates.size()));
        // min/max from config, but capped by enchantment's own max-level from enchantments.yml
        int configMin = Math.max(1, config.getInt("loot-chest.min-level", 1));
        int configMax = Math.max(configMin, config.getInt("loot-chest.max-level", 3));
        int maxLevel = Math.min(configMax, selected.getMaxLevel());
        int minLevel = Math.min(configMin, maxLevel);
        int level = minLevel + random.nextInt(Math.max(1, maxLevel - minLevel + 1));

        ItemStack book = enchantManager.createEnchantedBook(selected.getId(), level);
        if (book != null) {
            event.getLoot().add(book);
        }
    }

    private List<CustomEnchantment> getValidEnchants(List<String> whitelist) {
        List<CustomEnchantment> result = new ArrayList<>();
        if (whitelist != null && !whitelist.isEmpty()) {
            for (String id : whitelist) {
                CustomEnchantment enchant = enchantManager.getEnchant(id.toLowerCase());
                if (enchant != null && enchant.isEnabled()) {
                    result.add(enchant);
                }
            }
        } else {
            for (CustomEnchantment enchant : enchantManager.getAllEnchantments().values()) {
                if (enchant.isEnabled()) {
                    result.add(enchant);
                }
            }
        }
        return result;
    }
}
