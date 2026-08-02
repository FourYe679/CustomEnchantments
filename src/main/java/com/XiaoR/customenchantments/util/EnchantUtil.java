package com.XiaoR.customenchantments.util;
import com.XiaoR.customenchantments.CustomEnchantments;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
public class EnchantUtil {
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public static String formatNumber(int number) {
        if (number <= 0) return "";
        if (number > 3999) return String.valueOf(number);
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[number / 1000]
                + hundreds[(number % 1000) / 100]
                + tens[(number % 100) / 10]
                + ones[number % 10];
    }
    public static boolean hasPermission(Player player, String permission) {
        if (player.hasPermission("cenchant.bypass")) return true;
        return player.hasPermission(permission);
    }
    public static void sendMessage(Player player, String message) {
        player.sendMessage(CustomEnchantments.getInstance().getLanguageManager().getPrefix() + colorize(message));
    }
    public static void sendRawMessage(Player player, String message) {
        player.sendMessage(colorize(message));
    }
    public static LivingEntity getTargetEntity(Player player) {
        RayTraceResult result = player.rayTraceEntities(5);
        if (result != null && result.getHitEntity() != null
                && result.getHitEntity() instanceof LivingEntity) {
            return (LivingEntity) result.getHitEntity();
        }
        return null;
    }
    public static String replacePlaceholders(String message, String enchant, int level, int max) {
        return message
                .replace("{enchant}", enchant)
                .replace("{level}", String.valueOf(level))
                .replace("{max}", String.valueOf(max))
                .replace("{player}", "");
    }
    public static String replacePlaceholders(String message, String enchant, int level, int max, String player) {
        return message
                .replace("{enchant}", enchant)
                .replace("{level}", String.valueOf(level))
                .replace("{max}", String.valueOf(max))
                .replace("{player}", player);
    }
}
