package me.solar.apolloLibrary.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

public final class SerializerRegistry {
    private static final Map<Class<?>, ConfigSerializer<?>> MAP = new ConcurrentHashMap<>();

    private SerializerRegistry() {}

    public static <T> void register(Class<T> type, ConfigSerializer<T> serializer) {
        MAP.put(type, serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> ConfigSerializer<T> get(Class<T> type) {
        // direct hit
        ConfigSerializer<?> s = MAP.get(type);
        if (s != null) return (ConfigSerializer<T>) s;

        // fallback: find first serializer for a supertype or interface
        for (Map.Entry<Class<?>, ConfigSerializer<?>> e : MAP.entrySet()) {
            if (e.getKey().isAssignableFrom(type)) {
                return (ConfigSerializer<T>) e.getValue();
            }
        }
        return null;
    }

    public static Optional<ConfigSerializer<?>> getOptional(Class<?> type) {
        return Optional.ofNullable(get(type));
    }
}
