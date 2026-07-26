package com.XiaoR.customenchantments.enchantments.impl;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
public class TeleportStrikeEnchant extends CustomEnchantment {
    public TeleportStrikeEnchant() {
        super("teleport_strike");
    }
    @Override
    public void onActivate(Player player, Entity target, int level, ItemStack item) {
        if (target == null || target.getWorld() == null) return;
        World world = target.getWorld();
        Location targetLoc = target.getLocation();
        Location playerLoc = player.getLocation();
        Vector direction = targetLoc.toVector().subtract(playerLoc.toVector());
        if (direction.lengthSquared() == 0) return;
        direction.normalize();
        Location behindLoc = targetLoc.clone().add(direction.multiply(2));
        behindLoc.setY(targetLoc.getY());
        behindLoc.setYaw(targetLoc.getYaw());
        behindLoc.setPitch(targetLoc.getPitch());
        player.teleport(behindLoc);
        world.playSound(behindLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }
}
