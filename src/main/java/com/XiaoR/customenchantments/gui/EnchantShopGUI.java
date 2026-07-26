package com.XiaoR.customenchantments.gui;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EnchantShopGUI {
    private final CustomEnchantments plugin;
    private final LanguageManager lang;
    private final NamespacedKey enchantIdKey;
    private final NamespacedKey enchantLevelKey;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.#");

    public EnchantShopGUI(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLanguageManager();
        this.enchantIdKey = new NamespacedKey(plugin, "gui_enchant_id");
        this.enchantLevelKey = new NamespacedKey(plugin, "gui_enchant_level");
    }

    public void openShopMenu(Player player, CustomEnchantment enchant) {
        String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
        String title = lang.getGuiMessage("shop_menu", "title",
                "{enchant}", displayName);

        GUIHolder holder = new GUIHolder(GUIType.SHOP_MENU);
        Inventory inventory = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inventory);

        int slot = 10;
        for (int level = 1; level <= enchant.getMaxLevel(); level++) {
            double price = getEnchantPrice(enchant.getId(), level);
            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "\u2726 " + displayName + " "
                        + ChatColor.GOLD + "Lv." + level);
                meta.addItemFlags(
                        org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS,
                        org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
                        org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                try {
                    meta.addEnchant(Enchantment.LURE, 1, true);
                } catch (IllegalArgumentException ignored) {
                }
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "\u2726 \u7b49\u7ea7: " + ChatColor.WHITE + level + " / " + enchant.getMaxLevel());
                lore.add(ChatColor.GOLD + "\u2726 \u4ef7\u683c: " + ChatColor.GREEN + "$" + moneyFormat.format(price));
                lore.add("");
                lore.add(ChatColor.YELLOW + "\u70b9\u51fb\u8d2d\u4e70");
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(enchantIdKey, PersistentDataType.STRING, enchant.getId());
                meta.getPersistentDataContainer().set(enchantLevelKey, PersistentDataType.INTEGER, level);
                item.setItemMeta(meta);
            }
            inventory.setItem(slot, item);
            slot++;
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44) {
                slot += 2;
            }
        }

        Economy economy = plugin.getEconomy();
        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta balanceMeta = balanceItem.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.GREEN + "\u4f59\u989d: " + ChatColor.GOLD + "$"
                    + moneyFormat.format(economy.getBalance(player)));
            balanceItem.setItemMeta(balanceMeta);
        }
        inventory.setItem(49, balanceItem);

        player.openInventory(inventory);
    }

    public void openConfirmMenu(Player player, String enchantId, int level) {
        CustomEnchantment enchant = plugin.getEnchantManager().getEnchant(enchantId);
        String displayName = enchant != null
                ? ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName())
                : enchantId;
        String title = lang.getGuiMessage("confirm_menu", "title",
                "{enchant}", displayName,
                "{level}", String.valueOf(level));

        GUIHolder holder = new GUIHolder(GUIType.SHOP_MENU, null, 0, enchantId, level);
        Inventory inventory = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inventory);

        if (enchant == null) return;

        ItemStack previewItem = plugin.getEnchantManager().createEnchantedBook(enchantId, level);
        if (previewItem != null) {
            inventory.setItem(13, previewItem);
        }

        double price = getEnchantPrice(enchantId, level);
        ItemStack priceItem = new ItemStack(Material.PAPER);
        ItemMeta priceMeta = priceItem.getItemMeta();
        if (priceMeta != null) {
            priceMeta.setDisplayName(ChatColor.GREEN + "\u4ef7\u683c: " + ChatColor.GOLD + "$" + moneyFormat.format(price));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "\u4f60\u7684\u4f59\u989d: " + ChatColor.GREEN + "$"
                    + moneyFormat.format(plugin.getEconomy().getBalance(player)));
            priceMeta.setLore(lore);
            priceItem.setItemMeta(priceMeta);
        }
        inventory.setItem(22, priceItem);

        ItemStack confirmItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a\u2714 \u786e\u8ba4\u8d2d\u4e70"));
            confirmItem.setItemMeta(confirmMeta);
        }
        inventory.setItem(29, confirmItem);

        ItemStack cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c\u2718 \u53d6\u6d88\u8d2d\u4e70"));
            cancelItem.setItemMeta(cancelMeta);
        }
        inventory.setItem(33, cancelItem);

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "\u5173\u95ed");
            closeItem.setItemMeta(closeMeta);
        }
        inventory.setItem(49, closeItem);

        player.openInventory(inventory);
    }

    public double getEnchantPrice(String enchantId, int level) {
        double configPrice = plugin.getEnchantManager().getEnchantConfig()
                .getDouble("enchantments." + enchantId + ".price", -1);
        if (configPrice > 0) {
            return configPrice;
        }
        double defaultPrice = plugin.getConfig().getDouble("shop.default-price", 1000);
        return defaultPrice * level;
    }
}
