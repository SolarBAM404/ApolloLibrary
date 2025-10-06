package me.solar.apolloLibrary.games.managers;

import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import me.solar.apolloLibrary.entities.ApolloPlayerContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamesPlayer implements ApolloPlayerContainer {

    private UUID uuid;

    @Getter
    private final Map<String, Object> metadata = new HashMap<>();

    public GamesPlayer(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public PersistentDataContainerView getPersistentDataContainer() {
        return getOfflinePlayer().getPersistentDataContainer();
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }
}
