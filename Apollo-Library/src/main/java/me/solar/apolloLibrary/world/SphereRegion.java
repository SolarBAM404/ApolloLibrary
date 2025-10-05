package me.solar.apolloLibrary.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class SphereRegion implements Region {
    private final String id;
    private final Location center;
    private final double radius;
    private final Map<String, Object> metadata = new HashMap<>();

    public SphereRegion(Location center, double radius) {
        this.center = center.clone();
        this.radius = radius;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public World getWorld() {
        return center.getWorld();
    }

    @Override
    public BoundingBox getBoundingBox() {
        // The smallest cuboid containing the sphere
        double minX = center.getX() - radius;
        double maxX = center.getX() + radius;
        double minY = center.getY() - radius;
        double maxY = center.getY() + radius;
        double minZ = center.getZ() - radius;
        double maxZ = center.getZ() + radius;
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean contains(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        if (!loc.getWorld().equals(center.getWorld())) return false;
        double dx = loc.getX() - center.getX();
        double dy = loc.getY() - center.getY();
        double dz = loc.getZ() - center.getZ();
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    @Override
    public boolean intersects(Region other) {
        if (other == null || !getWorld().equals(other.getWorld())) return false;
        // Use bounding-box overlap as a fast path
        if (!this.getBoundingBox().overlaps(other.getBoundingBox())) return false;

        if (other instanceof SphereRegion o) {
            double d = center.distance(o.center);
            return d <= (this.radius + o.radius);
        } else if (other instanceof CuboidRegion) {
            // Check shortest distance from center to cuboid
            BoundingBox box = other.getBoundingBox();
            double cx = center.getX(), cy = center.getY(), cz = center.getZ();
            double closestX = Math.max(box.getMinX(), Math.min(cx, box.getMaxX()));
            double closestY = Math.max(box.getMinY(), Math.min(cy, box.getMaxY()));
            double closestZ = Math.max(box.getMinZ(), Math.min(cz, box.getMaxZ()));
            double dx = cx - closestX;
            double dy = cy - closestY;
            double dz = cz - closestZ;
            return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
        } else {
            // Fallback: bounding box
            return true;
        }
    }

    @Override
    public List<Location> getPoints() {
        // A sphere is defined by center & radius, but for tool support, return axis-aligned cardinal points
        return List.of(
                new Location(center.getWorld(), center.getX() + radius, center.getY(), center.getZ()),
                new Location(center.getWorld(), center.getX() - radius, center.getY(), center.getZ()),
                new Location(center.getWorld(), center.getX(), center.getY() + radius, center.getZ()),
                new Location(center.getWorld(), center.getX(), center.getY() - radius, center.getZ()),
                new Location(center.getWorld(), center.getX(), center.getY(), center.getZ() + radius),
                new Location(center.getWorld(), center.getX(), center.getY(), center.getZ() - radius)
        );
    }

    @Override
    public double getArea() {
        // Surface area of sphere
        return 4 * Math.PI * radius * radius;
    }

    @Override
    public double getVolume() {
        return (4.0 / 3.0) * Math.PI * radius * radius * radius;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SphereRegion o)) return false;
        return center.equals(o.center) && radius == o.radius && id.equals(o.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, radius, id);
    }

    @Override
    public String toString() {
        return "SphereRegion{center=" + center + ", radius=" + radius + ", id=" + id + "}";
    }
}
