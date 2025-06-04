package me.solar.apollo.apolloCore.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.FallingBlock

object WorldUtils {
    fun spawnFallingBlock(location: Location, material: Material): FallingBlock {
        val world = location.world
        val fallingBlock : FallingBlock? = world?.spawn(location, FallingBlock::class.java)
        if (fallingBlock == null) {
            throw IllegalStateException("No FallingBlock found")
        }
        fallingBlock.blockData.to(material.createBlockData())
        return fallingBlock
    }
}