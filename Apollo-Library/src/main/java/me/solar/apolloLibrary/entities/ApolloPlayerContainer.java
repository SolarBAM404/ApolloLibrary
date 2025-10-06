package me.solar.apolloLibrary.entities;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface ApolloPlayerContainer
{

    UUID getUniqueId();
    OfflinePlayer getOfflinePlayer();

    PersistentDataContainerView getPersistentDataContainer();

}
