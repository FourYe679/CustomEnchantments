package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.impl.DurabilityBlessingEnchant;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
public class DurabilityListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    public DurabilityListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;
        Map<String, Integer> enchants = enchantManager.getAllEnchants(item);
        if (enchants.isEmpty()) return;
        Integer level = enchants.get("durability_blessing");
        if (level == null || level <= 0) return;
        Object enchantObj = enchantManager.getEnchant("durability_blessing");
        if (!(enchantObj instanceof DurabilityBlessingEnchant enchant) || !enchant.isEnabled()) return;
        double chance = Math.min(level * 0.15, 0.75);
        if (Math.random() < chance) {
            event.setCancelled(true);
        }
    }
}
