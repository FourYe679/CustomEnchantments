package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class EnchantingTableListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final Random random = new Random();
    public EnchantingTableListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
    }
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int maxLevel = plugin.getConfig().getInt("max-enchant-level", 225);
        boolean enabled = plugin.getConfig().getBoolean("enable-enchanting-table-max-level", false);
        if (enabled) {
            int expLevel = event.getExpLevelCost();
            Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();
            for (Map.Entry<Enchantment, Integer> entry : enchantsToAdd.entrySet()) {
                Enchantment enchant = entry.getKey();
                int originalLevel = entry.getValue();
                int vanillaMax = enchant.getMaxLevel();
                if (expLevel > vanillaMax) {
                    int newLevel = Math.min(expLevel, maxLevel);
                    enchantsToAdd.put(enchant, newLevel);
                }
            }
        }

        // Randomly apply a custom enchant to the item
        boolean customEnabled = plugin.getConfig().getBoolean("enchanting-table-custom.enabled", true);
        if (!customEnabled) return;

        double chance = plugin.getConfig().getDouble("enchanting-table-custom.chance", 0.25);
        if (random.nextDouble() >= chance) return;

        ItemStack item = event.getItem();
        Player player = event.getEnchanter();

        // Find applicable custom enchants for this item type
        List<CustomEnchantment> applicable = new ArrayList<>();
        for (CustomEnchantment enchant : enchantManager.getAllEnchantments().values()) {
            if (!enchant.isEnabled()) continue;
            if (enchantManager.isEnchantable(item, enchant.getId())) {
                applicable.add(enchant);
            }
        }
        if (applicable.isEmpty()) return;

        // Pick a random custom enchant
        CustomEnchantment selected = applicable.get(random.nextInt(applicable.size()));

        // Determine level: config range capped by enchant's max-level
        int configMin = Math.max(1, plugin.getConfig().getInt("enchanting-table-custom.min-level", 1));
        int configMax = Math.max(configMin, plugin.getConfig().getInt("enchanting-table-custom.max-level", 3));
        int maxLvl = Math.min(configMax, selected.getMaxLevel());
        int minLvl = Math.min(configMin, maxLvl);
        int level = minLvl + random.nextInt(Math.max(1, maxLvl - minLvl + 1));

        // Apply after the event completes (next tick) so vanilla enchants are already on the item
        final ItemStack finalItem = item;
        final String enchantId = selected.getId();
        final int finalLevel = level;
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            enchantManager.applyEnchant(finalItem, enchantId, finalLevel);
            if (player != null) {
                String displayName = org.bukkit.ChatColor.translateAlternateColorCodes('&', selected.getDisplayName());
                String msg = org.bukkit.ChatColor.LIGHT_PURPLE + "\u2726 " + displayName
                        + " " + org.bukkit.ChatColor.GRAY + "Lv." + finalLevel;
                player.sendMessage(org.bukkit.ChatColor.GREEN + "\u2726 附魔台赐予了你: " + msg);
            }
        });
    }
}
