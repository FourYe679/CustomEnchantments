package com.XiaoR.customenchantments.enchantments.impl;

import com.XiaoR.customenchantments.CustomEnchantments;
import com.XiaoR.customenchantments.enchantments.CustomEnchantment;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomingArrowEnchant extends CustomEnchantment {
    private static final double CONE_COS = Math.cos(Math.toRadians(30));

    public HomingArrowEnchant() {
        super("homing_arrow");
    }

    @Override
    public void onBowShoot(Player player, AbstractArrow arrow, int level, ItemStack item) {
        if (level < 1) return;
        final double speed = arrow.getVelocity().length();
        if (speed < 0.0001) return;
        final double range = level * 10.0;
        final int maxTicks = level * 40;
        final Player shooter = player;
        final Vector shootDir = player.getEyeLocation().getDirection().normalize();
        final Location shootEyeLoc = player.getEyeLocation();
        new BukkitRunnable() {
            int ticks = 0;
            LivingEntity lockedTarget = null;

            @Override
            public void run() {
                if (arrow.isDead() || !arrow.isValid() || arrow.isOnGround()) {
                    cancel();
                    return;
                }
                if (ticks >= maxTicks) {
                    cancel();
                    return;
                }
                Location arrowLoc = arrow.getLocation();

                // Re-search target if not locked yet
                boolean needRetarget = false;
                if (lockedTarget == null) {
                    needRetarget = true;
                } else if (lockedTarget.isDead() || !lockedTarget.isValid()) {
                    needRetarget = true;
                    lockedTarget = null;
                }

                if (needRetarget) {
                    Collection<Entity> nearby = arrow.getWorld().getNearbyEntities(arrowLoc, range, range, range);
                    List<Candidate> candidates = new ArrayList<>();
                    for (Entity e : nearby) {
                        if (!(e instanceof LivingEntity)) continue;
                        if (e instanceof AbstractArrow) continue;
                        if (e.isDead()) continue;
                        if (!isValidTarget(e, shooter)) continue;
                        Vector toEntity = e.getLocation().toVector().subtract(shootEyeLoc.toVector());
                        double dist = toEntity.length();
                        if (dist < 0.001) continue;
                        toEntity.normalize();
                        double dot = toEntity.dot(shootDir);
                        if (dot < CONE_COS) continue;
                        if (!shooter.hasLineOfSight(e)) continue;
                        double distSq = e.getLocation().distanceSquared(arrowLoc);
                        candidates.add(new Candidate((LivingEntity) e, distSq));
                    }
                    if (!candidates.isEmpty()) {
                        candidates.sort((a, b) -> {
                            int pa = getPriority(a.entity);
                            int pb = getPriority(b.entity);
                            if (pa != pb) return Integer.compare(pb, pa);
                            return Double.compare(a.distSq, b.distSq);
                        });
                        lockedTarget = candidates.get(0).entity;
                    }
                }

                if (lockedTarget != null) {
                    // Check line of sight from arrow to each body part of the target
                    Location targetLoc = getVisibleBodyPart(arrowLoc, lockedTarget);
                    if (targetLoc == null) {
                        // Target fully behind cover — arrow stops tracking, flies straight
                        cancel();
                        return;
                    }
                    Vector toTarget = targetLoc.toVector().subtract(arrowLoc.toVector());
                    double len = toTarget.length();
                    if (len > 0.0001) {
                        toTarget.normalize().multiply(speed);
                        arrow.setVelocity(toTarget);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(CustomEnchantments.getInstance(), 1L, 1L);
    }

    private Location getVisibleBodyPart(Location arrowLoc, LivingEntity target) {
        World world = arrowLoc.getWorld();
        double eyeHeight = target.getEyeHeight();

        Location torso = target.getLocation().add(0, eyeHeight * 0.5, 0);
        Location head = target.getLocation().add(0, eyeHeight * 0.9, 0);
        Location feet = target.getLocation().add(0, 0.1, 0);

        // Return the first visible body part (torso first, then head, then feet)
        if (hasLineOfSight(arrowLoc, torso, world)) return torso;
        if (hasLineOfSight(arrowLoc, head, world)) return head;
        if (hasLineOfSight(arrowLoc, feet, world)) return feet;
        return null;
    }

    private boolean hasLineOfSight(Location from, Location to, World world) {
        Vector direction = to.toVector().subtract(from.toVector());
        double distance = from.distance(to);
        if (distance < 0.1) return true;
        direction.normalize();
        // Don't check fluids, do check transparent blocks (ignores water, grass, etc.)
        RayTraceResult result = world.rayTraceBlocks(from, direction, distance, FluidCollisionMode.NEVER, true);
        return result == null;
    }

    private int getPriority(LivingEntity entity) {
        // Boss-tier hostiles get highest priority
        if (entity instanceof org.bukkit.entity.EnderDragon) return 100;
        if (entity instanceof org.bukkit.entity.Wither) return 99;
        if (entity instanceof org.bukkit.entity.Warden) return 98;
        if (entity instanceof org.bukkit.entity.ElderGuardian) return 97;
        // Strong hostiles
        if (entity instanceof org.bukkit.entity.Hoglin) return 80;
        if (entity instanceof org.bukkit.entity.Zoglin) return 80;
        if (entity instanceof org.bukkit.entity.PiglinBrute) return 80;
        if (entity instanceof org.bukkit.entity.Ghast) return 75;
        if (entity instanceof org.bukkit.entity.Phantom) return 70;
        if (entity instanceof org.bukkit.entity.Shulker) return 70;
        if (entity instanceof org.bukkit.entity.Slime
                || entity instanceof org.bukkit.entity.MagmaCube) return 60;
        // Normal monsters (Zombie, Skeleton, Creeper, etc.)
        if (entity instanceof Monster) return 50;
        // Players are tracked but lower priority than hostiles
        if (entity instanceof Player) return 40;
        return 10;
    }

    private boolean isValidTarget(Entity entity, Player shooter) {
        // Track players (excluding the shooter)
        if (entity instanceof Player) return !entity.equals(shooter);
        // Track hostile mobs
        return isHostile(entity);
    }

    private boolean isHostile(Entity entity) {
        if (entity instanceof Monster) return true;
        if (entity instanceof org.bukkit.entity.Ghast) return true;
        if (entity instanceof org.bukkit.entity.Phantom) return true;
        if (entity instanceof org.bukkit.entity.Slime) return true;
        if (entity instanceof org.bukkit.entity.MagmaCube) return true;
        if (entity instanceof org.bukkit.entity.Shulker) return true;
        if (entity instanceof org.bukkit.entity.EnderDragon) return true;
        if (entity instanceof org.bukkit.entity.Wither) return true;
        if (entity instanceof org.bukkit.entity.ElderGuardian) return true;
        if (entity instanceof org.bukkit.entity.Hoglin) return true;
        if (entity instanceof org.bukkit.entity.Zoglin) return true;
        if (entity instanceof org.bukkit.entity.PiglinBrute) return true;
        if (entity instanceof org.bukkit.entity.Warden) return true;
        return false;
    }

    private static class Candidate {
        final LivingEntity entity;
        final double distSq;
        Candidate(LivingEntity entity, double distSq) {
            this.entity = entity;
            this.distSq = distSq;
        }
    }
}