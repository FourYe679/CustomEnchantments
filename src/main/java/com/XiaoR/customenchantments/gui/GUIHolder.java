package com.XiaoR.customenchantments.gui;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
public class GUIHolder implements InventoryHolder {
    private final GUIType type;
    private final EnchantCategory category;
    private final int page;
    private final String selectedEnchantId;
    private final int selectedLevel;
    private Inventory inventory;
    public GUIHolder(GUIType type) {
        this(type, null, 0);
    }
    public GUIHolder(GUIType type, EnchantCategory category, int page) {
        this(type, category, page, null, 0);
    }
    public GUIHolder(GUIType type, EnchantCategory category, int page, String selectedEnchantId, int selectedLevel) {
        this.type = type;
        this.category = category;
        this.page = page;
        this.selectedEnchantId = selectedEnchantId;
        this.selectedLevel = selectedLevel;
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    public GUIType getType() {
        return type;
    }
    public EnchantCategory getCategory() {
        return category;
    }
    public int getPage() {
        return page;
    }
    public String getSelectedEnchantId() {
        return selectedEnchantId;
    }
    public int getSelectedLevel() {
        return selectedLevel;
    }
}
