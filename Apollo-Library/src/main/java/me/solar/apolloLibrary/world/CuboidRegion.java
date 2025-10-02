package me.solar.apolloLibrary.world;

import org.bukkit.Location;

/**
 * Represents a cuboid region in 3D space.
 */
public class CuboidRegion {

    /** The minimum X coordinate of the region. */
    private double minX;
    /** The minimum Y coordinate of the region. */
    private double minY;
    /** The minimum Z coordinate of the region. */
    private double minZ;
    /** The maximum X coordinate of the region. */
    private double maxX;
    /** The maximum Y coordinate of the region. */
    private double maxY;
    /** The maximum Z coordinate of the region. */
    private double maxZ;

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
    }

}
