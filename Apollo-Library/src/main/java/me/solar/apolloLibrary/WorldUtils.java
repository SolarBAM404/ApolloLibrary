package me.solar.apolloLibrary;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.*;

public class WorldUtils {

    private WorldUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Set<Location> getBoundingBox(Location location1, Location location2) {
        List<Vector> vectors = new ArrayList<>();
        Location min = getLocationMin(location1, location2);
        Location max = getLocationMax(location1, location2);
        int height = (int) (max.getY() - min.getY());
        List<Vector> bottomCorners = new ArrayList<>();
        bottomCorners.add(new Vector(min.getX(), min.getY(), min.getZ()));
        bottomCorners.add(new Vector(max.getX(), min.getY(), min.getZ()));
        bottomCorners.add(new Vector(max.getX(), min.getY(), max.getZ()));
        bottomCorners.add(new Vector(min.getX(), min.getY(), max.getZ()));

        for (int i = 0; i <= height; i++) {
            Vector p1 = bottomCorners.get(i).clone();
            Vector p2 = i + 1 < bottomCorners.size() ? bottomCorners.get(i + 1) : bottomCorners.get(0);
            Vector p3 = p1.add(new Vector(0, height, 0));
            Vector p4 = p2.add(new Vector(0, height, 0));
            vectors.addAll(plotLine(p1, p2));
            vectors.addAll(plotLine(p2, p3));
            vectors.addAll(plotLine(p3, p4));
            vectors.addAll(plotLine(p4, p1));

            for (double offset = 1; offset < height; offset++) {
                Vector p5 = p1.add(new Vector(0, offset, 0));
                Vector p6 = p2.add(new Vector(0, offset, 0));
                vectors.addAll(plotLine(p5, p6));
            }
        }

        return vectorsToLocations(location1.getWorld(), vectors);
    }

    public static Collection<Vector> plotLine(Vector pointA, Vector pointB) {
        List<Vector> vectors = new ArrayList<>();
        int points = (int) (pointB.distance(pointA) + 1);
        double length = pointA.distance(pointB);
        double gap = length / (double) (points - 1);
        Vector gapShapeVector = pointB.subtract(pointA).normalize().multiply(gap);

        for (int i = 0; i < points; ++i) {
            Vector vector = pointA.add(gapShapeVector.multiply(i));
            vectors.add(vector);
        }

        return vectors;
    }

    public static Set<Location> vectorsToLocations(World world, Collection<Vector> vectors) {
        Set<Location> locations = new HashSet<>();
        for (Vector vector : vectors) {
            locations.add(new Location(world, vector.getX(), vector.getY(), vector.getZ()));
        }
        return locations;
    }

    public static Location getLocationMin(Location location1, Location location2) {
        double x = Math.min(location1.getX(), location2.getX());
        double y = Math.min(location1.getY(), location2.getY());
        double z = Math.min(location1.getZ(), location2.getZ());
        return new Location(location1.getWorld(), x, y, z);
    }

    public static Location getLocationMax(Location location1, Location location2) {
        double x = Math.max(location1.getX(), location2.getX());
        double y = Math.max(location1.getY(), location2.getY());
        double z = Math.max(location1.getZ(), location2.getZ());
        return new Location(location1.getWorld(), x, y, z);
    }

    public static int getBlockHeight(Location location1, Location location2) {
        return (int) (location2.getY() - location1.getY());
    }

    public static Location getBlockCenter(Location location) {
        return new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
    }

    public static Vector getDistance(Location location1, Location location2) {
        return new Vector(location2.getX() - location1.getX(), location2.getY() - location1.getY(), location2.getZ() - location1.getZ());
    }

    public static Location fromDistance(Location location, Vector vector) {
        return location.clone().add(vector);
    }

    public static Set<Location> surrounding(Location location, int radius) {
        return surrounding(location, radius, radius, radius);
    }

    public static Set<Location> surrounding(Location location, int radx, int rady, int radz) {
        Set<Location> locations = new HashSet<>();
        World world = location.getWorld();
        int x0 = location.getBlockX();
        int y0 = location.getBlockY();
        int z0 = location.getBlockZ();
        for (int x = x0 - radx; x <= x0 + radx; x++) {
            for (int y = y0 - rady; y <= y0 + rady; y++) {
                for (int z = z0 - radz; z <= z0 + radz; z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }

    public static boolean isSpawnable(Location location) {
        Block block = location.getBlock();
        return block.isEmpty() && block.getRelative(BlockFace.UP).isEmpty();
    }

    public static boolean isSafeSpawnable(Location location) {
        Block block = location.getBlock();
        return block.isEmpty() && block.getRelative(BlockFace.UP).isEmpty() && block.getRelative(BlockFace.DOWN).getType().isSolid();
    }

    public static Location between(Location location1, Location location2) {
        return location1.clone().add(location2.clone().subtract(location1));
    }

    public static Location moveTowards(Location from, Location to) {
        return moveTowards(from, to, 1);
    }

    public static Location moveTowards(Location from, Location to, double steps) {
        Vector direction = to.clone().subtract(from).toVector().normalize().multiply(steps);
        return from.clone().add(direction);
    }

    public static Set<Location> makeHollow(Set<Location> locations, boolean sphere) {
        Set<Location> edge = new HashSet<>();

        if (sphere) {
            for (Location location : locations) {
                for (BlockFace face : BlockFace.values()) {
                    if (face.getModX() == 0 && face.getModZ() == 0) {
                        continue;
                    }

                    Block block = location.getBlock().getRelative(face);
                    if (block.isEmpty()) {
                        edge.add(block.getLocation());
                    }
                }
            }
        } else {
            for (Location location : locations) {
                for (BlockFace face : BlockFace.values()) {
                    Block block = location.getBlock().getRelative(face);
                    if (block.isEmpty()) {
                        edge.add(block.getLocation());
                    }
                }
            }
        }
        return edge;
    }

    public static boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static boolean isSameBlock(Location loc1, Block block) {
        return loc1.getBlockX() == block.getX() && loc1.getBlockY() == block.getY() && loc1.getBlockZ() == block.getZ();
    }

    public static boolean isSameBlock(Block block1, Block block2) {
        return block1.getX() == block2.getX() && block1.getY() == block2.getY() && block1.getZ() == block2.getZ();
    }

    public static boolean isBlockSameType(Location loc1, Location loc2) {
        return loc1.getBlock().getType() == loc2.getBlock().getType();
    }

    public static boolean isBlockSameType(Location loc1, Material material) {
        return loc1.getBlock().getType() == material;
    }

    public static boolean isForBlockSelection(Material material) {
        if (material.isBlock() && material != Material.AIR) {
            try {
                if (material.isInteractable()) {
                    return false;
                }
            } catch (Throwable var3) {
            }

            try {
                if (material.hasGravity()) {
                    return false;
                }
            } catch (Throwable var2) {
            }

            return material.isSolid();
        } else {
            return false;
        }
    }

    public static FallingBlock shootBlock(Block block, Vector velocity) {
        return shootBlock(block, velocity, (double)0.0F);
    }

    public static FallingBlock shootBlock(Block block, Vector velocity, double burnOnFallChance) {
        if (!canShootBlock(block)) {
            return null;
        } else {
            FallingBlock falling = EntityUtils.spawnFallingBlock(block.getLocation().clone().add((double)0.5F, (double)0.0F, (double)0.5F), block.getType());
            double x = MathsUtils.range(velocity.getX(), (double)-2.0F, (double)2.0F) * (double)0.5F;
            double y = Math.random();
            double z = MathsUtils.range(velocity.getZ(), (double)-2.0F, (double)2.0F) * (double)0.5F;
            falling.setVelocity(new Vector(x, y, z));
            if (Common.chanceDouble(burnOnFallChance) && block.getType().isBurnable()) {
                scheduleBurnOnFall(falling);
            }

            falling.setDropItem(false);
            block.setType(Material.AIR);
            return falling;
        }
    }

    public static FallingBlock spawnFallingBlock(Block block, Vector velocity) {
        FallingBlock falling = EntityUtils.spawnFallingBlock(block.getLocation().clone().add((double)0.5F, (double)0.0F, (double)0.5F), block.getType());
        falling.setVelocity(velocity);
        falling.setDropItem(false);
        block.setType(Material.AIR);
        return falling;
    }

    private static boolean canShootBlock(Block block) {
        Material material = block.getType();
        return !material.isAir() && (material.toString().contains("STEP") || material.toString().contains("SLAB") || isForBlockSelection(material));
    }

    private static void scheduleBurnOnFall(FallingBlock block) {
        EntityUtils.trackFalling(block, () -> {
            Block upperBlock = block.getLocation().getBlock().getRelative(BlockFace.UP);
            if (upperBlock.getType() == Material.AIR) {
                upperBlock.setType(Material.FIRE);
            }

        });
    }

    public static String[] locationToString(Location location) {
        return new String[]{"World: " + location.getWorld().getName(), "X: " + location.getBlockX(), "Y: " + location.getBlockY(), "Z: " + location.getBlockZ()};
    }

}
