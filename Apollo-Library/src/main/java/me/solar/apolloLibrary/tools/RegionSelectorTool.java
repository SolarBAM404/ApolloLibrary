package me.solar.apolloLibrary.tools;

import me.solar.apolloLibrary.ItemStackUtils;
import me.solar.apolloLibrary.world.CuboidRegion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionSelectorTool extends Tool{

    public static RegionSelectorTool INSTANCE = new RegionSelectorTool(null);

    protected static final Material primaryMaterial = Material.GOLD_BLOCK;
    protected static final Material secondaryMaterial = Material.REDSTONE_BLOCK;
    protected final Map<UUID, CuboidRegion> regions = new HashMap<>();

    protected Material toolMaterial = Material.WOODEN_HOE;
    protected String toolName = "Region Selector";
    protected String[] toolLore = new String[]{"Select a region"};

    public RegionSelectorTool(JavaPlugin plugin) {
        super(plugin);
        INSTANCE = this;
    }

    public static RegionSelectorTool getInstance() {
        try {
            Class<?> clazz = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()); // Get the calling class
            RegionSelectorTool tool = (RegionSelectorTool) clazz.getField("INSTANCE").get(null);
            if (tool == null) {
                if (!RegionSelectorTool.class.isAssignableFrom(clazz)) {
                    throw new IllegalStateException("The class " + clazz.getName() + " must extend RegionSelectorTool");
                }

                tool = (RegionSelectorTool) clazz.getConstructor(JavaPlugin.class).newInstance(JavaPlugin.getProvidingPlugin(clazz));
            }

            return tool;
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Falied to get instance of the tool", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to create new instance of the tool", e);
        }
    }

    public static CuboidRegion getRegion(UUID uuid) {
        return getInstance().getCuboidRegion(uuid);
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(toolMaterial);
        ItemStackUtils.setDisplayName(item, toolName);
        ItemStackUtils.setLore(item, toolLore);
        return item;
    }

    public CuboidRegion getCuboidRegion(UUID uuid) {
        return regions.get(uuid);
    }


}
