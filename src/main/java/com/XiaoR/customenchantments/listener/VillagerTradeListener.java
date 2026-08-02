package com.XiaoR.customenchantments.listener;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 让图书管理员村民有概率出售自定义附魔书。
 * 当村民学会新交易时，按配置概率替换为自定义附魔书交易。
 */
public class VillagerTradeListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final Random random = new Random();

    public VillagerTradeListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("villager-trades.enabled", false)) return;

        AbstractVillager villager = event.getEntity();
        if (!(villager instanceof Villager)) return;
        Villager v = (Villager) villager;
        if (v.getProfession() != Villager.Profession.LIBRARIAN) return;

        double chance = config.getDouble("villager-trades.chance", 0.15);
        if (random.nextDouble() >= chance) return;

        int minPrice = Math.max(1, config.getInt("villager-trades.min-emerald-price", 5));
        int maxPrice = Math.max(minPrice, config.getInt("villager-trades.max-emerald-price", 32));
        int configMin = Math.max(1, config.getInt("villager-trades.min-level", 1));
        int configMax = Math.max(configMin, config.getInt("villager-trades.max-level", 3));

        List<String> whitelist = config.getStringList("villager-trades.whitelist");
        List<CustomEnchantment> candidates = getValidEnchants(whitelist);
        if (candidates.isEmpty()) return;

        CustomEnchantment selected = candidates.get(random.nextInt(candidates.size()));
        // min/max from config, but capped by enchantment's own max-level from enchantments.yml
        int maxLevel = Math.min(configMax, selected.getMaxLevel());
        int minLevel = Math.min(configMin, maxLevel);
        int level = minLevel + random.nextInt(Math.max(1, maxLevel - minLevel + 1));
        int price = minPrice + random.nextInt(maxPrice - minPrice + 1);

        ItemStack book = enchantManager.createEnchantedBook(selected.getId(), level);
        if (book == null) return;

        MerchantRecipe recipe = new MerchantRecipe(book, 0, 12, true, 2, 1.0f);
        recipe.addIngredient(new ItemStack(Material.EMERALD, price));
        event.setRecipe(recipe);
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
