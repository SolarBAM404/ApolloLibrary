package me.solarbam.apollo.apolloCore.file;

import lombok.Getter;
import com.google.gson.Gson;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import java.util.HashMap;
import java.util.Map;

public class FileConfig {

    @Getter
    protected final SerialiseUtil.Mode mode;

    protected Map<String, Object> data = new HashMap<>();

    @Getter
    @Setter
    protected String pathPrefix = "";

    @Setter
    protected FileConfig defaults;

    public FileConfig(final SerialiseUtil.Mode mode) {
        this.mode = mode;
    }

    public void loadFromString(@NotNull String contents) {
        if (mode == SerialiseUtil.Mode.YAML) {
            Yaml yaml = new Yaml();
            data = yaml.load(contents);
        } else if (mode == SerialiseUtil.Mode.JSON) {
            Gson gson = new Gson();
            data = gson.fromJson(contents, Map.class);
        }
    }

    public void loadFromString(@NotNull String contents, Class<?> clazz) {
        if (mode == SerialiseUtil.Mode.YAML) {
            Yaml yaml = new Yaml();
            yaml.loadAs(contents, clazz);
        } else if (mode == SerialiseUtil.Mode.JSON) {
            Gson gson = new Gson();
            gson.fromJson(contents, clazz);
        }
    }

    public String saveToString(Object obj) {
        if (mode == SerialiseUtil.Mode.YAML) {
            Yaml yaml = new Yaml();
            return yaml.dump(obj);
        } else if (mode == SerialiseUtil.Mode.JSON) {
            Gson gson = new Gson();
            return gson.toJson(obj);
        }
        return null;
    }

    public String buildPath(String path) {
        if (pathPrefix == null || pathPrefix.isEmpty()) return path;
        if (path == null || path.isEmpty()) return pathPrefix;
        return String.join(".", pathPrefix, path);
    }

    public @Nullable Object get(String path) {
        String fullPath = buildPath(path);
        Object value = getValue(fullPath);

        if (value == null && defaults != null) {
            Object defaultValue = defaults.getValue(fullPath);
            if (defaultValue != null) {
                setValue(fullPath, defaultValue);
                return defaultValue;
            }

            return null;
        }
        return value;
    }

    protected Object getValue(String path) {
        String fullPath = buildPath(path);

        return data.get(fullPath);
    }

    protected void setValue(String path, Object value) {
        String fullPath = buildPath(path);
        data.put(fullPath, value);
    }

    public String getString(String path) {
        Object value = get(path);
        return value != null ? value.toString() : null;
    }

    public Integer getInteger(String path) {
        Object value = get(path);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getBoolean(String path) {
        Object value = get(path);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    public Double getDouble(String path) {
        Object value = get(path);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public <T> T getObject(String path, Class<T> clazz) {
        Object value = get(path);
        if (value == null) return null;

        if (mode == SerialiseUtil.Mode.YAML) {
            Yaml yaml = new Yaml();
            String serialized = yaml.dump(value);
            return yaml.loadAs(serialized, clazz);
        } else if (mode == SerialiseUtil.Mode.JSON) {
            Gson gson = new Gson();
            String serialized = gson.toJson(value);
            return gson.fromJson(serialized, clazz);
        }
        return null;
    }

}
