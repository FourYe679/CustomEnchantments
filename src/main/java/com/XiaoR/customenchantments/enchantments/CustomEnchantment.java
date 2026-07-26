package com.XiaoR.customenchantments.enchantments;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CustomEnchantment {
    private final String id;
    private String displayName;
    private int maxLevel;
    private List<String> description;
    private boolean enabled;
    private int cooldown;
    private Set<Material> applicableMaterials;

    public CustomEnchantment(String id) {
        this.id = id;
        loadConfig();
    }

    private void loadConfig() {
        CustomEnchantments plugin = CustomEnchantments.getInstance();
        EnchantManager manager = plugin.getEnchantManager();
        FileConfiguration config = manager.getEnchantConfig();
        String path = "enchantments." + id;
        this.displayName = config.getString(path + ".display-name", id);
        this.maxLevel = config.getInt(path + ".max-level", 1);
        this.description = config.getStringList(path + ".description");
        this.enabled = config.getBoolean(path + ".enabled", true);
        this.cooldown = config.getInt(path + ".cooldown", 0);
        List<String> applyTo = config.getStringList(path + ".apply-to");
        this.applicableMaterials = EnchantManager.expandMaterials(applyTo);
    }

    public void reloadConfig() {
        loadConfig();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<String> getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Set<Material> getApplicableMaterials() {
        return applicableMaterials;
    }

    public void onActivate(Player player, Entity target, int level, ItemStack item) {
    }

    public void onProjectileHit(Player player, Entity target, int level, ItemStack item) {
    }

    public void onBowShoot(Player player, Arrow arrow, int level, ItemStack item) {
    }
}
