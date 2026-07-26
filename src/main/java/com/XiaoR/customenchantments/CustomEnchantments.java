package com.XiaoR.customenchantments;

import com.XiaoR.customenchantments.gui.EnchantGUI;
import com.XiaoR.customenchantments.gui.GUIListener;
import com.XiaoR.customenchantments.listener.AnvilListener;
import com.XiaoR.customenchantments.listener.ArmorEffectListener;
import com.XiaoR.customenchantments.listener.BlockBreakListener;
import com.XiaoR.customenchantments.listener.DurabilityListener;
import com.XiaoR.customenchantments.listener.EnchantListener;
import com.XiaoR.customenchantments.listener.EnchantingTableListener;
import com.XiaoR.customenchantments.manager.CooldownManager;
import com.XiaoR.customenchantments.manager.EnchantCommand;
import com.XiaoR.customenchantments.manager.EnchantManager;
import com.XiaoR.customenchantments.manager.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CustomEnchantments extends JavaPlugin {
    private static CustomEnchantments instance;
    private EnchantManager enchantManager;
    private CooldownManager cooldownManager;
    private LanguageManager languageManager;
    private ArmorEffectListener armorEffectListener;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        saveDefaultConfig();

        languageManager = new LanguageManager(this);
        languageManager.load();

        enchantManager = new EnchantManager(this);
        enchantManager.loadEnchantments();

        cooldownManager = new CooldownManager(this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            cooldownManager.cleanupExpired();
        }, 20L * 60, 20L * 60);

        getCommand("cenchant").setExecutor(new EnchantCommand(this));
        getCommand("cenchant").setTabCompleter(new EnchantCommand(this));

        Bukkit.getPluginManager().registerEvents(new EnchantListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DurabilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EnchantingTableListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);

        armorEffectListener = new ArmorEffectListener(this);
        Bukkit.getPluginManager().registerEvents(armorEffectListener, this);
        Bukkit.getPluginManager().registerEvents(cooldownManager, this);

        setupEconomy();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "CustomEnchantments v" + getPluginMeta().getVersion() + " enabled!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded " + enchantManager.getAllEnchantments().size() + " custom enchantments.");
    }

    @Override
    public void onDisable() {
        if (armorEffectListener != null) {
            armorEffectListener.shutdown();
        }
        if (cooldownManager != null) {
            cooldownManager.shutdown();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "CustomEnchantments disabled!");
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Vault not found! Shop features will be disabled.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No economy provider found! Shop features will be disabled.");
            return;
        }
        economy = rsp.getProvider();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Vault economy hooked: " + economy.getName());
    }

    public void openEnchantGUI(Player player) {
        new EnchantGUI(this).openMainMenu(player);
    }

    public static CustomEnchantments getInstance() {
        return instance;
    }

    public EnchantManager getEnchantManager() {
        return enchantManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean isEconomyEnabled() {
        return economy != null;
    }
}
