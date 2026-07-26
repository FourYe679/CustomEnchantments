package com.XiaoR.customenchantments.manager;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.util.EnchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;
public class EnchantCommand implements CommandExecutor, TabCompleter {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final LanguageManager lang;
    public EnchantCommand(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
        this.lang = plugin.getLanguageManager();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "apply" -> handleApply(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "list" -> handleList(sender, args);
            case "give" -> handleGive(sender, args);
            case "reload" -> handleReload(sender);
            case "maxlevel" -> handleMaxLevel(sender, args);
            case "gui" -> handleGUI(sender);
            case "vlevel" -> handleVLevel(sender, args);
            default -> sendHelp(sender);
        }
        return true;
    }
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(lang.getMessage("help-header"));
        sender.sendMessage(lang.getMessage("help-apply"));
        sender.sendMessage(lang.getMessage("help-remove"));
        sender.sendMessage(lang.getMessage("help-list"));
        sender.sendMessage(lang.getMessage("help-give"));
        sender.sendMessage(lang.getMessage("help-gui"));
        sender.sendMessage(lang.getMessage("help-vlevel"));
        sender.sendMessage(lang.getMessage("help-reload"));
        sender.sendMessage(lang.getMessage("help-maxlevel"));
    }
    private void handleGUI(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getMessage("player-only"));
            return;
        }
        plugin.openEnchantGUI(player);
    }
    private void handleApply(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getMessage("player-only"));
            return;
        }
        if (!EnchantUtil.hasPermission(player, "cenchant.apply")) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            EnchantUtil.sendMessage(player, lang.getMessage("usage-apply"));
            return;
        }
        String enchantId = args[1].toLowerCase();
        int level = args.length >= 3 ? parseInt(args[2], 1) : 1;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-item-in-hand"));
            return;
        }
        CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
        if (enchant == null) {
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-not-found", "{enchant}", enchantId));
            return;
        }
        if (!enchant.isEnabled()) {
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-disabled"));
            return;
        }
        if (!enchantManager.isEnchantable(item, enchantId)) {
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-not-applicable"));
            return;
        }
        if (level > enchant.getMaxLevel()) {
            EnchantUtil.sendMessage(player, lang.getMessage("max-level-exceeded",
                    "{max}", String.valueOf(enchant.getMaxLevel())));
            return;
        }
        boolean success = enchantManager.applyEnchant(item, enchantId, level);
        if (success) {
            String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-applied",
                    "{enchant}", displayName, "{level}", String.valueOf(level)));
        }
    }
    private void handleRemove(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getMessage("player-only"));
            return;
        }
        if (!EnchantUtil.hasPermission(player, "cenchant.remove")) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            EnchantUtil.sendMessage(player, lang.getMessage("usage-remove"));
            return;
        }
        String enchantId = args[1].toLowerCase();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-item-in-hand"));
            return;
        }
        boolean success = enchantManager.removeEnchant(item, enchantId);
        if (success) {
            String displayName = enchantId;
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant != null) {
                displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
            }
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-removed", "{enchant}", displayName));
        } else {
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-not-found", "{enchant}", enchantId));
        }
    }
    private void handleList(CommandSender sender, String[] args) {
        Map<String, CustomEnchantment> enchantments = enchantManager.getAllEnchantments();
        List<String> keys = new ArrayList<>(enchantments.keySet());
        int perPage = 7;
        int page = args.length >= 2 ? parseInt(args[1], 1) : 1;
        int totalPages = (int) Math.ceil((double) keys.size() / perPage);
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;
        if (totalPages == 0) totalPages = 1;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                lang.getMessage("list-header") + ChatColor.GRAY + " (" + page + "/" + totalPages + ")"));
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, keys.size());
        for (int i = start; i < end; i++) {
            String key = keys.get(i);
            CustomEnchantment enchant = enchantments.get(key);
            String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
            String desc = enchant.getDescription().isEmpty() ? "无描述"
                    : ChatColor.translateAlternateColorCodes('&', enchant.getDescription().get(0));
            String status = enchant.isEnabled() ? ChatColor.GREEN + "[启用]" : ChatColor.RED + "[禁用]";
            String line = ChatColor.YELLOW + displayName + " " + status
                    + ChatColor.GRAY + " - 最大" + enchant.getMaxLevel() + "级 | " + desc;
            sender.sendMessage(line);
        }
    }
    private void handleGive(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getMessage("player-only"));
            return;
        }
        if (!EnchantUtil.hasPermission(player, "cenchant.give")) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-permission"));
            return;
        }
        if (args.length < 3) {
            EnchantUtil.sendMessage(player, lang.getMessage("usage-give"));
            return;
        }
        String playerName = args[1];
        String enchantId = args[2].toLowerCase();
        int level = args.length >= 4 ? parseInt(args[3], 1) : 1;
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            EnchantUtil.sendMessage(player, lang.getMessage("player-not-found", "{player}", playerName));
            return;
        }
        CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
        if (enchant == null) {
            EnchantUtil.sendMessage(player, lang.getMessage("enchant-not-found", "{enchant}", enchantId));
            return;
        }
        if (level > enchant.getMaxLevel()) {
            level = enchant.getMaxLevel();
        }
        ItemStack book = enchantManager.createEnchantedBook(enchantId, level);
        if (book == null) {
            EnchantUtil.sendMessage(player, lang.getMessage("book-create-failed"));
            return;
        }
        HashMap<Integer, ItemStack> leftover = target.getInventory().addItem(book);
        if (!leftover.isEmpty()) {
            for (ItemStack item : leftover.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), item);
            }
        }
        String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
        EnchantUtil.sendMessage(player, lang.getMessage("give-success",
                "{player}", target.getName(), "{enchant}", displayName, "{level}", String.valueOf(level)));
    }
    private void handleReload(CommandSender sender) {
        if (sender instanceof Player player) {
            if (!EnchantUtil.hasPermission(player, "cenchant.reload")) {
                EnchantUtil.sendMessage(player, lang.getMessage("no-permission"));
                return;
            }
        }
        plugin.reloadConfig();
        lang.reload();
        enchantManager.loadEnchantments();
        sender.sendMessage(lang.getPrefixedMessage("reloaded"));
    }
    private void handleMaxLevel(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                EnchantUtil.sendMessage(player, lang.getMessage("no-permission"));
                return;
            }
        }
        if (args.length < 2) {
            int current = plugin.getConfig().getInt("max-enchant-level", 225);
            sender.sendMessage(lang.getMessage("maxlevel-current", "{level}", String.valueOf(current)));
            return;
        }
        int newLevel = parseInt(args[1], 225);
        if (newLevel < 1 || newLevel > 255) {
            sender.sendMessage(lang.getMessage("maxlevel-range-error"));
            return;
        }
        plugin.getConfig().set("max-enchant-level", newLevel);
        plugin.saveConfig();
        sender.sendMessage(lang.getMessage("maxlevel-set", "{level}", String.valueOf(newLevel)));
    }
    private void handleVLevel(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getMessage("player-only"));
            return;
        }
        if (!EnchantUtil.hasPermission(player, "cenchant.vlevel")) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-permission"));
            return;
        }
        if (args.length < 3) {
            EnchantUtil.sendMessage(player, lang.getMessage("usage-vlevel"));
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            EnchantUtil.sendMessage(player, lang.getMessage("no-item-in-hand"));
            return;
        }
        String enchantName = args[1].toLowerCase();
        int level = parseInt(args[2], 1);
        Enchantment enchantment = parseVanillaEnchant(enchantName);
        if (enchantment == null) {
            EnchantUtil.sendMessage(player, lang.getMessage("vanilla-enchant-not-found", "{enchant}", enchantName));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            EnchantUtil.sendMessage(player, lang.getMessage("book-create-failed"));
            return;
        }
        if (level <= 0) {
            if (meta instanceof org.bukkit.inventory.meta.EnchantmentStorageMeta storageMeta) {
                storageMeta.removeStoredEnchant(enchantment);
            }
            meta.removeEnchant(enchantment);
            removeVanillaLore(meta, enchantment);
            item.setItemMeta(meta);
            EnchantUtil.sendMessage(player, lang.getMessage("vanilla-enchant-removed",
                    "{enchant}", enchantment.getKey().getKey()));
            return;
        }
        if (meta instanceof org.bukkit.inventory.meta.EnchantmentStorageMeta storageMeta) {
            storageMeta.addStoredEnchant(enchantment, level, true);
        } else {
            meta.addEnchant(enchantment, level, true);
        }
        if (level > 10) {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            addVanillaLore(meta, enchantment, level);
        } else {
            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            removeVanillaLore(meta, enchantment);
        }
        item.setItemMeta(meta);
        EnchantUtil.sendMessage(player, lang.getMessage("vanilla-enchant-set",
                "{enchant}", enchantment.getKey().getKey(),
                "{level}", String.valueOf(level)));
    }

    private void addVanillaLore(ItemMeta meta, Enchantment enchantment, int level) {
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String enchantKey = enchantment.getKey().getKey();
        String displayName = getVanillaDisplayName(enchantKey);
        String loreMarker = ChatColor.DARK_AQUA + "\u2727 " + displayName + " " + ChatColor.GRAY + "Lv." + level;
        lore.removeIf(line -> {
            String stripped = ChatColor.stripColor(line);
            return stripped.startsWith("\u2727 " + displayName + " ");
        });
        lore.add(0, loreMarker);
        meta.setLore(lore);
    }

    private void removeVanillaLore(ItemMeta meta, Enchantment enchantment) {
        if (!meta.hasLore()) return;
        String enchantKey = enchantment.getKey().getKey();
        String displayName = getVanillaDisplayName(enchantKey);
        List<String> lore = new ArrayList<>(meta.getLore());
        lore.removeIf(line -> {
            String stripped = ChatColor.stripColor(line);
            return stripped.startsWith("\u2727 " + displayName + " ");
        });
        meta.setLore(lore.isEmpty() ? null : lore);
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

    private Enchantment parseVanillaEnchant(String name) {
        Enchantment byKey = Enchantment.getByKey(NamespacedKey.minecraft(name));
        if (byKey != null) return byKey;
        for (Enchantment e : Enchantment.values()) {
            if (e.getKey().getKey().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    private int parseInt(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> subCommands = Arrays.asList("apply", "remove", "list", "give", "gui", "reload", "maxlevel", "vlevel");
            for (String sub : subCommands) {
                if (sub.startsWith(input)) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();
            switch (subCommand) {
                case "apply", "remove" -> {
                    for (String id : enchantManager.getAllEnchantments().keySet()) {
                        if (id.startsWith(input)) {
                            completions.add(id);
                        }
                    }
                }
                case "give" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().toLowerCase().startsWith(input)) {
                            completions.add(p.getName());
                        }
                    }
                }
                case "vlevel" -> {
                    for (Enchantment e : Enchantment.values()) {
                        String key = e.getKey().getKey().toLowerCase();
                        if (key.startsWith(input)) {
                            completions.add(key);
                        }
                    }
                }
            }
            return completions;
        }
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String input = args[2].toLowerCase();
            if (subCommand.equals("give")) {
                for (String id : enchantManager.getAllEnchantments().keySet()) {
                    if (id.startsWith(input)) {
                        completions.add(id);
                    }
                }
            } else if (subCommand.equals("apply") || subCommand.equals("vlevel")) {
                for (int i = 1; i <= 255; i++) {
                    completions.add(String.valueOf(i));
                }
            }
            return completions;
        }
        if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give")) {
                String enchantId = args[2].toLowerCase();
                CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
                if (enchant != null) {
                    for (int i = 1; i <= enchant.getMaxLevel(); i++) {
                        completions.add(String.valueOf(i));
                    }
                }
            }
            return completions;
        }
        return completions;
    }
}
