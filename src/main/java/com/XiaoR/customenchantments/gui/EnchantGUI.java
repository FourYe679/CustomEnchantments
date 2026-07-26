package com.XiaoR.customenchantments.gui;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantGUI {
    private final CustomEnchantments plugin;
    private final LanguageManager lang;

    public EnchantGUI(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLanguageManager();
    }

    public void openMainMenu(Player player) {
        String title = lang.getGuiMessage("main_menu", "title");
        GUIHolder holder = new GUIHolder(GUIType.MAIN_MENU);
        Inventory inventory = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inventory);

        EnchantCategory[] categories = EnchantCategory.values();
        for (int i = 0; i < categories.length; i++) {
            EnchantCategory category = categories[i];
            int count = plugin.getEnchantManager().getEnchantsForCategory(category).size();

            ItemStack item = new ItemStack(category.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        category.getColor() + "\u2726 " + category.getDisplayName(lang)));
                meta.addItemFlags(
                        org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
                        org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                meta.setEnchantmentGlintOverride(true);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "\u5171 " + ChatColor.WHITE + count + ChatColor.GRAY + " \u4e2a\u9644\u9b54");
                lore.add("");
                lore.add(ChatColor.YELLOW + "\u70b9\u51fb\u6d4f\u89c8" + category.getDisplayName(lang));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(10 + i, item);
        }

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c\u5173\u95ed\u83dc\u5355"));
            closeItem.setItemMeta(closeMeta);
        }
        inventory.setItem(49, closeItem);

        player.openInventory(inventory);
    }
}
