package me.solar.apolloLibrary.config;

import me.solar.apolloLibrary.config.builtin.CuboidRegionSerializer;
import me.solar.apolloLibrary.config.builtin.LocationSerializer;
import me.solar.apolloLibrary.world.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Cleaner implementation for saving/loading static config classes.
 * Writes plain maps/lists/primitives with SnakeYAML to avoid Java/Bukkit tags.
 * Uses YamlConfiguration only as an in-memory adapter for ConfigSerializer.deserialize(...).
 */
public final class StaticConfigHandler {

    static {
        // keep built-in serializer registrations here
        SerializerRegistry.register(Location.class, new LocationSerializer());
        SerializerRegistry.register(CuboidRegion.class, new CuboidRegionSerializer());
    }

//    private StaticConfigHandler() {
//    }
//
//    public static void saveConfig(@NotNull Class<?> configClass, @NotNull Path target) throws IOException, IllegalAccessException {
//        ConfigFormat fmt = configClass.getAnnotation(ConfigFormat.class);
//        if (fmt == null) throw new IllegalStateException("Missing @ConfigFormat on " + configClass.getName());
//        if (fmt.value() != FormatType.YAML) throw new UnsupportedOperationException("Only YAML is supported");
//
//        Map<String, Object> root = serializeClassToMap(null, configClass);
//
//        DumperOptions opts = new DumperOptions();
//        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        opts.setPrettyFlow(true);
//        opts.setIndent(2);
//        Yaml yaml = new Yaml(opts);
//
//        String dumped = yaml.dump(root == null ? Collections.emptyMap() : root);
//        Files.writeString(target, dumped);
//    }
//
//    public static void loadConfig(@NotNull Class<?> configClass, @NotNull Path source) throws IOException, IllegalAccessException {
//        ConfigFormat fmt = configClass.getAnnotation(ConfigFormat.class);
//        if (fmt == null) throw new IllegalStateException("Missing @ConfigFormat on " + configClass.getName());
//        if (fmt.value() != FormatType.YAML) throw new UnsupportedOperationException("Only YAML is supported");
//
//        Yaml yaml = new Yaml();
//        Object loaded = yaml.load(Files.newInputStream(source));
//        @SuppressWarnings("unchecked") Map<String, Object> root = (loaded instanceof Map) ? (Map<String, Object>) loaded : new LinkedHashMap<>();
//        deserializeClassFromMap(null, configClass, root);
//    }
//
//    // -----------------------
//    // Serialization (to plain Map/List/primitive)
//    // -----------------------
//    public static Map<String, Object> serializeClassToMap(@NotNull Object instance) throws IllegalAccessException {
//        Class<?> clazz = instance.getClass();
//        return serializeClassToMap(instance, clazz);
//    }
//
//    private static Map<String, Object> serializeClassToMap(Object instance, Class<?> clazz) throws IllegalAccessException {
//        Map<String, Object> out = new LinkedHashMap<>();
//
//        for (Field field : clazz.getDeclaredFields()) {
//            boolean isStatic = Modifier.isStatic(field.getModifiers());
//            if (instance == null && !isStatic) continue;
//            if (instance != null && isStatic) continue;
//
//            field.setAccessible(true);
//            ConfigKey key = field.getAnnotation(ConfigKey.class);
//            String name = (key != null) ? key.value() : field.getName();
//
//            Object raw = field.get(instance == null ? null : instance);
//            if (raw == null) {
//                out.put(name, null);
//                continue;
//            }
//
//            // List handling
//            if (raw instanceof List<?>) {
//                out.put(name, serializeListField((List<?>) raw, field));
//                continue;
//            }
//
//            // nested config class
//            if (isConfigClass(field.getType())) {
//                Map<String, Object> child = serializeClassToMap(raw, field.getType());
//                out.put(name, child);
//                continue;
//            }
//
//            // serializer-backed
//            @SuppressWarnings("unchecked") ConfigSerializer<Object> serializer = (ConfigSerializer<Object>) SerializerRegistry.get(field.getType());
//            if (serializer != null) {
//                Object serOut = serializer.serialize(raw);
//                System.out.println(serOut);
//                out.put(name, normalizeForYaml(serOut));
//                continue;
//            }
//
//            // fallback (primitive, string, maps, lists)
//            out.put(name, normalizeForYaml(raw));
//        }
//
//        // also include static nested classes declared inside this config class
//        for (Class<?> inner : clazz.getDeclaredClasses()) {
//            if (!Modifier.isStatic(inner.getModifiers())) continue;
//            ConfigKey k = inner.getAnnotation(ConfigKey.class);
//            String innerName = (k != null) ? k.value() : inner.getSimpleName();
//            out.put(innerName, serializeClassToMap(null, inner));
//        }
//
//        return out;
//    }
//
//    private static List<Object> serializeListField(List<?> list, Field field) throws IllegalAccessException {
//        List<Object> result = new ArrayList<>();
//        Class<?> elementType = extractListElementType(field);
//
//        for (Object elem : list) {
//            if (elem == null) continue;
//
//            Class<?> runtimeElemClass = (elementType != null) ? elementType : elem.getClass();
//
//            if (isConfigClass(runtimeElemClass)) {
//                // serialize config instance or static class
//                Map<String, Object> nested = serializeClassToMap(elem, runtimeElemClass);
//                result.add(nested);
//            } else {
//                // serializer-backed or primitive
//                result.add(normalizeForYaml(elem));
//            }
//        }
//        return result;
//    }
//
//    // -----------------------
//    // Deserialization (from plain Map/List/primitive)
//    // -----------------------
//    private static void deserializeClassFromMap(Object instance, Class<?> clazz, Map<String, Object> map) throws IllegalAccessException {
//        for (Field field : clazz.getDeclaredFields()) {
//            boolean isStatic = Modifier.isStatic(field.getModifiers());
//            if (instance == null && !isStatic) continue;
//            if (instance != null && isStatic) continue;
//
//            field.setAccessible(true);
//            ConfigKey key = field.getAnnotation(ConfigKey.class);
//            if (key == null) continue;
//
//            Object raw = map.get(key.value());
//            if (raw == null) continue;
//
//            // List
//            if (raw instanceof List<?>) {
//                List<?> rawList = (List<?>) raw;
//                List<Object> outList = deserializeListField(rawList, field);
//                field.set(instance == null ? null : instance, outList);
//                continue;
//            }
//
//            // nested config class
//            if (isConfigClass(field.getType())) {
//                if (!(raw instanceof Map)) continue;
//                @SuppressWarnings("unchecked") Map<String, Object> childMap = (Map<String, Object>) raw;
//
//                if (Modifier.isStatic(field.getModifiers())) {
//                    deserializeClassFromMap(null, field.getType(), childMap);
//                } else {
//                    try {
//                        Object nested = field.get(instance);
//                        if (nested == null) {
//                            Constructor<?> ctor = field.getType().getDeclaredConstructor();
//                            ctor.setAccessible(true);
//                            nested = ctor.newInstance();
//                            field.set(instance, nested);
//                        }
//                        deserializeClassFromMap(nested, field.getType(), childMap);
//                    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException ignored) {
//                        // fallback: populate static fields of field.getType()
//                        deserializeClassFromMap(null, field.getType(), childMap);
//                    }
//                }
//                continue;
//            }
//
//            // serializer-aware
//            ConfigSerializer<?> serializer = SerializerRegistry.get(field.getType());
//            if (serializer != null) {
//                Object des = deserializeUsingSerializer(raw, serializer);
//                field.set(Modifier.isStatic(field.getModifiers()) ? null : instance, des);
//                continue;
//            }
//
//            // simple set
//            field.set(Modifier.isStatic(field.getModifiers()) ? null : instance, raw);
//        }
//    }
//
//    private static List<Object> deserializeListField(List<?> rawList, Field field) {
//        List<Object> out = new ArrayList<>();
//        Class<?> elementType = extractListElementType(field);
//
//        for (Object elem : rawList) {
//            if (elem == null) continue;
//
//            if (elem instanceof Map<?, ?>) {
//                @SuppressWarnings("unchecked") Map<String, Object> mapElem = (Map<String, Object>) elem;
//
//                // config class elements
//                if (elementType != null && isConfigClass(elementType)) {
//                    try {
//                        Constructor<?> ctor = elementType.getDeclaredConstructor();
//                        ctor.setAccessible(true);
//                        Object inst = ctor.newInstance();
//                        deserializeClassFromMap(inst, elementType, mapElem);
//                        out.add(inst);
//                        continue;
//                    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
//                             IllegalAccessException e) {
//                        // fallback: static population
//                        try {
//                            deserializeClassFromMap(null, elementType, mapElem);
//                        } catch (IllegalAccessException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                        out.add(null);
//                        continue;
//                    }
//                }
//
//                // serializer-backed element
//                if (elementType != null) {
//                    ConfigSerializer<?> elemSer = SerializerRegistry.get(elementType);
//                    if (elemSer != null) {
//                        Object des = deserializeUsingSerializer(mapElem, elemSer);
//                        out.add(des);
//                        continue;
//                    }
//                }
//
//                // fallback: keep map
//                out.add(mapElem);
//            } else {
//                // primitive or already-deserialized value
//                out.add(elem);
//            }
//        }
//
//        return out;
//    }
//
//    // -----------------------
//    // Helpers
//    // -----------------------
//    private static Class<?> extractListElementType(Field field) {
//        Type g = field.getGenericType();
//        if (!(g instanceof ParameterizedType)) return null;
//        Type[] args = ((ParameterizedType) g).getActualTypeArguments();
//        if (args.length != 1) return null;
//        if (args[0] instanceof Class<?>) return (Class<?>) args[0];
//        return null;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static Object deserializeUsingSerializer(Object raw, ConfigSerializer<?> serializer) {
//        if (raw instanceof ConfigurationSection) {
//            return ((ConfigSerializer<Object>) serializer).deserialize((ConfigurationSection) raw);
//        }
//        if (raw instanceof Map<?, ?>) {
//            Map<?, ?> m = (Map<?, ?>) raw;
//            YamlConfiguration temp = new YamlConfiguration();
//            for (Map.Entry<?, ?> e : m.entrySet()) temp.set(String.valueOf(e.getKey()), e.getValue());
//            return ((ConfigSerializer<Object>) serializer).deserialize(temp);
//        }
//        // cannot use serializer for non-section data; return raw
//        return raw;
//    }
//
//    private static boolean isConfigClass(Class<?> type) {
//        return type != null && type.isAnnotationPresent(ConfigFormat.class);
//    }
//
//    /**
//     * Convert arbitrary objects into plain Maps/Lists/primitives suitable for SnakeYAML
//     * - ConfigurationSection / MemorySection -> Map
//     * - Map -> normalized Map of String -> normalized value
//     * - List -> normalized list
//     * - Registered serializer runtime values -> serializer.serialize(...) then normalize
//     * - primitives/strings left as-is
//     */
//    @SuppressWarnings("unchecked")
//    private static Object normalizeForYaml(Object value) {
//        if (value == null) return null;
//
//        if (value instanceof ConfigurationSection) {
//            ConfigurationSection sec = (ConfigurationSection) value;
//            Map<String, Object> m = new LinkedHashMap<>();
//            for (String k : sec.getKeys(false)) m.put(k, normalizeForYaml(sec.get(k)));
//            return m;
//        }
//        if (value instanceof MemorySection) {
//            MemorySection ms = (MemorySection) value;
//            Map<String, Object> m = new LinkedHashMap<>();
//            for (String k : ms.getKeys(false)) m.put(k, normalizeForYaml(ms.get(k)));
//            return m;
//        }
//        if (value instanceof Map<?, ?>) {
//            Map<String, Object> out = new LinkedHashMap<>();
//            for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet())
//                out.put(String.valueOf(e.getKey()), normalizeForYaml(e.getValue()));
//            return out;
//        }
//        if (value instanceof List<?>) {
//            List<Object> out = new ArrayList<>();
//            for (Object o : (List<?>) value) out.add(normalizeForYaml(o));
//            return out;
//        }
//
//        ConfigSerializer<Object> ser = (ConfigSerializer<Object>) SerializerRegistry.get(value.getClass());
//        if (ser != null) {
//            Object serOut = ser.serialize(value);
//            return normalizeForYaml(serOut);
//        }
//
//        // primitives, strings, numbers, booleans left as-is
//        return value;
//    }

    public static void saveConfig(@NotNull Class<?> configClass, @NotNull Path target) throws IOException, IllegalAccessException {
        File file = target.toFile();
        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        serializeClassToMap(configClass, yaml);
        yaml.save(file);
    }

    public static void loadConfig(@NotNull Class<?> configClass, @NotNull Path source) throws IOException, IllegalAccessException {
        File file = source.toFile();

        if (!file.exists()) {
            throw new FileNotFoundException("File " + source + " does not exist");
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        deserializeClassFromMap(configClass, yaml);

    }

    private static void serializeClass(@NotNull Class<?> clazz) {
        throw new UnsupportedOperationException("Not yet implemented");

        // Take class fields, convert to Map<String, primative>
        // Serialize each field with serializer
        // Write to target file using SnakeYAML

    }

    public static void serializeClassToMap(@NotNull Object instance, @NotNull ConfigurationSection out) throws IllegalAccessException {
        Field[] fields;
        boolean isStaticContext = instance instanceof Class<?>;

        if (isStaticContext) {
            fields = ((Class<?>) instance).getDeclaredFields();
        } else {
            fields = instance.getClass().getDeclaredFields();
        }

        for (Field field : fields) {
            field.setAccessible(true);

            // Skip non-static fields when serializing a class (static context)
            // Skip static fields when serializing an instance
            boolean isStaticField = Modifier.isStatic(field.getModifiers());
            if (isStaticContext && !isStaticField) continue;
            if (!isStaticContext && isStaticField) continue;

            Object raw = field.get(isStaticField ? null : instance);
            if (raw == null) {
                continue;
            }

            Annotation[] fieldAnnotations = field.getAnnotations();
            boolean isKey = false;
            ConfigKey keyValue = null;
            for (Annotation annotation : fieldAnnotations) {
                if (annotation instanceof ConfigKey) {
                    isKey = true;
                    keyValue = (ConfigKey) annotation;
                    break;
                }
            }

            String key;
            if (!isKey) {
                key = field.getName();
            } else {
                key = keyValue.value();
            }
            Class<?> fieldType = field.getType();

            // Check to see if field's class is of ConfigFormat Type
            Annotation[] annotations = field.getType().getAnnotations();
            boolean isConfigClass = false;
            for (Annotation annotation : annotations) {
                if (annotation instanceof ConfigFormat) {
                    isConfigClass = true;
                }
            }

            @SuppressWarnings("unchecked") ConfigSerializer<Object> configSerializer = (ConfigSerializer<Object>) SerializerRegistry.get(fieldType);

            // Handle Lists BEFORE checking for config classes
            if (raw instanceof List<?> && isKey) {
                List<?> list = (List<?>) raw;
                List<Object> serializedList = new ArrayList<>();

                for (Object item : list) {
                    if (item == null) continue;

                    // Check if list items are config classes
                    Class<?> itemClass = item.getClass();
                    boolean isItemConfigClass = itemClass.isAnnotationPresent(ConfigFormat.class);
                    ConfigurationSection itemSection = new MemoryConfiguration();
                    serializeClassToMap(item, itemSection);
                    serializedList.add(itemSection);

                }

                out.set(key, serializedList);
                continue;
            } else if (isConfigClass) {
                ConfigurationSection child = out.createSection(key);
                serializeClassToMap(raw, child);
                continue;
            } else if (configSerializer != null) {
                Object serialize = configSerializer.serialize(field.get(isStaticField ? null : instance));
                // Normalize the serialized output to plain data structures
                out.set(key, serialize);
                continue;
            } else if ((fieldType.isPrimitive() || fieldType == String.class) && isKey) {
                out.set(key, raw);
            }

            if (isKey && keyValue.comment() != null) {
                out.setComments(key, List.of(keyValue.comment()));
            }

        }
    }

    private static void deserializeClass(@NotNull Class<?> clazz) {
        throw new UnsupportedOperationException("Not yet implemented");

        // s

    }

    public static void deserializeClassFromMap(@NotNull Object instance, @NotNull ConfigurationSection in) throws IllegalAccessException {
        Field[] fields;
        boolean isStaticContext = instance instanceof Class<?>;

        if (isStaticContext) {
            fields = ((Class<?>) instance).getDeclaredFields();
        } else {
            fields = instance.getClass().getDeclaredFields();
        }

        for (Field field : fields) {
            field.setAccessible(true);

            // Skip non-static fields when deserializing a class (static context)
            // Skip static fields when deserializing an instance
            boolean isStaticField = Modifier.isStatic(field.getModifiers());
            if (isStaticContext && !isStaticField) continue;
            if (!isStaticContext && isStaticField) continue;

            // Get the ConfigKey annotation to find the proper key name
            ConfigKey keyValue = field.getAnnotation(ConfigKey.class);

            // For non-static contexts (instance deserialization), ONLY deserialize fields with @ConfigKey
            if (!isStaticContext && keyValue == null) {
                continue;
            }

            // Determine the key name (same logic as serialize)
            String key;
            if (keyValue == null) {
                key = field.getName();
            } else {
                key = keyValue.value();
            }

            // Skip if the key doesn't exist in the config
            if (!in.contains(key)) {
                continue;
            }

            Object raw = in.get(key);
            if (raw == null) {
                continue;
            }

            Class<?> fieldType = field.getType();

            // Handle Lists BEFORE checking for config classes
            if (raw instanceof List<?>) {
                List<?> rawList = (List<?>) raw;
                List<Object> deserializedList = new ArrayList<>();

                // Try to determine the list element type
                Class<?> elementType = null;
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                        elementType = (Class<?>) typeArgs[0];
                    }
                }

                for (Object item : rawList) {
                    if (item == null) continue;

                    if (item instanceof Map<?, ?> && elementType != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) item;

                        // Check if element type is a config class
                        if (elementType.isAnnotationPresent(ConfigFormat.class)) {
                            try {
                                Object itemInstance = elementType.getDeclaredConstructor().newInstance();
                                MemoryConfiguration tempConfig = new MemoryConfiguration();
                                for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                                    tempConfig.set(entry.getKey(), entry.getValue());
                                }
                                deserializeClassFromMap(itemInstance, tempConfig);
                                deserializedList.add(itemInstance);
                            } catch (Exception e) {
                                // Skip this item if we can't instantiate it
                            }
                            continue;
                        }

                        // Check if there's a serializer for the element type
                        ConfigSerializer<?> elemSerializer = SerializerRegistry.get(elementType);
                        if (elemSerializer != null) {
                            MemoryConfiguration tempConfig = new MemoryConfiguration();
                            for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                                tempConfig.set(entry.getKey(), entry.getValue());
                            }
                            Object deserialized = elemSerializer.deserialize(tempConfig);
                            deserializedList.add(deserialized);
                            continue;
                        }
                    }

                    // For primitives or already-deserialized items
                    deserializedList.add(item);
                }

                if (isStaticField) {
                    field.set(null, deserializedList);
                } else {
                    field.set(instance, deserializedList);
                }
                continue;
            }

            // Check if field's class is a ConfigFormat Type (nested config class)
            boolean isConfigClass = fieldType.isAnnotationPresent(ConfigFormat.class);

            // If it's a nested config class, deserialize recursively
            if (isConfigClass) {
                if (raw instanceof ConfigurationSection) {
                    ConfigurationSection childSection = (ConfigurationSection) raw;

                    // For static fields, pass the field type as a Class
                    if (isStaticField) {
                        deserializeClassFromMap(fieldType, childSection);
                    } else {
                        // For instance fields, get or create the nested object
                        Object nestedInstance = field.get(instance);
                        if (nestedInstance == null) {
                            try {
                                nestedInstance = fieldType.getDeclaredConstructor().newInstance();
                                field.set(instance, nestedInstance);
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        deserializeClassFromMap(nestedInstance, childSection);
                    }
                }
                continue;
            }

            // Check if there's a registered serializer for this field type
            @SuppressWarnings("unchecked")
            ConfigSerializer<Object> configSerializer = (ConfigSerializer<Object>) SerializerRegistry.get(fieldType);

            if (configSerializer != null) {
                // Convert raw data to ConfigurationSection if needed
                ConfigurationSection section;
                if (raw instanceof ConfigurationSection) {
                    section = (ConfigurationSection) raw;
                } else if (raw instanceof Map<?, ?>) {
                    // Convert Map to ConfigurationSection
                    MemoryConfiguration tempConfig = new MemoryConfiguration();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) raw;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        tempConfig.set(entry.getKey(), entry.getValue());
                    }
                    section = tempConfig;
                } else {
                    // Can't deserialize from this type
                    continue;
                }

                Object deserialized = configSerializer.deserialize(section);

                // For static fields, pass null as the instance
                if (isStaticField) {
                    field.set(null, deserialized);
                } else {
                    field.set(instance, deserialized);
                }
                continue;
            }

            // For primitive types and simple objects, set directly
            // For static fields, pass null as the instance
            if (isStaticField) {
                field.set(null, raw);
            } else {
                field.set(instance, raw);
            }
        }
    }
}