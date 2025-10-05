package me.solar.apolloLibrary.tools;

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

public class ToolListener implements Listener {
    private final JavaPlugin plugin;

    public ToolListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Common.registerListener(plugin, this);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = false
    )
    public void onToolClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Tool tool = Tool.getTool(player.getItemInHand());
        if (tool != null) {
            try {
                tool.onBlockClick(event);
                if (tool.autoCancel()) {
                    event.setCancelled(true);
                }
            } catch (Exception e) {
                event.setCancelled(true);
                String action = String.valueOf(event.getAction());
                Common.tell(player, "<red><bold>Error:</bold>Failed to handle " + action + " using tool: " + tool.getClass().getSimpleName());
                String var10000 = String.valueOf(event.getAction());
                Common.log("<red><bold>Error:</bold>Failed to handle " + action + " using tool: " + tool.getClass().getSimpleName());
                this.plugin.getLogger().severe(e.getMessage());
            }
        }

    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onToolPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Tool tool = Tool.getTool(player.getItemInHand());
        if (tool != null) {
            try {
                tool.onBlockPlace(event);
                if (tool.autoCancel()) {
                    event.setCancelled(true);
                }
            } catch (Throwable t) {
                event.setCancelled(true);
                Common.tell(player, "<red><bold>Error:</bold>Failed to handle block place event using tool: " + tool.getClass().getSimpleName());
                Common.log("<red><bold>Error:</bold>Failed to handle block place event using tool: " + tool.getClass().getSimpleName());
                this.plugin.getLogger().severe(t.getMessage());
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
        Tool tool = Tool.getTool(player.getItemInHand());
        if (tool != null) {
            tool.shutdown(event.getPlayer());
        }

    }
}
