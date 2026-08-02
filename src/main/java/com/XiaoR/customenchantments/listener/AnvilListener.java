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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        boolean hasSuperHighVanilla = false;
        for (Map.Entry<Enchantment, Integer> entry : mergedVanilla.entrySet()) {
            if (entry.getValue() > 10) { hasSuperHighVanilla = true; break; }
        }
        if (hasSuperHighVanilla) {
            resultMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        } else {
            resultMeta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        }

        Map<String, Integer> mergedCustom = new HashMap<>(firstCustomEnchants);
        boolean anyCustomFromSecondApplied = false;
        if (secondItem.getType() == Material.ENCHANTED_BOOK || !secondCustomEnchants.isEmpty()) {
            boolean restrictType = plugin.getConfig().getBoolean("anvil-restrict-enchant-type", true);
            for (Map.Entry<String, Integer> entry : secondCustomEnchants.entrySet()) {
                String enchantId = entry.getKey();
                int secondLvl = entry.getValue();
                // When restriction is enabled, skip enchants that don't match the target item type
                // (exempt book-to-book merging so enchanted books can still be combined)
                if (restrictType && firstItem.getType() != Material.ENCHANTED_BOOK
                        && !firstItem.getType().isAir()
                        && !enchantManager.isEnchantable(firstItem, enchantId)) {
                    continue;
                }
                anyCustomFromSecondApplied = true;
                if (mergedCustom.containsKey(enchantId)) {
                    int existingLvl = mergedCustom.get(enchantId);
                    mergedCustom.put(enchantId, Math.max(existingLvl, secondLvl));
                } else {
                    mergedCustom.put(enchantId, secondLvl);
                }
            }
        }
        // If the second item is a book with custom enchants but none matched the tool type,
        // block the operation entirely (no result, book not consumed, can't take from anvil)
        if (!secondCustomEnchants.isEmpty() && !anyCustomFromSecondApplied
                && secondItem.getType() == Material.ENCHANTED_BOOK
                && firstItem.getType() != Material.ENCHANTED_BOOK) {
            event.setResult(null);
            return;
        }
        result.setItemMeta(resultMeta);
        for (Map.Entry<String, Integer> entry : mergedCustom.entrySet()) {
            enchantManager.applyEnchant(result, entry.getKey(), entry.getValue());
        }
        if (hasSuperHighVanilla) {
            ItemMeta finalMeta = result.getItemMeta();
            if (finalMeta != null) {
                List<String> vanillaLore = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : mergedVanilla.entrySet()) {
                    String enchantKey = entry.getKey().getKey().getKey();
                    String displayName = getVanillaDisplayName(enchantKey);
                    String loreLine = org.bukkit.ChatColor.DARK_AQUA + "\u2727 " + displayName + " " + org.bukkit.ChatColor.GRAY + "Lv." + entry.getValue();
                    vanillaLore.add(loreLine);
                }
                List<String> existingLore = finalMeta.hasLore() ? new ArrayList<>(finalMeta.getLore()) : new ArrayList<>();
                existingLore.addAll(0, vanillaLore);
                finalMeta.setLore(existingLore.isEmpty() ? null : existingLore);
                result.setItemMeta(finalMeta);
            }
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

    private String getVanillaDisplayName(String enchantKey) {
        return switch (enchantKey) {
            case "unbreaking" -> "耐久";
            case "mending" -> "经验修补";
            case "fortune" -> "时运";
            case "silk_touch" -> "精准采集";
            case "efficiency" -> "效率";
            case "sharpness" -> "锋利";
            case "smite" -> "亡灵杀手";
            case "bane_of_arthropods" -> "节肢杀手";
            case "knockback" -> "击退";
            case "fire_aspect" -> "火焰附加";
            case "looting" -> "抢夺";
            case "sweeping_edge" -> "横扫之刃";
            case "protection" -> "保护";
            case "fire_protection" -> "火焰保护";
            case "blast_protection" -> "爆炸保护";
            case "projectile_protection" -> "弹射物保护";
            case "feather_falling" -> "摔落保护";
            case "respiration" -> "水下呼吸";
            case "aqua_affinity" -> "水下速掘";
            case "thorns" -> "荆棘";
            case "depth_strider" -> "深海探索者";
            case "frost_walker" -> "冰霜行者";
            case "binding_curse" -> "绑定诅咒";
            case "vanishing_curse" -> "消失诅咒";
            case "power" -> "力量";
            case "punch" -> "冲击";
            case "flame" -> "火矢";
            case "infinity" -> "无限";
            case "luck_of_the_sea" -> "海之眷顾";
            case "lure" -> "饵钓";
            case "loyalty" -> "忠诚";
            case "riptide" -> "激流";
            case "channeling" -> "引雷";
            case "impaling" -> "穿刺";
            case "multishot" -> "多重射击";
            case "piercing" -> "穿透";
            case "quick_charge" -> "快速装填";
            case "soul_speed" -> "灵魂疾行";
            case "swift_sneak" -> "潜行加速";
            default -> enchantKey;
        };
    }
}
