package com.XiaoR.customenchantments.manager;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.enchantments.impl.*;
import com.XiaoR.customenchantments.gui.EnchantCategory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class EnchantManager {
    private final CustomEnchantments plugin;
    private final NamespacedKey enchantKey;
    private final Map<String, CustomEnchantment> registeredEnchantments;
    private final Gson gson;
    private final Type mapType;
    private FileConfiguration enchantConfig;

    private static final Map<String, Set<Material>> MATERIAL_GROUPS;

    static {
        Map<String, Set<Material>> groups = new HashMap<>();
        groups.put("SWORD", Set.of(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
                Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD));
        groups.put("AXE", Set.of(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
                Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE));
        groups.put("PICKAXE", Set.of(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
                Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE));
        groups.put("SHOVEL", Set.of(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
                Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL));
        groups.put("HOE", Set.of(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
                Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE));
        groups.put("BOW", Set.of(Material.BOW));
        groups.put("CROSSBOW", Set.of(Material.CROSSBOW));
        groups.put("HELMET", Set.of(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
                Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET));
        groups.put("CHESTPLATE", Set.of(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE));
        groups.put("LEGGINGS", Set.of(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
                Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS));
        groups.put("BOOTS", Set.of(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
                Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS));
        groups.put("ARMOR", Set.of(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
                Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
                Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
                Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
                Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
                Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
                Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS));
        groups.put("WEAPON", Set.of(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
                Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
                Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
                Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
                Material.BOW, Material.CROSSBOW));
        groups.put("TOOL", Set.of(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
                Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
                Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
                Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
                Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
                Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
                Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
                Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE));
        MATERIAL_GROUPS = Collections.unmodifiableMap(groups);
    }

    public EnchantManager(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantKey = new NamespacedKey(plugin, "custom_enchant");
        this.registeredEnchantments = new LinkedHashMap<>();
        this.gson = new Gson();
        this.mapType = new TypeToken<Map<String, Integer>>() {}.getType();
    }

    public FileConfiguration getEnchantConfig() {
        return enchantConfig;
    }

    public static Set<Material> expandMaterials(List<String> materialNames) {
        Set<Material> result = new HashSet<>();
        if (materialNames == null) return result;
        for (String name : materialNames) {
            String upper = name.toUpperCase();
            if (MATERIAL_GROUPS.containsKey(upper)) {
                result.addAll(MATERIAL_GROUPS.get(upper));
            } else {
                try {
                    Material mat = Material.valueOf(upper);
                    result.add(mat);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return result;
    }

    public void loadEnchantments() {
        registeredEnchantments.clear();
        File enchantFile = new File(plugin.getDataFolder(), "enchantments.yml");
        if (!enchantFile.exists()) {
            plugin.saveResource("enchantments.yml", false);
        }
        enchantConfig = YamlConfiguration.loadConfiguration(enchantFile);

        registerEnchantment(new TNTBurstEnchant());
        registerEnchantment(new LightningStrikeEnchant());
        registerEnchantment(new FireStormEnchant());
        registerEnchantment(new IceFreezeEnchant());
        registerEnchantment(new LifeStealEnchant());
        registerEnchantment(new ExplosionEnchant());
        registerEnchantment(new TeleportStrikeEnchant());
        registerEnchantment(new MagneticEnchant());
        registerEnchantment(new PoisonBladeEnchant());
        registerEnchantment(new WitherTouchEnchant());
        registerEnchantment(new VampirismEnchant());
        registerEnchantment(new ExecuteEnchant());
        registerEnchantment(new CleaveEnchant());
        registerEnchantment(new ThunderSlashEnchant());
        registerEnchantment(new BleedEnchant());
        registerEnchantment(new KnockbackMasterEnchant());
        registerEnchantment(new CritChanceEnchant());
        registerEnchantment(new ParryEnchant());
        registerEnchantment(new BerserkEnchant());
        registerEnchantment(new SoulReapEnchant());
        registerEnchantment(new MultishotBurstEnchant());
        registerEnchantment(new HomingArrowEnchant());
        registerEnchantment(new PoisonArrowEnchant());
        registerEnchantment(new ExplosiveArrowEnchant());
        registerEnchantment(new WebShotEnchant());
        registerEnchantment(new GravityArrowEnchant());
        registerEnchantment(new VeinMinerEnchant());
        registerEnchantment(new TimberEnchant());
        registerEnchantment(new AutoSmeltEnchant());
        registerEnchantment(new FortuneBoostEnchant());
        registerEnchantment(new ExcavatorEnchant());
        registerEnchantment(new ReplantEnchant());
        registerEnchantment(new MoltenArmorEnchant());
        registerEnchantment(new ThornsSpikeEnchant());
        registerEnchantment(new SpeedBoostArmorEnchant());
        registerEnchantment(new JumpBoostArmorEnchant());
        registerEnchantment(new NightVisionHelmEnchant());
        registerEnchantment(new RegenerationArmorEnchant());
        registerEnchantment(new ResistanceArmorEnchant());
        registerEnchantment(new AquaAffinityPlusEnchant());
        registerEnchantment(new FeatherFallEnchant());
        registerEnchantment(new InvisibilityCloakEnchant());
        registerEnchantment(new ExperienceBoostEnchant());
        registerEnchantment(new LootingMasterEnchant());
        registerEnchantment(new DurabilityBlessingEnchant());
    }

    private void registerEnchantment(CustomEnchantment enchant) {
        registeredEnchantments.put(enchant.getId(), enchant);
    }

    public Map<String, CustomEnchantment> getAllEnchantments() {
        return registeredEnchantments;
    }

    public CustomEnchantment getEnchant(String id) {
        return registeredEnchantments.get(id);
    }

    public boolean isEnchantable(ItemStack item, String enchantId) {
        CustomEnchantment enchant = getEnchant(enchantId);
        if (enchant == null) return false;
        Set<Material> materials = enchant.getApplicableMaterials();
        if (materials == null || materials.isEmpty()) return false;
        return materials.contains(item.getType());
    }

    public Map<String, Integer> getAllEnchants(ItemStack item) {
        if (item == null || item.getType().isAir()) return new HashMap<>();
        if (!item.hasItemMeta()) return new HashMap<>();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String json = pdc.get(enchantKey, PersistentDataType.STRING);
        if (json == null || json.isEmpty()) return new HashMap<>();
        try {
            Map<String, Integer> map = gson.fromJson(json, mapType);
            return map != null ? map : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public boolean applyEnchant(ItemStack item, String enchantId, int level) {
        CustomEnchantment enchant = getEnchant(enchantId);
        if (enchant == null) return false;
        if (!isEnchantable(item, enchantId)) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Map<String, Integer> enchants = getAllEnchants(item);
        enchants.put(enchantId, level);
        saveEnchants(meta, enchants);
        updateItemLore(meta, enchants);
        meta.setEnchantmentGlintOverride(true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        boolean hasSuperHighVanilla = false;
        Map<Enchantment, Integer> vanillaEnchants = new HashMap<>(meta.getEnchants());
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            vanillaEnchants.putAll(storageMeta.getStoredEnchants());
        }
        for (int vanillaLevel : vanillaEnchants.values()) {
            if (vanillaLevel > 10) { hasSuperHighVanilla = true; break; }
        }
        if (hasSuperHighVanilla) {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        } else {
            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        }
        item.setItemMeta(meta);
        return true;
    }

    public boolean removeEnchant(ItemStack item, String enchantId) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Map<String, Integer> enchants = getAllEnchants(item);
        if (!enchants.containsKey(enchantId)) return false;
        enchants.remove(enchantId);
        saveEnchants(meta, enchants);
        updateItemLore(meta, enchants);
        if (enchants.isEmpty() && !meta.hasEnchants()) {
            meta.setEnchantmentGlintOverride(null);
        }
        boolean hasSuperHighVanilla = false;
        Map<Enchantment, Integer> vanillaEnchants = new HashMap<>(meta.getEnchants());
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            vanillaEnchants.putAll(storageMeta.getStoredEnchants());
        }
        for (int vanillaLevel : vanillaEnchants.values()) {
            if (vanillaLevel > 10) { hasSuperHighVanilla = true; break; }
        }
        if (hasSuperHighVanilla) {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        } else {
            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        }
        item.setItemMeta(meta);
        return true;
    }

    private void saveEnchants(ItemMeta meta, Map<String, Integer> enchants) {
        if (enchants.isEmpty()) {
            meta.getPersistentDataContainer().remove(enchantKey);
        } else {
            String json = gson.toJson(enchants);
            meta.getPersistentDataContainer().set(enchantKey, PersistentDataType.STRING, json);
        }
    }

    private void updateItemLore(ItemMeta meta, Map<String, Integer> enchants) {
        List<String> lore = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            CustomEnchantment enchant = getEnchant(entry.getKey());
            if (enchant == null) continue;
            String name = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
            String line = ChatColor.LIGHT_PURPLE + "\u2726 " + name + " " + ChatColor.GRAY + "Lv." + entry.getValue();
            lore.add(line);
        }
        if (meta.hasLore()) {
            List<String> existing = meta.getLore();
            for (String l : existing) {
                String stripped = ChatColor.stripColor(l);
                if (!stripped.startsWith("\u2726 ")) {
                    lore.add(l);
                }
            }
        }
        meta.setLore(lore.isEmpty() ? null : lore);
    }

    public ItemStack createEnchantedBook(String enchantId, int level) {
        CustomEnchantment enchant = getEnchant(enchantId);
        if (enchant == null) return null;
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return null;
        String displayName = ChatColor.translateAlternateColorCodes('&', enchant.getDisplayName());
        meta.setDisplayName(ChatColor.AQUA + "\u2726 " + displayName + " " + ChatColor.GOLD + "Lv." + level);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
                org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setEnchantmentGlintOverride(true);
        Map<String, Integer> enchants = new HashMap<>();
        enchants.put(enchantId, level);
        String json = gson.toJson(enchants);
        meta.getPersistentDataContainer().set(enchantKey, PersistentDataType.STRING, json);
        List<String> lore = new ArrayList<>();
        for (String desc : enchant.getDescription()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', desc));
        }
        lore.add("");
        lore.add(ChatColor.LIGHT_PURPLE + "\u2726 " + displayName + " " + ChatColor.GRAY + "Lv." + level);
        meta.setLore(lore);
        book.setItemMeta(meta);
        return book;
    }

    public String getMaterialCategoryNames(Set<Material> materials) {
        if (materials == null || materials.isEmpty()) return "\u65e0";
        Set<String> categories = new LinkedHashSet<>();
        for (Material mat : materials) {
            String name = mat.name();
            if (name.endsWith("_SWORD")) categories.add("\u5251");
            else if (name.endsWith("_AXE")) categories.add("\u65a7");
            else if (name.endsWith("_PICKAXE")) categories.add("\u9562");
            else if (name.endsWith("_SHOVEL")) categories.add("\u94f2");
            else if (name.endsWith("_HOE")) categories.add("\u9504");
            else if (mat == Material.BOW) categories.add("\u5f13");
            else if (mat == Material.CROSSBOW) categories.add("\u5f29");
            else if (name.endsWith("_HELMET")) categories.add("\u5934\u76d4");
            else if (name.endsWith("_CHESTPLATE")) categories.add("\u80f8\u7532");
            else if (name.endsWith("_LEGGINGS")) categories.add("\u62a4\u817f");
            else if (name.endsWith("_BOOTS")) categories.add("\u9774\u5b50");
        }
        return String.join("\u3001", categories);
    }

    public List<CustomEnchantment> getEnchantsForCategory(EnchantCategory category) {
        List<CustomEnchantment> result = new ArrayList<>();
        for (CustomEnchantment enchant : registeredEnchantments.values()) {
            if (category.matches(enchant)) {
                result.add(enchant);
            }
        }
        return result;
    }
}
