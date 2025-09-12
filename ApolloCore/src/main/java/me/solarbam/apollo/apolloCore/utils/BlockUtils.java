package me.solarbam.apollo.apolloCore.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public final class BlockUtils {

    private BlockUtils() {}

    public static Pair<Location, Location> minMax(Location primary, Location secondary) {
        double minX = Math.min(primary.getX(), secondary.getX());
        double minY = Math.min(primary.getY(), secondary.getY());
        double minZ = Math.min(primary.getZ(), secondary.getZ());
        double maxX = Math.max(primary.getX(), secondary.getX());
        double maxY = Math.max(primary.getY(), secondary.getY());
        double maxZ = Math.max(primary.getZ(), secondary.getZ());
        return new Pair<>(
            new Location(primary.getWorld(), minX, minY, minZ),
            new Location(primary.getWorld(), maxX, maxY, maxZ)
        );
    }

    public static List<Location> boundingBox(Location primary, Location secondary) {
        List<Location> locations = new ArrayList<>();
        Pair<Location, Location> pair = minMax(primary, secondary);
        for (int x = (int) pair.first.getX(); x <= (int) pair.second.getX(); x++) {
            for (int y = (int) pair.first.getY(); y <= (int) pair.second.getY(); y++) {
                for (int z = (int) pair.first.getZ(); z <= (int) pair.second.getZ(); z++) {
                    locations.add(new Location(primary.getWorld(), x, y, z));
                }
            }
        }
        return locations;
    }

    public static List<ApolloVector> plotLine(Location alpha, Location bravo) {
        return plotLine(ApolloVector.fromLocation(alpha), ApolloVector.fromLocation(bravo));
    }

    public static List<ApolloVector> plotLine(org.bukkit.util.Vector alpha, org.bukkit.util.Vector bravo) {
        return plotLine(ApolloVector.fromBukkit(alpha), ApolloVector.fromBukkit(bravo));
    }

    public static List<ApolloVector> plotLine(ApolloVector alpha, ApolloVector bravo) {
        List<ApolloVector> locations = new ArrayList<>();
        ApolloVector delta = bravo.subtract(alpha);
        double length = delta.magnitude();
        ApolloVector direction = delta.divide(new ApolloVector(length, length, length));
        for (int i = 0; i < (int) length; i++) {
            ApolloVector location = alpha.add(direction.multiply(new ApolloVector(i, i, i)));
            locations.add(location);
        }
        return locations;
    }

    public static Location getMinLocation(Location primary, Location secondary) {
        double minX = Math.min(primary.getX(), secondary.getX());
        double minY = Math.min(primary.getY(), secondary.getY());
        double minZ = Math.min(primary.getZ(), secondary.getZ());
        return new Location(primary.getWorld(), minX, minY, minZ);
    }

    public static Location getMaxLocation(Location primary, Location secondary) {
        double maxX = Math.max(primary.getX(), secondary.getX());
        double maxY = Math.max(primary.getY(), secondary.getY());
        double maxZ = Math.max(primary.getZ(), secondary.getZ());
        return new Location(primary.getWorld(), maxX, maxY, maxZ);
    }

    public static Location getCenterLocation(Location primary, Location secondary) {
        Pair<Location, Location> pair = minMax(primary, secondary);
        Location minPair = pair.first;
        Location maxPair = pair.second;
        double x = minPair.getX() + (maxPair.getX() - minPair.getX()) / 2;
        double y = minPair.getY() + (maxPair.getY() - minPair.getY()) / 2;
        double z = minPair.getZ() + (maxPair.getZ() - minPair.getZ()) / 2;
        return new Location(primary.getWorld(), x, y, z);
    }

    public static int getHeight(Location primary, Location secondary) {
        return (int) (Math.max(primary.getY(), secondary.getY()) - Math.min(primary.getY(), secondary.getY()));
    }

    public static int getWidth(Location primary, Location secondary) {
        return (int) (Math.max(primary.getX(), secondary.getX()) - Math.min(primary.getX(), secondary.getX()));
    }

    public static int getLength(Location primary, Location secondary) {
        return (int) (Math.max(primary.getZ(), secondary.getZ()) - Math.min(primary.getZ(), secondary.getZ()));
    }

    public static int getVolume(Location primary, Location secondary) {
        return getHeight(primary, secondary) * getWidth(primary, secondary) * getLength(primary, secondary);
    }

    public static int getSurfaceArea(Location primary, Location secondary) {
        int height = getHeight(primary, secondary);
        int width = getWidth(primary, secondary);
        int length = getLength(primary, secondary);
        return 2 * (height * width + height * length + width * length);
    }

    public static double getDiagonal(Location primary, Location secondary) {
        return primary.distance(secondary);
    }

    public static boolean equalsLocation(Location primary, Location secondary) {
        return primary.getX() == secondary.getX() &&
               primary.getY() == secondary.getY() &&
               primary.getZ() == secondary.getZ();
    }

    public static boolean isSafeSpawnLocation(Location location) {
        return isSafeSpawnLocation(location.clone(), 2);
    }

    public static boolean isSafeSpawnLocation(Location location, int height) {
        for (int i = 0; i < height; i++) {
            Location check = location.clone().add(0.0, i, 0.0);
            if (check.getBlock().getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public static void shootBlock(Location location, ApolloVector velocity, ApolloVector direction) {
        var fallingBlock = WorldUtils.spawnFallingBlock(location, Material.STONE);
        fallingBlock.setVelocity(ApolloVector.toBukkit(velocity.multiply(direction)));
    }

    // Helper Pair class
    public static class Pair<F, S> {
        public final F first;
        public final S second;
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}

