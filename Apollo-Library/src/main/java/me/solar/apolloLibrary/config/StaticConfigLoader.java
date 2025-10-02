package me.solar.apolloLibrary.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class for loading and saving static config classes.
 */
public class StaticConfigLoader {

    /**
     * Saves the static fields of the given config class to the specified path.
     * The config class must be annotated with {@link ConfigFormat} and its fields with {@link ConfigKey}.
     *
     * @param configClass the config class
     * @param path        the path to save to
     * @throws IOException            if an I/O error occurs
     * @throws IllegalAccessException if field access fails
     */
    public static void saveConfig(@NotNull Class<?> configClass, Path path) throws IOException, IllegalAccessException {
        ConfigFormat format = configClass.getAnnotation(ConfigFormat.class);
        if (format == null) throw new RuntimeException("No config format specified!");

        if (format.value() == FormatType.YAML) {
            YamlConfiguration configuration = new YamlConfiguration();

            serializeYamlClass(configClass, configuration);
            configuration.save(path.toFile());

        } else {
            throw new RuntimeException("Not yet implemented!");
        }
    }

    /**
     * Loads the static fields of the given config class from the specified path.
     * The config class must be annotated with {@link ConfigFormat} and its fields with {@link ConfigKey}.
     *
     * @param configClass the config class
     * @param path        the path to load from
     * @throws IOException            if an I/O error occurs
     * @throws IllegalAccessException if field access fails
     */
    public static void loadConfig(@NotNull Class<?> configClass, Path path) throws IOException, IllegalAccessException {
        ConfigFormat format = configClass.getAnnotation(ConfigFormat.class);
        if (format == null) throw new RuntimeException("No config format specified!");

        if (format.value() == FormatType.YAML) {

            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(path.toFile());
            deserializeYamlClass(configClass, configuration);
        } else {
            throw new RuntimeException("Not yet implemented!");
        }
    }

    private static void serializeYamlClass(@NotNull Class<?> clazz, ConfigurationSection configuration) {

        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);

            ConfigKey key = field.getAnnotation(ConfigKey.class);
            if (key == null) continue;

            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (isConfigClass(field.getType())) {
                ConfigurationSection childConfig = configuration.createSection(key.value());
                serializeYamlClass(field.getType(), childConfig);
            } else {
                configuration.set(key.value(), value);
            }

            if (key.comment().length > 0) {
                configuration.setComments(key.value(), List.of(key.comment()));
            }
        }

        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            if (!Modifier.isStatic(innerClass.getModifiers())) continue;

            ConfigKey key = innerClass.getAnnotation(ConfigKey.class);
            if (key == null) continue;

            ConfigurationSection innerConfig = configuration.createSection(key.value());
            serializeYamlClass(innerClass, innerConfig);

            if (key.comment().length > 0) {
                configuration.setComments(key.value(), List.of(key.comment()));
            }
        }

    }

    private static void deserializeYamlClass(@NotNull Class<?> clazz, ConfigurationSection configuration) throws IllegalAccessException {

        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);

            ConfigKey key = field.getAnnotation(ConfigKey.class);

            if (key == null) continue;

            Object value = configuration.get(key.value());

            if (value == null) continue;

            if (isConfigClass(field.getType())) {
                deserializeYamlClass(field.getType(), configuration.getConfigurationSection(key.value()));
            } else {
                field.set(null, value);
            }

        }

    }

    private static boolean isConfigClass(Class<?> type) {
        return type.isAnnotationPresent(ConfigFormat.class);
    }
}