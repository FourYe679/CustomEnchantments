package com.XiaoR.customenchantments.manager;

import com.XiaoR.customenchantments.CustomEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager implements Listener {
    private final CustomEnchantments plugin;
    private final Map<String, Map<String, Long>> cooldowns;
    private BukkitTask cleanupTask;

    public CooldownManager(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.cooldowns = new ConcurrentHashMap<>();
    }

    public boolean checkAndTrigger(Player player, String enchantId, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return true;
        UUID uuid = player.getUniqueId();
        String key = uuid.toString();
        long now = System.currentTimeMillis();
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        Long expiry = playerCooldowns.get(enchantId);
        if (expiry != null && expiry > now) {
            return false;
        }
        playerCooldowns.put(enchantId, now + (cooldownSeconds * 1000L));
        return true;
    }

    public boolean isOnCooldown(Player player, String enchantId) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId().toString());
        if (playerCooldowns == null) return false;
        Long expiry = playerCooldowns.get(enchantId);
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public long getRemainingCooldown(Player player, String enchantId) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId().toString());
        if (playerCooldowns == null) return 0;
        Long expiry = playerCooldowns.get(enchantId);
        if (expiry == null) return 0;
        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Map<String, Long>>> outer = cooldowns.entrySet().iterator();
        while (outer.hasNext()) {
            Map.Entry<String, Map<String, Long>> entry = outer.next();
            Map<String, Long> playerCooldowns = entry.getValue();
            playerCooldowns.entrySet().removeIf(e -> e.getValue() <= now);
            if (playerCooldowns.isEmpty()) {
                outer.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cooldowns.remove(player.getUniqueId().toString());
    }

    public void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
        cooldowns.clear();
    }
}
