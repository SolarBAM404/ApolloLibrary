package me.solar.apolloLibrary.world;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a cuboid region in 3D space.
 */
@Getter
public class CuboidRegion {

    /** The minimum X coordinate of the region. */
    private final double minX;
    /** The minimum Y coordinate of the region. */
    private final double minY;
    /** The minimum Z coordinate of the region. */
    private final double minZ;
    /** The maximum X coordinate of the region. */
    private final double maxX;
    /** The maximum Y coordinate of the region. */
    private final double maxY;
    /** The maximum Z coordinate of the region. */
    private final double maxZ;

    private World world;

    /**
     * Constructs a CuboidRegion with specified min and max coordinates.
     *
     * @param minX minimum X
     * @param minY minimum Y
     * @param minZ minimum Z
     * @param maxX maximum X
     * @param maxY maximum Y
     * @param maxZ maximum Z
     */
    public CuboidRegion(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.world = null;
    }

    /**
     * Copy constructor.
     *
     * @param region the region to copy
     */
    public CuboidRegion(CuboidRegion region) {
        this.minX = region.minX;
        this.minY = region.minY;
        this.minZ = region.minZ;
        this.maxX = region.maxX;
        this.maxY = region.maxY;
        this.maxZ = region.maxZ;
        this.world = region.world;
    }

    /**
     * Constructs a CuboidRegion centered at a location with a given radius.
     *
     * @param location the center location
     * @param radius the radius
     */
    public CuboidRegion(Location location, double radius) {
        this(location.getX() - radius, location.getY() - radius, location.getZ() - radius,
                location.getX() + radius, location.getY() + radius, location.getZ() + radius);
        this.world = location.getWorld();
    }

    /**
     * Constructs a CuboidRegion from two locations.
     *
     * @param location1 the first location
     * @param location2 the second location
     */
    public CuboidRegion(Location location1, Location location2) {
        this(Math.min(location1.getX(), location2.getX()), Math.min(location1.getY(), location2.getY()),
                Math.min(location1.getZ(), location2.getZ()), Math.max(location1.getX(), location2.getX()),
                Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
        this.world = location1.getWorld();
    }

    public boolean contains(Location location) {
        return location.getX() >= minX && location.getX() <= maxX && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    public boolean contains(CuboidRegion region) {
        return region.minX >= minX && region.maxX <= maxX && region.minY >= minY && region.maxY <= maxY && region.minZ >= minZ && region.maxZ <= maxZ;
    }

    public boolean intersects(CuboidRegion region) {
        return region.maxX >= minX && region.minX <= maxX && region.maxY >= minY && region.minY <= maxY && region.maxZ >= minZ && region.minZ <= maxZ;
    }

    public double getVolume() {
        return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
    }

    public double getArea() {
        return (maxX - minX) * (maxZ - minZ);
    }

    public Location getMinimumPoint() {
        return new Location(world, minX, minY, minZ);
    }

    public Location getMaximumPoint() {
        return new Location(world, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return "CuboidRegion [minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY
                + ", maxZ=" + maxZ + "]";
    }

}

