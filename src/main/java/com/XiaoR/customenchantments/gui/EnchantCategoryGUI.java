package com.XiaoR.customenchantments.gui;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.EnchantManager;
import com.XiaoR.customenchantments.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class EnchantCategoryGUI {
    private final CustomEnchantments plugin;
    private final LanguageManager lang;
    private final NamespacedKey enchantIdKey;
    private static final int ITEMS_PER_PAGE = 28;

    public EnchantCategoryGUI(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLanguageManager();
        this.enchantIdKey = new NamespacedKey(plugin, "gui_enchant_id");
    }

    public void openCategory(Player player, EnchantCategory category, int page) {
        List<CustomEnchantment> enchants = getEnchantsForCategory(category);
        int totalPages = (int) Math.ceil(enchants.size() / (double) ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        String title = lang.getGuiMessage("category_menu", "title-prefix")
                + ChatColor.translateAlternateColorCodes('&', category.getColor())
                + category.getDisplayName(lang)
                + lang.getGuiMessage("category_menu", "title-suffix",
                        "{page}", String.valueOf(page + 1),
                        "{total}", String.valueOf(totalPages));

        GUIHolder holder = new GUIHolder(GUIType.CATEGORY_MENU, category, page);
        Inventory inventory = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inventory);

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

        int startIndex = page * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = startIndex + i;
            if (index < enchants.size()) {
                inventory.setItem(slots[i], createEnchantItem(enchants.get(index)));
            }
        }

        if (page > 0) {
            ItemStack prevItem = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevItem.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.YELLOW + "\u2190 \u4e0a\u4e00\u9875");
                prevItem.setItemMeta(prevMeta);
            }
            inventory.setItem(48, prevItem);
        }

        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backItem.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + "\u2190 \u8fd4\u56de\u4e3b\u83dc\u5355");
            backItem.setItemMeta(backMeta);
        }
        inventory.setItem(49, backItem);

        if (page < totalPages - 1) {
            ItemStack nextItem = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextItem.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.YELLOW + "\u4e0b\u4e00\u9875 \u2192");
                nextItem.setItemMeta(nextMeta);
            }
            inventory.setItem(50, nextItem);
        }

        player.openInventory(inventory);
    }

    private List<CustomEnchantment> getEnchantsForCategory(EnchantCategory category) {
        return plugin.getEnchantManager().getEnchantsForCategory(category);
    }

    private ItemStack createEnchantItem(CustomEnchantment enchant) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
        meta.setDisplayName(ChatColor.AQUA + "\u2726 " + displayName);

        meta.addItemFlags(
                org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
                org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setEnchantmentGlintOverride(true);

        List<String> lore = new ArrayList<>();
        for (String line : enchant.getDescription()) {
            lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', line));
        }
        lore.add("");
        lore.add(ChatColor.GOLD + "\u2726 \u6700\u5927\u7b49\u7ea7: " + ChatColor.WHITE + enchant.getMaxLevel());
        if (enchant.getCooldown() > 0) {
            lore.add(ChatColor.GOLD + "\u2726 \u51b7\u5374\u65f6\u95f4: " + ChatColor.WHITE + enchant.getCooldown() + "s");
        }
        lore.add(ChatColor.GOLD + "\u2726 \u9002\u7528\u7269\u54c1: " + ChatColor.WHITE
                + plugin.getEnchantManager().getMaterialCategoryNames(enchant.getApplicableMaterials()));
        lore.add("");
        boolean shopEnabled = plugin.getConfig().getBoolean("shop.enabled", true);
        if (shopEnabled) {
            lore.add(ChatColor.YELLOW + "\u70b9\u51fb\u8d2d\u4e70");
        } else {
            lore.add(ChatColor.GRAY + "\u5546\u5e97\u5df2\u5173\u95ed - \u70b9\u51fb\u67e5\u770b");
        }

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(enchantIdKey, PersistentDataType.STRING, enchant.getId());
        item.setItemMeta(meta);
        return item;
    }
}
