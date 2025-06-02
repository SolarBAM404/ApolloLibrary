@file:JvmName("Vector")
package me.solar.apollo.apolloBukkitCore.utils

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class ApolloVector {

    val x: Double
    val y: Double
    val z: Double

    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun add(vector: ApolloVector): ApolloVector {
        return ApolloVector(x + vector.x, y + vector.y, z + vector.z)
    }

    fun subtract(vector: ApolloVector): ApolloVector {
        return ApolloVector(x - vector.x, y - vector.y, z - vector.z)
    }

    fun multiply(vector: ApolloVector): ApolloVector {
        return ApolloVector(x * vector.x, y * vector.y, z * vector.z)
    }

    fun divide(vector: ApolloVector): ApolloVector {
        return ApolloVector(x / vector.x, y / vector.y, z / vector.z)
    }

    fun dotProduct(vector: ApolloVector): Double {
        return x * vector.x + y * vector.y + z * vector.z
    }

    fun crossProduct(vector: ApolloVector): ApolloVector {
        val x = y * vector.z - z * vector.y
        val y = z * vector.x - x * vector.z
        val z = x * vector.y - y * vector.x
        return ApolloVector(x, y, z)
    }

    fun magnitude(): Double {
        return sqrt((x * x + y * y + z * z).toDouble())
    }

    fun normalize(): ApolloVector {
        val magnitude = magnitude()
        return ApolloVector(x / magnitude.toInt(), y / magnitude.toInt(), z / magnitude.toInt())
    }

    fun angle(vector: ApolloVector): Double {
        val dotProduct = dotProduct(vector)
        val magnitude = magnitude() * vector.magnitude()
        return acos(dotProduct / magnitude)
    }

    fun isParallel(vector: ApolloVector): Boolean {
        return crossProduct(vector).magnitude() == 0.0
    }

    fun isOrthogonal(vector: ApolloVector): Boolean {
        return dotProduct(vector) == 0.0
    }

    fun isCollinear(vector: ApolloVector): Boolean {
        return isParallel(vector) && dotProduct(vector) > 0
    }

    fun isOpposite(vector: ApolloVector): Boolean {
        return isParallel(vector) && dotProduct(vector) < 0
    }

    fun distance(vector: ApolloVector): Double {
        return sqrt((vector.x - x).pow(2.0) + (vector.y - y).pow(2.0) + (vector.z - z).pow(2.0))
    }

    override fun toString(): String {
        return "Vector(x=$x, y=$y, z=$z)"
    }
}

fun ApolloVector.toBukkit() : org.bukkit.util.Vector {
    return org.bukkit.util.Vector(x, y, z)
}


fun ApolloVector.toLocation() : Location {
    return Location(null, x, y, z)
}


fun ApolloVector.fromBukkit(v : org.bukkit.util.Vector) : ApolloVector {
    return ApolloVector(v.x, v.y, v.z)
}


fun ApolloVector.fromLocation(location: Location) : ApolloVector {
    return ApolloVector(location.x, location.y, location.z)
}


fun org.bukkit.util.Vector.toApolloVector() : ApolloVector {
    return ApolloVector(x, y, z)
}


fun Location.toApolloVector() : ApolloVector {
    return ApolloVector(x, y, z)
}