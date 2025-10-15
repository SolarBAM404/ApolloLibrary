package me.solar.apolloLibrary.world;

import lombok.Getter;
import lombok.Setter;
import me.solar.apolloLibrary.runnables.RunnableObject;
import me.solar.apolloLibrary.utils.MathsUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.*;

@Getter
public class CuboidRegion implements Region {

    @Setter
    private Location pointA, pointB;

    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;
    private final World world;
    private final String id;
    private final Map<String, Object> metadata = new HashMap<>();

    private RunnableObject runnableObject;
    private List<Player> viewers = new ArrayList<>();

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
    public double getHeight() {
        return MathsUtils.max(1, maxY - minY);
    }

    @Override
    public double getWidth() {
        return MathsUtils.max(1, maxX - minX);
    }

    @Override
    public double getLength() {
        return MathsUtils.max(1, maxZ - minZ);
    }

    @Override
    public Location getCenter() {
        return new Location(world, (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public List<Location> getEdges() {
        List<Location> edges = List.of();

        Location min = new Location(world, minX, minY, minZ);
        Location max = new Location(world, maxX, maxY, maxZ);

        addEdge(edges, (int)minX, (int)maxX, CoordinateType.X, minY, minZ); // Bottom face, minZ
        addEdge(edges, (int)minX, (int)maxX, CoordinateType.X, minY, maxZ); // Bottom face, maxZ
        addEdge(edges, (int)minX, (int)maxX, CoordinateType.X, maxY, minZ); // Top face, minZ
        addEdge(edges, (int)minX, (int)maxX, CoordinateType.X, maxY, maxZ); // Top face, maxZ

        addEdge(edges, (int)minX, (int)maxX, CoordinateType.Z, minY, minX); // Front face, minX
        addEdge(edges, (int)minX, (int)maxX, CoordinateType.Z, minY, maxX); // Front face, maxX
        addEdge(edges, (int)minX, (int)maxX, CoordinateType.Z, maxY, minX); // Back face, minX
        addEdge(edges, (int)minX, (int)maxX, CoordinateType.Z, maxY, maxX); // Back face, maxX

        addEdge(edges, (int)minY, (int)maxY, CoordinateType.Y, minX, minZ); // Left face, minZ
        addEdge(edges, (int)minY, (int)maxY, CoordinateType.Y, minX, maxZ); // Left face, maxZ
        addEdge(edges, (int)minY, (int)maxY, CoordinateType.Y, maxX, minZ); // Right face, minZ
        addEdge(edges, (int)minY, (int)maxY, CoordinateType.Y, maxX, maxZ); // Right face, maxZ

        return edges;
    }

    private void addEdge(List<Location> edges, int loopMin, int loopMax, CoordinateType type, double valueA, double valueB) {

        switch (type) {
            case X -> {
                for (double x = loopMin; x <= loopMax; x++) {
                    edges.add(new Location(world, x, valueA, valueB));
                }
            }
            case Y -> {
                for (double y = loopMin; y <= loopMax; y++) {
                    edges.add(new Location(world, valueA, y, valueB));
                }
            }
            case Z -> {
                for (double z = loopMin; z <= loopMax; z++) {
                    edges.add(new Location(world, valueA, valueB, z));
                }
            }
            default -> {
                throw new IllegalArgumentException("Invalid coordinate type: " + type);
            }
        }


    }

    /**
     * Retrieves all locations within the bounds of this cuboid region.
     * This includes every possible coordinate within the defined
     * minimum and maximum bounds for X, Y, and Z dimensions in the specified world.
     * <p>
     * Note: This method can be resource-intensive for large regions due to the potentially large number of locations.
     * It is recommended to use {@link #getPoints()} instead.
     * Or to asynchronously run this method in a separate thread.
     *
     * @return A list of all {@code Location} objects within the region.
     */
    @Override
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }

    @Override
    public void visualize() {
        if (isVisualized()) return;

        runnableObject = RunnableObject.of(() -> {
            for (Location loc : getPoints()) {
                Particle.FLAME.builder().count(1).color(Color.AQUA)
                        .location(loc)
                        .receivers(viewers);
            }
        });
        runnableObject.runTaskTimer(0, 5);
    }

    @Override
    public void unvisualize() {
        runnableObject.cancel();
    }

    @Override
    public boolean isVisualized() {
        return false;
    }

    public void addViewer(Player player) {
        viewers.add(player);
    }

    public void removeViewer(Player player) {
        viewers.remove(player);
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