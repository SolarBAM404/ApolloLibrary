package me.solar.apolloLibrary.utils;

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
            List<Entity> found = new ArrayList<>();

            for(Entity nearby : location.getWorld().getEntities()) {
                if (nearby.getLocation().distance(location) <= radius) {
                    found.add(nearby);
                }
            }

            return found;
        }
    }

    public static <T extends LivingEntity> T findNearestEntity(Location center, double range3D, Class<T> entityClass) {
        List<T> found = new ArrayList<>();

        for(Entity nearby : getNearbyEntities(center, range3D)) {
            if (entityClass.isAssignableFrom(nearby.getClass())) {
                //noinspection unchecked
                found.add((T) nearby);
            }
        }

        found.sort(Comparator.comparingDouble(t -> t.getLocation().distance(center)));
        return found.isEmpty() ? null : found.getFirst();
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

        if (entity instanceof Mob) {
            target = ((Mob)entity).getTarget();
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
            return 20.0F;
        } else {
            Location location = Bukkit.getWorlds().getFirst().getSpawnLocation();
            location.setY(0.0F);
            Entity entity = location.getWorld().spawnEntity(location, type);
            Valid.checkBoolean(entity instanceof LivingEntity, "Cannot use getDefaultHealth for non-living entity: " + type);
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

        for(Entity passenger : entity.getPassengers()) {
            passenger.remove();
        }

    }

    public static boolean isAggressive(Entity entity) {
        if (!(entity instanceof Ghast) && !(entity instanceof Slime)) {
            if (entity instanceof Wolf && ((Wolf)entity).isAngry()) {
                return true;
            } else {
                return !(entity instanceof Animals) && entity instanceof Creature;
            }
        } else {
            return true;
        }
    }

    public static boolean isCreature(Entity entity) {
        return entity instanceof Slime || entity instanceof Creature;
    }

    public static boolean canBeCleaned(Entity entity) {
        return entity instanceof FallingBlock || entity instanceof Item || entity instanceof Projectile || entity instanceof ExperienceOrb;
    }

    public static Item dropItem(Location location, ItemStack item) {
        return location.getWorld().dropItem(location, item);
    }

    public static void trackFalling(Entity entity, Runnable hitGroundListener) {
        track(entity, 600, null, hitGroundListener);
    }

    public static void trackFlying(Entity entity, Runnable flyListener) {
        track(entity, 600, flyListener, null);
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
