package me.solar.apolloLibrary.world;

import org.bukkit.Location;

public class CuboidRegion {

    private double minX, minY, minZ, maxX, maxY, maxZ;

    public CuboidRegion(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public CuboidRegion(CuboidRegion region) {
        this.minX = region.minX;
        this.minY = region.minY;
        this.minZ = region.minZ;
        this.maxX = region.maxX;
        this.maxY = region.maxY;
        this.maxZ = region.maxZ;
    }

    public CuboidRegion(Location location, double radius) {
        this(location.getX() - radius, location.getY() - radius, location.getZ() - radius,
                location.getX() + radius, location.getY() + radius, location.getZ() + radius);
    }

    public CuboidRegion(Location location1, Location location2) {
        this(Math.min(location1.getX(), location2.getX()), Math.min(location1.getY(), location2.getY()),
                Math.min(location1.getZ(), location2.getZ()), Math.max(location1.getX(), location2.getX()),
                Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
    }

}
