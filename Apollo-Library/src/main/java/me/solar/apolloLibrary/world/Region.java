package me.solar.apolloLibrary.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Map;

public interface Region {

    /**
     * Get the unique identifier for this region.
     *
     * @return The unique identifier.
     */
    String getIdentifier();

    /**
     * Get the world this region is in.
     *
     * @return The world.
     */
    World getWorld();

    /**
     * Get the bounding box of this region.
     *
     * @return The bounding box.
     */
    BoundingBox getBoundingBox();

    /**
     * Check if a location is within this region.
     *
     * @param location The location to check.
     * @return true if the location is within this region, false otherwise
     */
    boolean contains(Location location);

    /**
     * Check if this region intersects another region.
     *
     * @param region The region to check.
     * @return true if the regions intersect, false otherwise
     */
    boolean intersects(Region region);

    /**
     * Get a list of all points in this region. Used to draw advanced shapes.
     *
     * @return A list of all points in this region.
     */
    List<Location> getPoints();

    /**
     * Get the volume of this region.
     *
     * @return The volume.
     */
    double getVolume();

    /**
     * Get the area of this region (2D projection).
     *
     * @return The area.
     */
    double getArea();

    /**
     * Get the height of this region (Y axis).
     *
     * @return The height.
     */
    double getHeight();

    /**
     * Get the width of this region (X axis).
     *
     * @return The width.
     */
    double getWidth();

    /**
     * Get the length of this region (Z axis).
     *
     * @return The length.
     */
    double getLength();

    /**
     * Get the center of this region.
     * @return The center location.
     */
    Location getCenter();

    /**
     * Get the metadata or flags for this region.
     *
     * @return The metadata.
     */
    Map<String, Object> getMetadata();

    /**
     * Get the edge locations of this region.
     *
     * @return A list of edge locations.
     */
    List<Location> getEdges();

    /**
     * Get all locations within this region including inside
     *
     * @return A list of all locations within this region.
     */
    List<Location> getAllLocations();

    void visualize();

    void unvisualize();

    boolean isVisualized();

}
