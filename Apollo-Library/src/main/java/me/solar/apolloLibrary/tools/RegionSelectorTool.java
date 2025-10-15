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

//    public static RegionSelectorTool INSTANCE = new RegionSelectorTool(null);
//
//    protected static final Material primaryMaterial = Material.GOLD_BLOCK;
//    protected static final Material secondaryMaterial = Material.REDSTONE_BLOCK;
//    protected final Map<UUID, CuboidRegion> regions = new HashMap<>();
//    protected final Map<UUID, Location> primaryLocations = new HashMap<>();
//    protected final Map<UUID, Location> secondaryLocations = new HashMap<>();
//
//    protected Material toolMaterial = Material.WOODEN_HOE;
//    protected String toolName = "Region Selector";
//    protected String[] toolLore = new String[]{"Select a region"};
//
//    public RegionSelectorTool(JavaPlugin plugin) {
//        super(plugin);
//        INSTANCE = this;
//    }
//
//    public static RegionSelectorTool getInstance() {
//        try {
//            Class<?> clazz = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()); // Get the calling class
//            RegionSelectorTool tool = (RegionSelectorTool) clazz.getField("INSTANCE").get(null);
//            if (tool == null) {
//                if (!RegionSelectorTool.class.isAssignableFrom(clazz)) {
//                    throw new IllegalStateException("The class " + clazz.getName() + " must extend RegionSelectorTool");
//                }
//
//                tool = (RegionSelectorTool) clazz.getConstructor(JavaPlugin.class).newInstance(JavaPlugin.getProvidingPlugin(clazz));
//            }
//
//            return tool;
//        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Falied to get instance of the tool", e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException("Failed to create new instance of the tool", e);
//        }
//    }
//
//    public static CuboidRegion getRegion(UUID uuid) {
//        return getInstance().getCuboidRegion(uuid);
//    }
//
//    public static CuboidRegion getRegion(Player player) {
//        return getRegion(player.getUniqueId());
//    }
//
//    @Override
//    public ItemStack getItem() {
//        ItemStack item = new ItemStack(toolMaterial);
//        ItemStackUtils.setDisplayName(item, toolName);
//        ItemStackUtils.setLore(item, toolLore);
//        return item;
//    }
//
//    public CuboidRegion getCuboidRegion(UUID uuid) {
//        return regions.get(uuid);
//    }
//
//    @Override
//    protected void onBlockPlace(BlockPlaceEvent event) {
//        event.setCancelled(true);
//        primaryLocations.put(event.getPlayer().getUniqueId(), event.getBlockPlaced().getLocation());
//    }
//
//    @Override
//    protected void onBlockClick(PlayerInteractEvent event) {
//        event.setCancelled(true);
//        secondaryLocations.put(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
//        onHotbarFocused(event.getPlayer());
//    }
//
//
//
//    @Override
//    protected void onHotbarFocused(Player player) {
//
//        Location primary = primaryLocations.get(player.getUniqueId());
//        Location secondary = secondaryLocations.get(player.getUniqueId());
//        if (primary == null || secondary == null) {
//            return;
//        }
//
//        PlayerUtils.sendBlock(player, primary, Material.GOLD_BLOCK);
//        PlayerUtils.sendBlock(player, secondary, Material.REDSTONE_BLOCK);
//
//        CuboidRegion region = getCuboidRegion(player.getUniqueId());
//        if (region == null) {
//
//
//            region = new CuboidRegion(primary, secondary);
//            regions.put(player.getUniqueId(), region);
//        }
//
//        region.visualize();
//        region.addViewer(player);
//    }
//
//    @Override
//    protected void onHotbarDefocused(Player player) {
//        CuboidRegion region = getCuboidRegion(player.getUniqueId());
//
//        if (region == null) {
//            return;
//        }
//
//        region.removeViewer(player);
//        region.unvisualize();
//    }
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

        if (event.getAction().isLeftClick()) {
            onLeftClick(event, playerMap, region, interactionPoint);
        } else if (event.getHand() == EquipmentSlot.HAND) {
            onRightClick(event, playerMap, region, interactionPoint);
        }
        visualiseRegion(player);
    }

    protected void onLeftClick(PlayerInteractEvent event, Map<String, Object> playerMap, CuboidRegion region, Location interactionPoint) {
        Player player = event.getPlayer();

        if (region.getPointA() != null) {
            player.sendBlockChange(region.getPointA(), ((Block) playerMap.get("preBlockA")).getBlockData());
        }

        region.setPointA(interactionPoint);
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
            player.sendBlockChange(region.getPointB(), ((Block) playerMap.get("preBlockB")).getBlockData());
        }

        region.setPointB(interactionPoint);
        Block block = region.getPointB().getBlock();

        String message = "<green>Secondary position set to: x: <yellow>" + block.getX() + "<green> y: <yellow>" + block.getY() + "<green> z: <yellow>" + block.getZ();
        Common.tell(player, message);

        if (region.getPointA() == interactionPoint) {
            return;
        }

        playerMap.put("preBlockB", interactionPoint.getBlock());
        visualiseRegion(player);
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
        if (region.getPointA() != null) {
            PlayerUtils.sendBlock(player, region.getPointA(), primaryMaterial.createBlockData());
        }
        if (region.getPointB() != null) {
            PlayerUtils.sendBlock(player, region.getPointB(), secondaryMaterial.createBlockData());
        }

        if (region.getPointA() != null && region.getPointB() != null) {
            region.addViewer(player);
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
