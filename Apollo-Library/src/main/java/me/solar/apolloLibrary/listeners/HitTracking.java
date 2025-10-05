package me.solar.apolloLibrary.listeners;

import me.solar.apolloLibrary.collection.expiringmap.ExpiringMap;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class HitTracking implements Listener {
    private static final ExpiringMap<UUID, List<Consumer<ProjectileHitEvent>>> flyingProjectiles;

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onHit(ProjectileHitEvent event) {
        List<Consumer<ProjectileHitEvent>> hitListeners = flyingProjectiles.remove(event.getEntity().getUniqueId());
        if (hitListeners != null) {
            for(Consumer<ProjectileHitEvent> listener : hitListeners) {
                listener.accept(event);
            }
        }

    }

    public static void addFlyingProjectile(Projectile projectile, Consumer<ProjectileHitEvent> hitTask) {
        UUID uniqueId = projectile.getUniqueId();
        List<Consumer<ProjectileHitEvent>> listeners = (List)flyingProjectiles.getOrDefault(uniqueId, new ArrayList());
        listeners.add(hitTask);
        flyingProjectiles.put(uniqueId, listeners);
    }

    static {
        flyingProjectiles = ExpiringMap.builder().expiration(30L, TimeUnit.SECONDS).build();
    }
}
