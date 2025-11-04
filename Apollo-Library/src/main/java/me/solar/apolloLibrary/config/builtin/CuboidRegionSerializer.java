package me.solar.apolloLibrary.config.builtin;

import me.solar.apolloLibrary.config.ConfigSerializer;
import me.solar.apolloLibrary.config.StaticConfigHandler;
import me.solar.apolloLibrary.world.CuboidRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Map;

public class CuboidRegionSerializer implements ConfigSerializer<CuboidRegion> {

    @Override
    public Object serialize(CuboidRegion object) {
        ConfigurationSection section = new MemoryConfiguration();
        try {
            StaticConfigHandler.serializeClassToMap(object, section);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return section;
    }

    @Override
    public CuboidRegion deserialize(ConfigurationSection section) {
        if (!section.contains("point-a") || !section.contains("point-b")) {
            return null;
        }

        CuboidRegion region = new CuboidRegion();
        try {
            StaticConfigHandler.deserializeClassFromMap(region, section);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return region;
    }

}
