package com.XiaoR.customenchantments.manager;

import com.XiaoR.customenchantments.CustomEnchantments;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private final CustomEnchantments plugin;
    private FileConfiguration messages;
    private final Map<String, FileConfiguration> guiConfigs;
    private String language;

    public LanguageManager(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.guiConfigs = new HashMap<>();
        this.language = "zh_cn";
    }

    public void load() {
        saveDefaultLanguageFiles();
        reload();
    }

    private void saveDefaultLanguageFiles() {
        File langDir = new File(plugin.getDataFolder(), "language");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        File langFile = new File(langDir, language + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("language/" + language + ".yml", false);
        }

        File guiDir = new File(plugin.getDataFolder(), "gui");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
        }
        String[] guiFiles = {"categories.yml", "main_menu.yml", "category_menu.yml", "shop_menu.yml", "confirm_menu.yml"};
        for (String fileName : guiFiles) {
            File guiFile = new File(guiDir, fileName);
            if (!guiFile.exists()) {
                plugin.saveResource("gui/" + fileName, false);
            }
        }
    }

    public void reload() {
        File langFile = new File(plugin.getDataFolder(), "language" + File.separator + language + ".yml");
        messages = YamlConfiguration.loadConfiguration(langFile);

        guiConfigs.clear();
        File guiDir = new File(plugin.getDataFolder(), "gui");
        if (guiDir.exists()) {
            File[] files = guiDir.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    String key = file.getName().replace(".yml", "");
                    guiConfigs.put(key, YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    public String getMessage(String key, String... placeholders) {
        String message = messages.getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(message, placeholders));
    }

    public String getPrefixedMessage(String key, String... placeholders) {
        String prefix = messages.getString("prefix", "");
        String message = messages.getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(prefix + message, placeholders));
    }

    public String getPrefix() {
        String prefix = messages.getString("prefix", "");
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getGuiMessage(String fileName, String key, String... placeholders) {
        FileConfiguration config = guiConfigs.get(fileName);
        if (config == null) return key;
        String message = config.getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(message, placeholders));
    }

    private String replacePlaceholders(String message, String... placeholders) {
        if (placeholders == null || placeholders.length == 0) return message;
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message;
    }
}
