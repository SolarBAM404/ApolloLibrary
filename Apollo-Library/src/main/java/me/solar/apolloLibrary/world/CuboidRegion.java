package me.solar.apolloLibrary.world;

import lombok.Getter;
import me.solar.apolloLibrary.config.ConfigFormat;
import me.solar.apolloLibrary.config.ConfigKey;
import me.solar.apolloLibrary.config.FormatType;
import me.solar.apolloLibrary.runnables.RunnableObject;
import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.utils.MathsUtils;
import me.solar.apolloLibrary.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

@Getter
public class CuboidRegion implements Region {

    @ConfigKey("point-a")
    private Location pointA;
    @ConfigKey("point-b")
    private Location pointB;

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;
    private World world;
    private String id;
    private final Map<String, Object> metadata = new HashMap<>();

    private final List<Player> viewers = new ArrayList<>();
    private final Visualizer runnableObject = new Visualizer();
    private boolean isVisualized = false;

    public CuboidRegion() {
    }

    // Constructors
    public CuboidRegion(World world, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this(new Location(world, minX, minY, minZ), new Location(world, maxX, maxY, maxZ));
    }

    public CuboidRegion(Location corner1, Location corner2) {
        pointA = corner1;
        pointB = corner2;
        world = corner1.getWorld();
        id = UUID.randomUUID().toString();
        setMinMax();
    }

    // Interface methods
    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public World getWorld() {
        if (world == null) {
            if (pointA.getWorld() != null) {
                world = pointA.getWorld();
            }

        }
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

    public Location getPointA() {
        if (pointA.getWorld() == null) {
            if (world != null) {
                pointA.setWorld(world);
            } else {
                pointA.setWorld(Bukkit.getWorlds().getFirst());
            }
        }
        return pointA;
    }

    public Location getPointB() {
        if (pointB.getWorld() == null) {
            if (world != null) {
                pointB.setWorld(world);
            } else {
                pointB.setWorld(Bukkit.getWorlds().getFirst());
            }
        }
        return pointB;
    }

    public void setPointA(Location pointA) {
        this.pointA = pointA;
        setMinMax();
    }

    public void setPointB(Location pointB) {
        this.pointB = pointB;
        setMinMax();
    }

    private void setMinMax() {
        if (pointA == null || pointB == null) return;
        setMinMax(pointA, pointB);
    }

    private void setMinMax(Location pointA, Location pointB) {
        minX = Math.min(pointA.getX(), pointB.getX());
        minY = Math.min(pointA.getY(), pointB.getY());
        minZ = Math.min(pointA.getZ(), pointB.getZ());
        maxX = Math.max(pointA.getX(), pointB.getX());
        maxY = Math.max(pointA.getY(), pointB.getY());
        maxZ = Math.max(pointA.getZ(), pointB.getZ());
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

    public Location getMin() {
        setMinMax();
        return new Location(world, minX, minY, minZ);
    }

    public Location getMax() {
        setMinMax();
        return new Location(world, maxX, maxY, maxZ);
    }

    @Override
    public List<Location> getEdges() {
        Set<Location> boundingBox = WorldUtils.getBoundingBox(getMin(), getMax());
        return boundingBox.stream().toList();
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

        runnableObject.changeViewers(viewers);
        runnableObject.changeEdges(getEdges());
        runnableObject.runTaskTimer(0, 1);
        isVisualized(true);
    }

    @Override
    public void unvisualize() {
        viewers.clear();
        runnableObject.cancel();
        isVisualized(false);
    }

    @Override
    public boolean isVisualized() {
        return isVisualized;
    }

    public void isVisualized(boolean isVisualized) {
        this.isVisualized = isVisualized;
    }

    public void addViewer(Player player) {
        if (viewers.contains(player)) return;
        viewers.add(player);

        if (!isVisualized()) {
            visualize();
        }

    }

    public void removeViewer(Player player) {
        viewers.remove(player);

        if (viewers.isEmpty()) {
            unvisualize();
        }
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

    public class Visualizer extends RunnableObject {

        private final List<Player> viewers = new ArrayList<>();
        private final List<Location> edges = new ArrayList<>();

        public void changeViewers(List<Player> viewers) {
            this.viewers.clear();
            this.viewers.addAll(viewers);
        }

        public void changeEdges(List<Location> locations) {
            this.edges.clear();
            this.edges.addAll(locations);
        }

        @Override
        public void run() {
            for (Location loc : getEdges()) {
                Particle.DUST.builder().count(1).color(Color.AQUA)
                        .location(WorldUtils.getBlockCenter(loc))
                        .receivers(this.viewers)
                        .spawn();
            }
        }
    }
}