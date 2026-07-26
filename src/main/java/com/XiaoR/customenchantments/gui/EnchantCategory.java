package com.XiaoR.customenchantments.gui;

import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.LanguageManager;
import org.bukkit.Material;

import java.util.Set;

public enum EnchantCategory {
    MELEE("近战附魔", Material.DIAMOND_SWORD, "&c"),
    RANGED("远程附魔", Material.BOW, "&a"),
    TOOL("工具附魔", Material.DIAMOND_PICKAXE, "&e"),
    ARMOR("盔甲附魔", Material.DIAMOND_CHESTPLATE, "&b"),
    SPECIAL("特殊附魔", Material.ENCHANTED_BOOK, "&d");

    private final String displayName;
    private final Material icon;
    private final String color;

    EnchantCategory(String displayName, Material icon, String color) {
        this.displayName = displayName;
        this.icon = icon;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayName(LanguageManager lang) {
        return lang.getGuiMessage("categories", name().toLowerCase());
    }

    public Material getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public boolean matches(CustomEnchantment enchant) {
        Set<Material> materials = enchant.getApplicableMaterials();
        if (materials == null || materials.isEmpty()) {
            return this == SPECIAL;
        }
        boolean hasMelee = false;
        boolean hasRanged = false;
        boolean hasTool = false;
        boolean hasArmor = false;
        for (Material mat : materials) {
            String name = mat.name();
            if (name.endsWith("_SWORD") || name.endsWith("_AXE") || mat == Material.TRIDENT) {
                hasMelee = true;
            }
            if (mat == Material.BOW || mat == Material.CROSSBOW) {
                hasRanged = true;
            }
            if (name.endsWith("_PICKAXE") || name.endsWith("_SHOVEL") || name.endsWith("_HOE")
                    || mat == Material.SHEARS || mat == Material.FISHING_ROD || mat == Material.FLINT_AND_STEEL) {
                hasTool = true;
            }
            if (name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS")
                    || name.endsWith("_BOOTS") || mat == Material.TURTLE_HELMET || mat == Material.ELYTRA) {
                hasArmor = true;
            }
        }
        switch (this) {
            case MELEE:
                return hasMelee;
            case RANGED:
                return hasRanged;
            case TOOL:
                return hasTool;
            case ARMOR:
                return hasArmor;
            case SPECIAL:
                return true;
            default:
                return false;
        }
    }
}
