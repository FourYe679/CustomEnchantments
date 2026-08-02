package com.XiaoR.customenchantments.gui;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.LanguageManager;
import com.XiaoR.customenchantments.manager.EnchantManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIListener implements Listener {
    private final CustomEnchantments plugin;
    private final LanguageManager lang;
    private final NamespacedKey enchantIdKey;
    private final NamespacedKey enchantLevelKey;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.#");

    public GUIListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLanguageManager();
        this.enchantIdKey = new NamespacedKey(plugin, "gui_enchant_id");
        this.enchantLevelKey = new NamespacedKey(plugin, "gui_enchant_level");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        InventoryHolder holder = clickedInventory.getHolder();
        if (!(holder instanceof GUIHolder)) return;
        event.setCancelled(true);
        GUIHolder guiHolder = (GUIHolder) holder;
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        switch (guiHolder.getType()) {
            case MAIN_MENU:
                handleMainMenuClick(player, clickedItem, event.getSlot());
                break;
            case CATEGORY_MENU:
                handleCategoryClick(player, clickedItem, guiHolder, event.getSlot());
                break;
            case SHOP_MENU:
                handleShopClick(player, clickedItem, guiHolder, event.getSlot());
                break;
        }
    }

    private void handleMainMenuClick(Player player, ItemStack item, int slot) {
        if (slot == 49) {
            player.closeInventory();
            return;
        }
        EnchantCategory category = getCategoryByIcon(item.getType());
        if (category != null) {
            new EnchantCategoryGUI(plugin).openCategory(player, category, 0);
        }
    }

    private void handleCategoryClick(Player player, ItemStack item, GUIHolder holder, int slot) {
        if (slot == 49) {
            new EnchantGUI(plugin).openMainMenu(player);
            return;
        }
        if (slot == 48) {
            new EnchantCategoryGUI(plugin).openCategory(player, holder.getCategory(), holder.getPage() - 1);
            return;
        }
        if (slot == 50) {
            new EnchantCategoryGUI(plugin).openCategory(player, holder.getCategory(), holder.getPage() + 1);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String enchantId = meta.getPersistentDataContainer().get(enchantIdKey, PersistentDataType.STRING);
        if (enchantId != null) {
            CustomEnchantment enchant = plugin.getEnchantManager().getEnchant(enchantId);
            if (enchant != null) {
                boolean shopEnabled = plugin.getConfig().getBoolean("shop.enabled", true);
                if (!shopEnabled) {
                    player.sendMessage(lang.getMessage("shop-disabled"));
                    return;
                }
                if (!isWorldAllowed(player)) {
                    player.sendMessage(lang.getMessage("world-not-allowed"));
                    return;
                }
                new EnchantShopGUI(plugin).openShopMenu(player, enchant);
            }
        }
    }

    private void handleShopClick(Player player, ItemStack item, GUIHolder holder, int slot) {
        if (holder.getSelectedEnchantId() != null && holder.getSelectedLevel() > 0) {
            if (slot == 49) {
                player.closeInventory();
                return;
            }
            if (slot == 33) {
                CustomEnchantment enchant = plugin.getEnchantManager().getEnchant(holder.getSelectedEnchantId());
                if (enchant != null) {
                    new EnchantShopGUI(plugin).openShopMenu(player, enchant);
                } else {
                    player.closeInventory();
                }
                return;
            }
            if (slot == 29) {
                processPurchase(player, holder);
                return;
            }
            return;
        }
        if (slot == 49) {
            player.closeInventory();
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String enchantId = meta.getPersistentDataContainer().get(enchantIdKey, PersistentDataType.STRING);
        Integer level = meta.getPersistentDataContainer().get(enchantLevelKey, PersistentDataType.INTEGER);
        if (enchantId != null && level != null) {
            new EnchantShopGUI(plugin).openConfirmMenu(player, enchantId, level);
        }
    }

    private void processPurchase(Player player, GUIHolder holder) {
        String enchantId = holder.getSelectedEnchantId();
        int level = holder.getSelectedLevel();
        CustomEnchantment enchant = plugin.getEnchantManager().getEnchant(enchantId);
        if (enchant == null) {
            player.closeInventory();
            return;
        }
        if (!plugin.isEconomyEnabled()) {
            player.sendMessage(ChatColor.RED + "\u7ecf\u6d4e\u7cfb\u7edf\u672a\u542f\u7528\uff0c\u65e0\u6cd5\u8d2d\u4e70\u3002");
            player.closeInventory();
            return;
        }
        double price = new EnchantShopGUI(plugin).getEnchantPrice(enchantId, level);
        Economy economy = plugin.getEconomy();
        double balance = economy.getBalance(player);
        if (balance < price) {
            player.sendMessage(ChatColor.RED + "\u4f59\u989d\u4e0d\u8db3\uff01\u9700\u8981 "
                    + ChatColor.GOLD + "$" + moneyFormat.format(price)
                    + ChatColor.RED + "\uff0c\u5f53\u524d\u4f59\u989d "
                    + ChatColor.GOLD + "$" + moneyFormat.format(balance));
            player.closeInventory();
            return;
        }
        economy.withdrawPlayer(player, price);
        ItemStack book = plugin.getEnchantManager().createEnchantedBook(enchantId, level);
        if (book != null) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(book);
            if (!leftover.isEmpty()) {
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
                player.sendMessage(lang.getMessage("inventory-full"));
            }
            String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
            String priceStr = ChatColor.GOLD + "$" + moneyFormat.format(price);
            player.sendMessage(lang.getMessage("purchase-success",
                    "{enchant}", displayName,
                    "{level}", String.valueOf(level),
                    "{price}", priceStr));
        }
        player.closeInventory();
    }

    private boolean isWorldAllowed(Player player) {
        List<String> worlds = plugin.getConfig().getStringList("shop.enabled-worlds");
        if (worlds == null || worlds.isEmpty()) return true;
        String worldName = player.getWorld().getName();
        for (String w : worlds) {
            if (w.equalsIgnoreCase(worldName)) return true;
        }
        return false;
    }

    private EnchantCategory getCategoryByIcon(Material material) {
        for (EnchantCategory category : EnchantCategory.values()) {
            if (category.getIcon() == material) {
                return category;
            }
        }
        return null;
    }
}
