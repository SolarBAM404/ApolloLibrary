 package me.solar.apolloLibrary.config.builtin;

import me.solar.apolloLibrary.config.ConfigSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationSerializer implements ConfigSerializer<Location> {

    @Override
    public Object serialize(Location loc) {
        if (loc == null) return null;
        ConfigurationSection section = new MemoryConfiguration();
        section.set("world", loc.getWorld() == null ? Bukkit.getWorlds().getFirst() : loc.getWorld().getName());
        section.set("x", loc.getX());
        section.set("y", loc.getY());
        section.set("z", loc.getZ());
        section.set("yaw", loc.getYaw());
        section.set("pitch", loc.getPitch());
        return section;
    }

    @Override
    public Location deserialize(ConfigurationSection section) {
        if (section == null) return null;
        String worldName = section.getString("world", null);
        double x = section.getDouble("x", 0.0);
        double y = section.getDouble("y", 0.0);
        double z = section.getDouble("z", 0.0);
        float yaw = (float) section.getDouble("yaw", 0.0);
        float pitch = (float) section.getDouble("pitch", 0.0);
        return new Location(worldName == null ? null : Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}