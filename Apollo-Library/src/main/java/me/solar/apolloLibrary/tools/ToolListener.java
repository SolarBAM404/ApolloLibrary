package me.solar.apolloLibrary.tools;

import me.solar.apolloLibrary.collection.expiringmap.ExpiringMap;
import me.solar.apolloLibrary.utils.Common;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class ToolListener implements Listener {
    private static final ExpiringMap<Player, Tool> lastUsedMap = ExpiringMap.builder()
            .variableExpiration()
            .expiration(200, TimeUnit.MILLISECONDS)
            .build();

    private final JavaPlugin plugin;

    public ToolListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Common.registerListener(plugin, this);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onToolClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Tool tool = Tool.getTool(player.getInventory().getItemInMainHand());

        if (tool == null || player == null) {
            return;
        }

        if (lastUsedMap.containsKey(player) && lastUsedMap.getExpectedExpiration(player) > 0) {
            return;
        }

        lastUsedMap.put(player, tool);
        if (        try {
            tool.onBlockClick(event);
            if (tool.autoCancel()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            event.setCancelled(true);
            String action = String.valueOf(event.getAction());
            Common.tell(player, "<red><bold>Error:</bold>Failed to handle " + action + " using tool: " + tool.getClass().getSimpleName());
            Common.log("<red><bold>Error:</bold>Failed to handle " + action + " using tool: " + tool.getClass().getSimpleName());
            Common.log(e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                Common.log(element.toString());
            }
        }

    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onToolPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Tool tool = Tool.getTool(player.getInventory().getItemInMainHand());
        if (tool != null) {
            try {
                tool.onBlockPlace(event);
                if (tool.autoCancel()) {
                    event.setCancelled(true);
                }
            } catch (Exception e) {
                event.setCancelled(true);
                Common.tell(player, "<red><bold>Error:</bold>Failed to handle block place event using tool: " + tool.getClass().getSimpleName());
                Common.log("<red><bold>Error:</bold>Failed to handle block place event using tool: " + tool.getClass().getSimpleName());
                Common.log(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    Common.log(element.toString());
                }
            }
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Tool current = Tool.getTool(player.getInventory().getItem(event.getNewSlot()));
        Tool previous = Tool.getTool(player.getInventory().getItem(event.getPreviousSlot()));
        if (current != null) {
            if (previous != null) {
                if (previous.equals(current)) {
                    return;
                }

                previous.onHotbarDefocused(player);
            }

            current.onHotbarFocused(player);
        } else if (previous != null) {
            previous.onHotbarDefocused(player);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Tool tool = Tool.getTool(player.getInventory().getItemInMainHand());
        if (tool != null) {
            tool.shutdown(event.getPlayer());
        }

    }
}
