package me.solar.apolloLibrary.tools;

import lombok.Getter;
import me.solar.apolloLibrary.core.ApolloPlugin;
import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.utils.ItemStackUtils;
import me.solar.apolloLibrary.utils.PlayerUtils;
import me.solar.apolloLibrary.world.CuboidRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionSelectorTool extends Tool{

    @Getter
    static RegionSelectorTool instance = new RegionSelectorTool();

    private static final Material primaryMaterial = Material.GOLD_BLOCK;

    private static final Material secondaryMaterial = Material.GOLD_BLOCK;

    private static final Map<UUID, Map<String, Object>> regions = new HashMap<>();

    public static Map<String, Object> getRegion(UUID uuid) {
        Map<String, Object> playerMap = regions.computeIfAbsent(uuid, k -> new HashMap<>());

        return playerMap;
    }

    public static Map<String, Object> getRegion(Player player) {
        return getRegion(player.getUniqueId());
    }

    public static CuboidRegion getCuboidRegion(UUID uuid) {
        return (CuboidRegion) getRegion(uuid).get("region");
    }

    public static CuboidRegion getCuboidRegion(Player player) {
        return (CuboidRegion) getRegion(player).get("region");
    }

    @Override
    protected boolean autoCancel() {
        return true;
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(Material.WOODEN_AXE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text("Region Selector Tool").color(TextColor.fromCSSHexString("#FFAA00")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    protected void onBlockClick(PlayerInteractEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Map<String, Object> playerMap = getRegion(player);

        if (playerMap.get("region") == null) {
            playerMap.put("region", new CuboidRegion(player.getLocation(), player.getLocation()));
        }

        CuboidRegion region = (CuboidRegion) playerMap.get("region");

        Location interactionPoint;
        if (event.getClickedBlock() == null) {
            Location eyeLocation = player.getEyeLocation();
            Vector direction = player.getLocation().getDirection();
            interactionPoint = eyeLocation.add(direction);
        } else {
            interactionPoint = event.getClickedBlock().getLocation();
        }

        if (event.getAction().isLeftClick() && !event.getPlayer().isSneaking()) {
            onLeftClick(event, playerMap, region, interactionPoint);
        } else if (event.getAction().isLeftClick() && event.getPlayer().isSneaking()) {
            onShiftLeftClick(event, playerMap, region, interactionPoint);
        } else if (event.getHand() == EquipmentSlot.HAND && event.getAction().isRightClick() && !event.getPlayer().isSneaking()) {
            onRightClick(event, playerMap, region, interactionPoint);
        } else if (event.getHand() == EquipmentSlot.HAND && event.getAction().isRightClick() && event.getPlayer().isSneaking()) {
            onShiftRightClick(event, playerMap, region, interactionPoint);
        }
        visualiseRegion(player);
    }

    protected void onLeftClick(PlayerInteractEvent event, Map<String, Object> playerMap, CuboidRegion region, Location interactionPoint) {
        Player player = event.getPlayer();

        if (region.getPointA() != null) {
            if (playerMap.get("preBlockA") != null) {
                player.sendBlockChange(region.getPointA(), ((Block) playerMap.get("preBlockA")).getBlockData());
            }
        }

        region.setPointA(interactionPoint.getBlock().getLocation());
        Block block = region.getPointA().getBlock();
        String message = "<green>Primary position set to: x: <yellow>" + block.getX() + "<green> y: <yellow>" + block.getY() + "<green> z: <yellow>" + block.getZ();
        Common.tell(player, message);

        if (region.getPointB() == interactionPoint.toBlockLocation()) {
            return;
        }

        playerMap.put("preBlockA", interactionPoint.getBlock());
        visualiseRegion(player);
    }

    protected void onRightClick(PlayerInteractEvent event, Map<String, Object> playerMap, CuboidRegion region, Location interactionPoint) {
        if (!mainHandCheck(event)) {
            return;
        }

        Player player = event.getPlayer();

        if (region.getPointB() != null) {
            if (playerMap.get("preBlockB") != null) {
                player.sendBlockChange(region.getPointB(), ((Block) playerMap.get("preBlockB")).getBlockData());
            }
        }

        region.setPointB(interactionPoint.getBlock().getLocation());
        Block block = region.getPointB().getBlock();

        String message = "<green>Secondary position set to: x: <yellow>" + block.getX() + "<green> y: <yellow>" + block.getY() + "<green> z: <yellow>" + block.getZ();
        Common.tell(player, message);

        if (region.getPointA() == interactionPoint) {
            return;
        }

        playerMap.put("preBlockB", interactionPoint.getBlock());
        visualiseRegion(player);
    }

    protected void onShiftLeftClick(PlayerInteractEvent event, Map<String, Object> playerMap, CuboidRegion region, Location interactionPoint) {
    }

    protected void onShiftRightClick(PlayerInteractEvent event, Map<String, Object> playerMap, CuboidRegion region, Location interactionPoint) {
    }


    @Override
    protected void onHotbarDefocused(Player player) {
        unvisualiseRegion(player);
    }

    @Override
    protected void onHotbarFocused(Player player) {
        visualiseRegion(player); //
    }

    @Override
    protected void shutdown(Player player) {
        unvisualiseRegion(player);
        regions.remove(player);
    }

    public void visualiseRegion(Player player) {
        Map<String, Object> playerMap = getRegion(player);

        if (playerMap.get("region") == null) {
            playerMap.put("region", new CuboidRegion(player.getLocation(), player.getLocation()));
        }

        CuboidRegion region = (CuboidRegion) playerMap.get("region");
        region.addViewer(player);

        if (region.getPointA() != null) {
            playerMap.put("preBlockA", region.getPointA().getBlock());
            PlayerUtils.sendBlock(player, region.getPointA(), primaryMaterial.createBlockData());
        }
        if (region.getPointB() != null) {
            playerMap.put("preBlockB", region.getPointB().getBlock());
            PlayerUtils.sendBlock(player, region.getPointB(), secondaryMaterial.createBlockData());
        }
    }

    private void unvisualiseRegion(Player player) {
        Map<String, Object> playerMap = getRegion(player);

        if (playerMap.get("region") == null) {
            playerMap.put("region", new CuboidRegion(player.getLocation(), player.getLocation()));
        }

        CuboidRegion region = (CuboidRegion) playerMap.get("region");

        region.removeViewer(player);

        if (region.getPointA() != null) {
            PlayerUtils.sendBlock(player, region.getPointA(), ((Block) playerMap.get("preBlockA")).getBlockData());
        }
        if (region.getPointB() != null) {
            PlayerUtils.sendBlock(player, region.getPointB(), ((Block) playerMap.get("preBlockB")).getBlockData());
        }
    }

    public boolean mainHandCheck(PlayerInteractEvent event) {
        return event.getHand() == EquipmentSlot.HAND;
    }

}
