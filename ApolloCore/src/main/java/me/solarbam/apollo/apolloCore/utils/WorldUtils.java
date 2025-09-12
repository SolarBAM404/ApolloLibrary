package me.solarbam.apollo.apolloCore.utils;

import org.bukkit.entity.FallingBlock;

public class WorldUtils {
    private WorldUtils() {
        // Utility class
    }

    public static FallingBlock spawnFallingBlock(org.bukkit.Location location, org.bukkit.Material material) {
        org.bukkit.entity.FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, material.createBlockData());
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
        return fallingBlock;
    }

}