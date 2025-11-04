package me.solar.apolloLibrary.config;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigSerializer<T> {

    Object serialize(T object);

    T deserialize(ConfigurationSection section);
}
