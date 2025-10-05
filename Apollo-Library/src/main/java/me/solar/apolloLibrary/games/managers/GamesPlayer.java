package me.solar.apolloLibrary.games.managers;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamesPlayer {

    private final UUID uuid;
    @Getter
    private final Map<String, Object> data = new HashMap<>();

    public GamesPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

}
