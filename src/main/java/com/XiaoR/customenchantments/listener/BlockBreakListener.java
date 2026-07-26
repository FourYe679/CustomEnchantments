package com.XiaoR.customenchantments.listener;
import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import com.XiaoR.customenchantments.manager.CooldownManager;
import com.XiaoR.customenchantments.manager.EnchantManager;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;
public class BlockBreakListener implements Listener {
    private final CustomEnchantments plugin;
    private final EnchantManager enchantManager;
    private final CooldownManager cooldownManager;
    public BlockBreakListener(CustomEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
        this.cooldownManager = plugin.getCooldownManager();
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;
        Map<String, Integer> enchants = enchantManager.getAllEnchants(item);
        if (enchants.isEmpty()) return;
        boolean hasVeinMiner = false;
        boolean hasTimber = false;
        boolean hasAutoSmelt = false;
        boolean hasFortuneBoost = false;
        boolean hasExcavator = false;
        boolean hasReplant = false;
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            String enchantId = entry.getKey();
            int level = entry.getValue();
            CustomEnchantment enchant = enchantManager.getEnchant(enchantId);
            if (enchant == null || !enchant.isEnabled()) continue;
            if (cooldownManager.checkAndTrigger(player, enchantId, enchant.getCooldown())) {
                enchant.onActivate(player, null, level, item);
            }
            switch (enchantId) {
                case "vein_miner":
                    hasVeinMiner = true;
                    break;
                case "timber":
                    hasTimber = true;
                    break;
                case "auto_smelt":
                    hasAutoSmelt = true;
                    break;
                case "fortune_boost":
                    hasFortuneBoost = true;
                    break;
                case "excavator":
                    hasExcavator = true;
                    break;
                case "replant":
                    hasReplant = true;
                    break;
            }
        }
        int[][] cubeOffsets = new int[26][];
        int idx = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    cubeOffsets[idx++] = new int[]{dx, dy, dz};
                }
            }
        }
        if (hasVeinMiner && isOre(block.getType())) {
            int veinMinerLevel = enchants.getOrDefault("vein_miner", 1);
            String baseOreType = getOreBaseType(block.getType());
            Set<Location> visited = new HashSet<>();
            Queue<Block> queue = new LinkedList<>();
            queue.add(block);
            visited.add(block.getLocation());
            int maxBlocks = veinMinerLevel * 8 + 8;
            int broken = 0;
            while (!queue.isEmpty() && broken < maxBlocks) {
                Block current = queue.poll();
                boolean isStartingBlock = current.equals(block);
                if (!isStartingBlock) {
                    if (!isOre(current.getType())) continue;
                    if (!getOreBaseType(current.getType()).equals(baseOreType)) continue;
                    broken++;
                    final Material brokenType = current.getType();
                    Block target = current;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            target.breakNaturally(item);
                        }
                    }.runTask(plugin);
                    int exp = getBlockExp(brokenType);
                    if (exp > 0) {
                        player.giveExp(exp);
                    }
                }
                for (int[] offset : cubeOffsets) {
                    Block neighbor = current.getRelative(offset[0], offset[1], offset[2]);
                    if (!visited.contains(neighbor.getLocation())
                            && isOre(neighbor.getType())
                            && getOreBaseType(neighbor.getType()).equals(baseOreType)) {
                        visited.add(neighbor.getLocation());
                        queue.add(neighbor);
                    }
                }
            }
        }
        if (hasTimber && isLog(block.getType())) {
            int timberLevel = enchants.getOrDefault("timber", 1);
            Set<Location> visited = new HashSet<>();
            Queue<Block> queue = new LinkedList<>();
            queue.add(block);
            visited.add(block.getLocation());
            int maxLogs = timberLevel * 16 + 16;
            int broken = 0;
            while (!queue.isEmpty() && broken < maxLogs) {
                Block current = queue.poll();
                boolean isStartingBlock = current.equals(block);
                if (!isStartingBlock) {
                    if (!isLog(current.getType())) continue;
                    broken++;
                    Block target = current;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            target.breakNaturally(item);
                        }
                    }.runTask(plugin);
                }
                for (int[] offset : cubeOffsets) {
                    Block neighbor = current.getRelative(offset[0], offset[1], offset[2]);
                    if (!visited.contains(neighbor.getLocation()) && isLog(neighbor.getType())) {
                        visited.add(neighbor.getLocation());
                        queue.add(neighbor);
                    }
                }
            }
        }
        if (hasAutoSmelt) {
            int autoSmeltLevel = enchants.getOrDefault("auto_smelt", 1);
            Material blockType = block.getType();
            Material smeltResult = getSmeltResult(blockType);
            if (smeltResult != null) {
                event.setDropItems(false);
                int amount = 1;
                Collection<ItemStack> originalDrops = block.getDrops(item);
                if (originalDrops != null && !originalDrops.isEmpty()) {
                    amount = originalDrops.iterator().next().getAmount();
                }
                if (hasFortuneBoost) {
                    int fortuneLevel = enchants.getOrDefault("fortune_boost", 1);
                    int extraAmount = new Random().nextInt(fortuneLevel + 1);
                    amount = amount * (1 + extraAmount);
                }
                ItemStack smeltedItem = new ItemStack(smeltResult, amount);
                player.getWorld().dropItemNaturally(block.getLocation(), smeltedItem);
            }
        } else if (hasFortuneBoost) {
            int fortuneLevel = enchants.getOrDefault("fortune_boost", 1);
            event.setDropItems(false);
            Collection<ItemStack> originalDrops = block.getDrops(item);
            for (ItemStack drop : originalDrops) {
                int bonusMultiplier = fortuneLevel;
                int extraAmount = new Random().nextInt(bonusMultiplier + 1);
                int totalAmount = drop.getAmount() * (1 + extraAmount);
                drop.setAmount(totalAmount);
                player.getWorld().dropItemNaturally(block.getLocation(), drop);
            }
        }
        if (hasExcavator) {
            int excavatorLevel = enchants.getOrDefault("excavator", 1);
            int radius = excavatorLevel >= 3 ? 2 : 1;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        Block target = block.getRelative(x, y, z);
                        if (target.getType() != Material.AIR && !target.getType().name().endsWith("BEDROCK")) {
                            final Material brokenType = target.getType();
                            Block t = target;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    t.breakNaturally(item);
                                }
                            }.runTask(plugin);
                            int exp = getBlockExp(brokenType);
                            if (exp > 0) {
                                player.giveExp(exp);
                            }
                        }
                    }
                }
            }
        }
        if (hasReplant) {
            Material cropType = block.getType();
            Material seedType = getSeedType(cropType);
            if (seedType != null) {
                org.bukkit.block.data.Ageable cropData = (org.bukkit.block.data.Ageable) block.getBlockData();
                boolean wasMature = cropData.getAge() >= cropData.getMaximumAge();
                if (wasMature && consumeSeed(player, seedType)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (block.getType() == Material.AIR) {
                                block.setType(cropType);
                                org.bukkit.block.data.Ageable newData = (org.bukkit.block.data.Ageable) block.getBlockData();
                                newData.setAge(0);
                                block.setBlockData(newData);
                            }
                        }
                    }.runTaskLater(plugin, 1L);
                }
            }
        }
    }
    private Material getSmeltResult(Material input) {
        Iterator<Recipe> recipes = plugin.getServer().recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                if (furnaceRecipe.getInput().getType() == input) {
                    return furnaceRecipe.getResult().getType();
                }
            }
        }
        return switch (input) {
            case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
            case SAND -> Material.GLASS;
            case COBBLESTONE -> Material.STONE;
            case CLAY_BALL -> Material.BRICK;
            case NETHERRACK -> Material.NETHER_BRICK;
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG,
                    MANGROVE_LOG, CHERRY_LOG, CRIMSON_STEM, WARPED_STEM -> Material.CHARCOAL;
            default -> null;
        };
    }
    private Material getSeedType(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case PUMPKIN_STEM -> Material.PUMPKIN_SEEDS;
            case MELON_STEM -> Material.MELON_SEEDS;
            case TORCHFLOWER_CROP -> Material.TORCHFLOWER_SEEDS;
            case PITCHER_CROP -> Material.PITCHER_POD;
            default -> null;
        };
    }

    private boolean consumeSeed(Player player, Material seedType) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.getType() == seedType && stack.getAmount() > 0) {
                int amount = stack.getAmount();
                if (amount > 1) {
                    stack.setAmount(amount - 1);
                } else {
                    inv.setItem(i, null);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isOre(Material material) {
        String name = material.name();
        return name.endsWith("_ORE") || name.equals("ANCIENT_DEBRIS");
    }

    private String getOreBaseType(Material material) {
        String name = material.name();
        if (name.equals("ANCIENT_DEBRIS")) return "DEBRIS";
        if (name.startsWith("DEEPSLATE_")) {
            return name.substring("DEEPSLATE_".length());
        }
        if (name.startsWith("NETHER_")) {
            return name.substring("NETHER_".length());
        }
        return name;
    }

    private boolean isLog(Material material) {
        String name = material.name();
        if (name.endsWith("_LOG")) return true;
        if (name.equals("CRIMSON_STEM") || name.equals("WARPED_STEM")) return true;
        return false;
    }

    private int getBlockExp(Material material) {
        return switch (material) {
            case COAL_ORE -> new Random().nextInt(3);
            case DIAMOND_ORE -> 3 + new Random().nextInt(5);
            case EMERALD_ORE -> 3 + new Random().nextInt(5);
            case LAPIS_ORE -> 2 + new Random().nextInt(4);
            case NETHER_QUARTZ_ORE -> 2 + new Random().nextInt(3);
            case REDSTONE_ORE -> 1 + new Random().nextInt(4);
            case NETHER_GOLD_ORE -> new Random().nextInt(2);
            case DEEPSLATE_COAL_ORE -> new Random().nextInt(3);
            case DEEPSLATE_DIAMOND_ORE -> 3 + new Random().nextInt(5);
            case DEEPSLATE_EMERALD_ORE -> 3 + new Random().nextInt(5);
            case DEEPSLATE_LAPIS_ORE -> 2 + new Random().nextInt(4);
            case DEEPSLATE_REDSTONE_ORE -> 1 + new Random().nextInt(4);
            case SPAWNER -> 15 + new Random().nextInt(44);
            default -> 0;
        };
    }
}
