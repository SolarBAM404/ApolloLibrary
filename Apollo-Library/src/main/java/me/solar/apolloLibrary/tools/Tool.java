package me.solar.apolloLibrary.tools;

import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.utils.ItemStackUtils;
import me.solar.apolloLibrary.utils.Valid;
import me.solar.apolloLibrary.runnables.RunnableObject;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract tool with custom behavior and registration logic.
 * Extend this class to implement specific tools.
 */
public abstract class Tool {

    /**
     * List of all registered tools.
     */
    private static final List<Tool> tools = new ArrayList();

    /**
     * Registers a tool if it is not already registered.
     * @param tool the tool to register
     */
    static void register(Tool tool) {
        Valid.checkBoolean(!isRegistered(tool), "Tool with itemstack " + String.valueOf(tool.getItem()) + " already registered");
        tools.add(tool);
    }

    /**
     * Checks if a tool is already registered.
     * @param tool the tool to check
     * @return true if registered, false otherwise
     */
    static boolean isRegistered(@NotNull Tool tool) {
        return getTool(tool.getItem()) != null;
    }

    /**
     * Gets the registered tool matching the given item.
     * @param item the item to match
     * @return the matching Tool, or null if not found
     */
    public static @Nullable Tool getTool(ItemStack item) {
        for(Tool t : tools) {
            if (t.isTool(item)) {
                return t;
            }
        }

        return null;
    }

    /**
     * Returns all registered tools.
     * @return array of registered tools
     */
    public static Tool @NotNull [] getTools() {
        return (Tool[])tools.toArray(new Tool[tools.size()]);
    }

    /**
     * Constructs and registers a new Tool instance.
     * @param plugin the plugin instance
     */
    protected Tool(JavaPlugin plugin) {
        (new Thread(() -> {
            try {
                Thread.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Common.runTask(plugin, RunnableObject.of(() -> {
                if (!isRegistered(this)) {
                    register(this);
                }

            }));
        })).start();
    }

    /**
     * Checks if the given item matches this tool's item.
     * @param item the item to check
     * @return true if the item matches this tool
     */
    public final boolean isTool(ItemStack item) {
        return ItemStackUtils.isSimilar(this.getItem(), item);
    }

    /**
     * Checks if the player is holding this tool in hand.
     * @param player the player to check
     * @return true if the player has this tool in hand
     */
    public final boolean hasToolInHand(@NotNull Player player) {
        return this.isTool(player.getItemInHand());
    }

    /**
     * Checks if the player has this tool anywhere in their inventory.
     * @param player the player to check
     * @return true if the player has this tool
     */
    public final boolean hasTool(@NotNull Player player) {
        for(ItemStack item : player.getInventory().getContents()) {
            if (this.isTool(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Registers this tool instance.
     */
    public final void register() {
        register(this);
    }

    /**
     * Gets the ItemStack representing this tool.
     * @return the tool's ItemStack
     */
    public abstract ItemStack getItem();

    /**
     * Called when a block is clicked with this tool.
     * Override to implement custom behavior.
     * @param event the block click event
     */
    protected void onBlockClick(PlayerInteractEvent event) {
    }

    /**
     * Called when a block is placed with this tool.
     * Override to implement custom behavior.
     * @param event the block place event
     */
    protected void onBlockPlace(BlockPlaceEvent event) {
    }

    /**
     * Called when the tool is focused in the hotbar.
     * Override to implement custom behavior.
     * @param player the player focusing the tool
     */
    protected void onHotbarFocused(Player player) {
    }

    /**
     * Called when the tool is defocused in the hotbar.
     * Override to implement custom behavior.
     * @param player the player defocusing the tool
     */
    protected void onHotbarDefocused(Player player) {
    }

    /**
     * Called when the tool is shut down for a player.
     * Override to implement custom behavior.
     * @param player the player
     */
    protected void shutdown(Player player) {
    }

    /**
     * Determines if events should be auto-cancelled for this tool.
     * Override to change default behavior.
     * @return true to auto-cancel, false otherwise
     */
    protected boolean autoCancel() {
        return false;
    }

    /**
     * Gives the tool to the player if they do not already have it.
     * @param player the player to give the tool to
     * @return true if the tool was given, false if the player already had it
     */
    public final boolean giveIfHasnt(Player player) {
        if (this.hasTool(player)) {
            return false;
        } else {
            this.give(player);
            return true;
        }
    }

    /**
     * Gives the tool to the player in the specified inventory slot.
     * @param player the player
     * @param slot the inventory slot
     */
    public final void give(@NotNull Player player, int slot) {
        player.getInventory().setItem(slot, this.getItem());
    }

    /**
     * Adds the tool to the player's inventory.
     * @param player the player
     */
    public final void give(@NotNull Player player) {
        player.getInventory().addItem(new ItemStack[]{this.getItem()});
    }

    /**
     * Checks if another object is a Tool with the same item.
     * @param obj the object to compare
     * @return true if equal, false otherwise
     */
    public final boolean equals(Object obj) {
        return obj instanceof Tool && ((Tool)obj).getItem().equals(this.getItem());
    }

}
