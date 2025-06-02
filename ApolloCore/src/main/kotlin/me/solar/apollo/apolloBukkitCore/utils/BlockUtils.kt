@file:JvmName("BlockUtils")
package me.solar.apollo.apolloBukkitCore.utils

import org.bukkit.Location
import org.bukkit.Material
import kotlin.math.max
import kotlin.math.min

fun minMax(primary: Location, secondary: Location): Pair<Location, Location> {
    val minX = min(primary.x, secondary.x)
    val minY = min(primary.y, secondary.y)
    val minZ = min(primary.z, secondary.z)

    val maxX = max(primary.x, secondary.x)
    val maxY = max(primary.y, secondary.y)
    val maxZ = max(primary.z, secondary.z)

    return Pair(
        Location(primary.world, minX, minY, minZ),
        Location(primary.world, maxX, maxY, maxZ)
    )
}

fun boundingBox(primary: Location, secondary: Location): List<Location> {
    val locations = mutableListOf<Location>()

    val pair = minMax(primary, secondary)

    for (x in pair.first.x.toInt()..pair.second.x.toInt()) {
        for (y in pair.first.y.toInt()..pair.second.y.toInt()) {
            for (z in pair.first.z.toInt()..pair.second.z.toInt()) {
                locations.add(Location(primary.world, x.toDouble(), y.toDouble(), z.toDouble()))
            }
        }
    }
    return locations
}

fun Location.isInBoundingBox(primary: Location, secondary: Location): Boolean {
    val pair = minMax(primary, secondary)
    return x in pair.first.x..pair.second.x && y >= pair.first.y
            && y <= pair.second.y && z >= pair.first.z && z <= pair.second.z
}

fun plotLine(alpha: Location, bravo: Location): List<ApolloVector> {
    return plotLine(alpha.toApolloVector(), bravo.toApolloVector())
}

fun plotLine(alpha: org.bukkit.util.Vector, bravo: org.bukkit.util.Vector): List<ApolloVector> {
    return plotLine(alpha.toApolloVector(), bravo.toApolloVector())
}

fun plotLine(alpha: ApolloVector, bravo: ApolloVector): List<ApolloVector> {
    val locations = mutableListOf<ApolloVector>()
    val delta = bravo.subtract(alpha)
    val length = delta.magnitude()
    val direction = delta.divide(ApolloVector(length, length, length))

    for (i in 0 until length.toInt()) {
        val location = alpha.add(direction.multiply(ApolloVector(i.toDouble(), i.toDouble(), i.toDouble())))
        locations.add(location)
    }
    return locations
}

// --- Location Methods ---
fun getMinLocation(primary: Location, secondary: Location): Location {
    val minX = min(primary.x, secondary.x)
    val minY = min(primary.y, secondary.y)
    val minZ = min(primary.z, secondary.z)
    return Location(primary.world, minX, minY, minZ)
}

fun getMaxLocation(primary: Location, secondary: Location): Location {
    val maxX = max(primary.x, secondary.x)
    val maxY = max(primary.y, secondary.y)
    val maxZ = max(primary.z, secondary.z)
    return Location(primary.world, maxX, maxY, maxZ)
}

fun getCenterLocation(primary: Location, secondary: Location): Location {
    val pair = minMax(primary, secondary)
    val minPair = pair.first
    val maxPair = pair.second

    val x = minPair.x + (maxPair.x - minPair.x) / 2
    val y = minPair.y + (maxPair.y - minPair.y) / 2
    val z = minPair.z + (maxPair.z - minPair.z) / 2

    return Location(primary.world, x, y, z)
}

fun getHeight(primary: Location, secondary: Location): Int {
    return (max(primary.y, secondary.y) - min(primary.y, secondary.y)).toInt()
}

fun getWidth(primary: Location, secondary: Location): Int {
    return (max(primary.x, secondary.x) - min(primary.x, secondary.x)).toInt()
}

fun getLength(primary: Location, secondary: Location): Int {
    return (max(primary.z, secondary.z) - min(primary.z, secondary.z)).toInt()
}

fun getVolume(primary: Location, secondary: Location): Int {
    return getHeight(primary, secondary) * getWidth(primary, secondary) * getLength(primary, secondary)
}

fun getSurfaceArea(primary: Location, secondary: Location): Int {
    return 2 * (getHeight(primary, secondary) * getWidth(primary, secondary) + getHeight(
        primary,
        secondary
    ) * getLength(primary, secondary) + getWidth(primary, secondary) * getLength(primary, secondary))
}

fun getDiagonal(primary: Location, secondary: Location): Double {
    return primary.distance(secondary)
}

fun equalsLocation(primary: Location, secondary: Location): Boolean {
    return primary.x == secondary.x && primary.y == secondary.y && primary.z == secondary.z
}

fun isSafeSpawnLocation(location: Location): Boolean {
    return isSafeSpawnLocation(location.clone(), 2)
}

fun isSafeSpawnLocation(location: Location, height: Int): Boolean {
    for (i in 0 until height) {
        if (location.add(0.0, i.toDouble(), 0.0).block.type != Material.AIR) {
            return false
        }
    }
    return true
}

fun shootBlock(location: Location, velocity: ApolloVector, direction: ApolloVector) {
    val fallingBlock = WorldUtils.spawnFallingBlock(location, Material.STONE)
    fallingBlock.velocity = (velocity.multiply(direction)).toBukkit()
}