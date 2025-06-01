@file:JvmName("Vector")
package me.solar.apollo.apolloCore.utils

import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class Vector {

    val x: Double
    val y: Double
    val z: Double

    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun add(vector: Vector): Vector {
        return Vector(x + vector.x, y + vector.y, z + vector.z)
    }

    fun subtract(vector: Vector): Vector {
        return Vector(x - vector.x, y - vector.y, z - vector.z)
    }

    fun multiply(vector: Vector): Vector {
        return Vector(x * vector.x, y * vector.y, z * vector.z)
    }

    fun divide(vector: Vector): Vector {
        return Vector(x / vector.x, y / vector.y, z / vector.z)
    }

    fun dotProduct(vector: Vector): Double {
        return x * vector.x + y * vector.y + z * vector.z
    }

    fun crossProduct(vector: Vector): Vector {
        val x = y * vector.z - z * vector.y
        val y = z * vector.x - x * vector.z
        val z = x * vector.y - y * vector.x
        return Vector(x, y, z)
    }

    fun magnitude(): Double {
        return sqrt((x * x + y * y + z * z).toDouble())
    }

    fun normalize(): Vector {
        val magnitude = magnitude()
        return Vector(x / magnitude.toInt(), y / magnitude.toInt(), z / magnitude.toInt())
    }

    fun angle(vector: Vector): Double {
        val dotProduct = dotProduct(vector)
        val magnitude = magnitude() * vector.magnitude()
        return acos(dotProduct / magnitude)
    }

    fun isParallel(vector: Vector): Boolean {
        return crossProduct(vector).magnitude() == 0.0
    }

    fun isOrthogonal(vector: Vector): Boolean {
        return dotProduct(vector) == 0.0
    }

    fun isCollinear(vector: Vector): Boolean {
        return isParallel(vector) && dotProduct(vector) > 0
    }

    fun isOpposite(vector: Vector): Boolean {
        return isParallel(vector) && dotProduct(vector) < 0
    }

    fun distance(vector: Vector): Double {
        return sqrt((vector.x - x).pow(2.0) + (vector.y - y).pow(2.0) + (vector.z - z).pow(2.0))
    }

    override fun toString(): String {
        return "Vector(x=$x, y=$y, z=$z)"
    }
}