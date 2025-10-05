package me.solar.apolloLibrary.world;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
public class CuboidRegion implements Region {

    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;
    private final World world;
    private final String id;
    private final Map<String, Object> metadata = new HashMap<>();

    // Constructors
    public CuboidRegion(World world, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.world = world;
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
        this.id = UUID.randomUUID().toString(); // Could be made more configurable
    }

    public CuboidRegion(Location corner1, Location corner2) {
        this(
                corner1.getWorld(),
                corner1.getX(), corner1.getY(), corner1.getZ(),
                corner2.getX(), corner2.getY(), corner2.getZ()
        );
    }

    // Interface methods
    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.of(
                new Location(world, minX, minY, minZ),
                new Location(world, maxX, maxY, maxZ)
        );
    }

    @Override
    public boolean contains(Location loc) {
        if (loc == null || loc.getWorld() == null || !loc.getWorld().equals(world)) return false;
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    @Override
    public boolean intersects(Region other) {
        if (!(other.getWorld().equals(this.world))) return false;
        BoundingBox otherBox = other.getBoundingBox();
        return this.getBoundingBox().overlaps(otherBox);
    }

    @Override
    public List<Location> getPoints() {
        // Returns all 8 corners of the cuboid
        return List.of(
                new Location(world, minX, minY, minZ),
                new Location(world, minX, minY, maxZ),
                new Location(world, minX, maxY, minZ),
                new Location(world, minX, maxY, maxZ),
                new Location(world, maxX, minY, minZ),
                new Location(world, maxX, minY, maxZ),
                new Location(world, maxX, maxY, minZ),
                new Location(world, maxX, maxY, maxZ)
        );
    }

    @Override
    public double getArea() {
        // Surface area of all 6 faces
        double lx = maxX - minX, ly = maxY - minY, lz = maxZ - minZ;
        return 2 * (lx * ly + lx * lz + ly * lz);
    }

    @Override
    public double getVolume() {
        double lx = maxX - minX, ly = maxY - minY, lz = maxZ - minZ;
        return lx * ly * lz;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CuboidRegion that)) return false;
        return Double.compare(that.minX, minX) == 0 &&
                Double.compare(that.minY, minY) == 0 &&
                Double.compare(that.minZ, minZ) == 0 &&
                Double.compare(that.maxX, maxX) == 0 &&
                Double.compare(that.maxY, maxY) == 0 &&
                Double.compare(that.maxZ, maxZ) == 0 &&
                Objects.equals(world, that.world) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, minZ, maxX, maxY, maxZ, world, id);
    }

    @Override
    public String toString() {
        return "CuboidRegion{" +
                "world=" + world.getName() +
                ", min=(" + minX + ',' + minY + ',' + minZ + ')' +
                ", max=(" + maxX + ',' + maxY + ',' + maxZ + ')' +
                ", id=" + id +
                '}';
    }
}