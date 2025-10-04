package me.solar.apolloLibrary;

import me.solar.apolloLibrary.core.ApolloPlugin;
import me.solar.apolloLibrary.listeners.HitTracking;
import me.solar.apolloLibrary.runnables.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class EntityUtils {

    private EntityUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static boolean registeredHitListener = false;

    public static Entity spawnEntity(EntityType entityType, Location location) {
        return location.getWorld().spawnEntity(location, entityType);
    }

    public static FallingBlock spawnFallingBlock(Location location, Material material) {
        FallingBlock entity = (FallingBlock)spawnEntity(EntityType.FALLING_BLOCK, location);
        entity.setBlockData(material.createBlockData());
        return entity;
    }

    public static Collection<Entity> getNearbyEntities(Location location, double radius) {
        try {
            return location.getWorld().getNearbyEntities(location, radius, radius, radius);
        } catch (Throwable var7) {
            List<Entity> found = new ArrayList();

            for(Entity nearby : location.getWorld().getEntities()) {
                if (nearby.getLocation().distance(location) <= radius) {
                    found.add(nearby);
                }
            }

            return found;
        }
    }

    public static <T extends LivingEntity> T findNearestEntity(Location center, double range3D, Class<T> entityClass) {
        List<T> found = new ArrayList();

        for(Entity nearby : getNearbyEntities(center, range3D)) {
            if (nearby instanceof LivingEntity && entityClass.isAssignableFrom(nearby.getClass())) {
                found.add((T) nearby);
            }
        }

        Collections.sort(found, (first, second) -> Double.compare(first.getLocation().distance(center), second.getLocation().distance(center)));
        return (T)(found.isEmpty() ? null : (LivingEntity)found.get(0));
    }

    public static Player getTargetPlayer(Entity entity) {
        Entity target = getTarget(entity);
        if (target == null) {
            return null;
        } else {
            return target instanceof Player && target.getLocation().getWorld().equals(entity.getWorld()) ? (Player)target : null;
        }
    }

    public static Entity getTarget(Entity entity) {
        Entity target = null;

        try {
            if (entity instanceof Mob) {
                target = ((Mob)entity).getTarget();
            }
        } catch (Throwable var3) {
        }

        if (target == null && entity instanceof Creature) {
            target = ((Creature)entity).getTarget();
        }

        return target;
    }

    public static double getHealth(LivingEntity entity) {
        return entity.getHealth();
    }

    public static double getDefaultHealth(EntityType type) {
        if (type == EntityType.PLAYER) {
            return (double)20.0F;
        } else {
            Location location = ((World) Bukkit.getWorlds().get(0)).getSpawnLocation();
            location.setY((double)0.0F);
            Entity entity = location.getWorld().spawnEntity(location, type);
            Valid.checkBoolean(entity instanceof LivingEntity, "Cannot use getDefaultHealth for non-living entity: " + String.valueOf(type));
            double health = getHealth((LivingEntity)entity);
            entity.remove();
            return health;
        }
    }

    public static void rotateYaw(Location location, Location facing) {
        float yaw = (float)Math.toDegrees(Math.atan2(facing.getZ() - location.getZ(), facing.getX() - location.getX())) - 90.0F;
        location.setYaw(yaw);
    }

    public static void removeVehiclesAndPassengers(Entity entity) {
        Entity vehicle = entity.getVehicle();

        while(vehicle != null) {
            Entity copyOf = vehicle;
            vehicle = vehicle.getVehicle();
            copyOf.remove();
        }

        try {
            for(Entity passenger : entity.getPassengers()) {
                passenger.remove();
            }
        } catch (NoSuchMethodError var4) {
            Entity passenger = entity.getPassenger();
            if (passenger != null) {
                passenger.remove();
            }
        }

    }

    public static boolean isAggressive(Entity entity) {
        if (!(entity instanceof Ghast) && !(entity instanceof Slime)) {
            if (entity instanceof Wolf && ((Wolf)entity).isAngry()) {
                return true;
            } else {
                return entity instanceof Animals ? false : entity instanceof Creature;
            }
        } else {
            return true;
        }
    }

    public static boolean isCreature(Entity entity) {
        return entity instanceof Slime || entity instanceof Wolf || entity instanceof Creature;
    }

    public static boolean canBeCleaned(Entity entity) {
        return entity instanceof FallingBlock || entity instanceof Item || entity instanceof Projectile || entity instanceof ExperienceOrb;
    }

    public static Item dropItem(Location location, ItemStack item) {
        return location.getWorld().dropItem(location, item);
    }

    public static void trackFalling(Entity entity, Runnable hitGroundListener) {
        track(entity, 600, (Runnable)null, hitGroundListener);
    }

    public static void trackFlying(Entity entity, Runnable flyListener) {
        track(entity, 600, flyListener, (Runnable)null);
    }

    public static void track(final Entity entity, int timeoutTicks, final Runnable flyListener, final Runnable hitGroundListener) {
        if (flyListener == null && hitGroundListener == null) {
            throw new RuntimeException("Cannot track entity with fly and hit listeners on null!");
        } else {
            TimerTask timerTask = new TimerTask(timeoutTicks) {
                public void run() {
                    if (entity.isOnGround()) {
                        if (hitGroundListener != null) {
                            hitGroundListener.run();
                        }
                    }
                }
            };
            Common.runTask(ApolloPlugin.getStaticInstance(), timerTask);

        }
    }

    public static void trackHit(Projectile projectile, Consumer<ProjectileHitEvent> hitTask) {
        HitTracking.addFlyingProjectile(projectile, hitTask);
        if (!registeredHitListener) {
            Common.registerListener(ApolloPlugin.getStaticInstance(), new HitTracking());
            registeredHitListener = true;
        }

    }

}
