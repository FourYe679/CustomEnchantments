package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
public class EnchantingTableListener implements Listener {
    private final CustomEnchantments plugin;
    public EnchantingTableListener(CustomEnchantments plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int maxLevel = plugin.getConfig().getInt("max-enchant-level", 225);
        boolean enabled = plugin.getConfig().getBoolean("enable-enchanting-table-max-level", false);
        if (!enabled) return;
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
}
