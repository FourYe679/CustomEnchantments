package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.HashMap;
import java.util.Map;
public class AnvilListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    public AnvilListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();
        if (firstItem == null || secondItem == null) return;
        int maxLevel = plugin.getConfig().getInt("max-enchant-level", 225);
        boolean allowCombine = plugin.getConfig().getBoolean("allow-combine-overlevel", true);

        Map<String, Integer> firstCustomEnchants = enchantManager.getAllEnchants(firstItem);
        Map<String, Integer> secondCustomEnchants = enchantManager.getAllEnchants(secondItem);
        boolean hasCustomEnchants = !firstCustomEnchants.isEmpty() || !secondCustomEnchants.isEmpty();

        Map<Enchantment, Integer> firstVanilla = getVanillaEnchants(firstItem);
        Map<Enchantment, Integer> secondVanilla = getVanillaEnchants(secondItem);

        boolean hasOverlevelVanilla = false;
        for (Map.Entry<Enchantment, Integer> e : firstVanilla.entrySet()) {
            if (e.getValue() > e.getKey().getMaxLevel()) { hasOverlevelVanilla = true; break; }
        }
        if (!hasOverlevelVanilla) {
            for (Map.Entry<Enchantment, Integer> e : secondVanilla.entrySet()) {
                if (e.getValue() > e.getKey().getMaxLevel()) { hasOverlevelVanilla = true; break; }
            }
        }

        boolean hasSameVanilla = false;
        for (Enchantment ench : secondVanilla.keySet()) {
            if (firstVanilla.containsKey(ench)) { hasSameVanilla = true; break; }
        }

        if (!hasCustomEnchants && !hasOverlevelVanilla) {
            if (!allowCombine || !hasSameVanilla) return;
        }

        ItemStack result = event.getResult();
        if (result == null) {
            result = firstItem.clone();
        } else {
            result = result.clone();
        }
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null) return;

        Map<Enchantment, Integer> mergedVanilla = new HashMap<>(firstVanilla);
        for (Map.Entry<Enchantment, Integer> entry : secondVanilla.entrySet()) {
            Enchantment ench = entry.getKey();
            int secondLvl = entry.getValue();
            if (mergedVanilla.containsKey(ench)) {
                int existingLvl = mergedVanilla.get(ench);
                int vanillaMax = ench.getMaxLevel();
                int combinedLvl;
                if (existingLvl > vanillaMax || secondLvl > vanillaMax) {
                    if (allowCombine) {
                        combinedLvl = Math.min(existingLvl + secondLvl, maxLevel);
                    } else {
                        combinedLvl = Math.min(Math.max(existingLvl, secondLvl), maxLevel);
                    }
                } else {
                    if (existingLvl == secondLvl) {
                        combinedLvl = Math.min(existingLvl + 1, maxLevel);
                    } else {
                        combinedLvl = Math.min(Math.max(existingLvl, secondLvl), maxLevel);
                    }
                }
                mergedVanilla.put(ench, combinedLvl);
            } else {
                mergedVanilla.put(ench, Math.min(secondLvl, maxLevel));
            }
        }
        boolean resultIsBook = result.getType() == Material.ENCHANTED_BOOK;
        for (Map.Entry<Enchantment, Integer> entry : mergedVanilla.entrySet()) {
            if (resultIsBook && resultMeta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            } else {
                resultMeta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        Map<String, Integer> mergedCustom = new HashMap<>(firstCustomEnchants);
        if (secondItem.getType() == Material.ENCHANTED_BOOK || !secondCustomEnchants.isEmpty()) {
            for (Map.Entry<String, Integer> entry : secondCustomEnchants.entrySet()) {
                String enchantId = entry.getKey();
                int secondLvl = entry.getValue();
                if (mergedCustom.containsKey(enchantId)) {
                    int existingLvl = mergedCustom.get(enchantId);
                    if (allowCombine) {
                        mergedCustom.put(enchantId, Math.min(existingLvl + secondLvl, maxLevel));
                    } else {
                        mergedCustom.put(enchantId, Math.min(Math.max(existingLvl, secondLvl), maxLevel));
                    }
                } else {
                    mergedCustom.put(enchantId, Math.min(secondLvl, maxLevel));
                }
            }
        }
        result.setItemMeta(resultMeta);
        for (Map.Entry<String, Integer> entry : mergedCustom.entrySet()) {
            enchantManager.applyEnchant(result, entry.getKey(), entry.getValue());
        }
        event.setResult(result);
    }

    private Map<Enchantment, Integer> getVanillaEnchants(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (item == null || !item.hasItemMeta()) return map;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return map;
        map.putAll(meta.getEnchants());
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            map.putAll(storageMeta.getStoredEnchants());
        }
        map.remove(Enchantment.LURE);
        return map;
    }
}
