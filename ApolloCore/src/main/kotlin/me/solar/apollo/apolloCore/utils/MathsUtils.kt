@file:JvmName("MathsUtils")
package me.solar.apollo.apolloCore.utils

import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sqrt

fun add(a: Int, b: Int): Int {
    return a + b
}

fun subtract(a: Int, b: Int): Int {
    return a - b
}

fun multiply(a: Int, b: Int): Int {
    return a * b
}

fun divide(a: Int, b: Int): Int {
    return a / b
}

fun getRomanNumerals(): Map<Int, String> {
    return mapOf(
        1 to "I",
        2 to "II",
        3 to "III",
        4 to "IV",
        5 to "V",
        6 to "VI",
        7 to "VII",
        8 to "VIII",
        9 to "IX",
        10 to "X",
        40 to "XL",
        50 to "L",
        90 to "XC",
        100 to "C",
        400 to "CD",
        500 to "D",
        900 to "CM",
        1000 to "M"
    )
}

fun convertToRomanNumeral(number: Int): String {
    val romanNumerals = getRomanNumerals()
    var remainingNumber = number
    var result = ""
    while (remainingNumber > 0) {
        val highestValue = romanNumerals.keys.last { it <= remainingNumber }
        result += romanNumerals[highestValue]
        remainingNumber -= highestValue
    }
    return result
}

fun max(a: Array<Int>) : Int {
    var max = a[0]
    for (i in 1 until a.size) {
        if (a[i] > max) {
            max = a[i]
        }
    }
    return max;
}

fun min(a: Array<Int>) : Int {
    var min = a[0]
    for (i in 1 until a.size) {
        if (a[i] < min) {
            min = a[i]
        }
    }
    return min;
}

fun floor(a: Double) : Double {
    return floor(a);
}

fun ceil(a: Double) : Double {
    return ceil(a);
}

fun round(a: Double) : Long {
    return round(a).toLong();
}

fun abs(a: Double) : Double {
    return abs(a);
}

fun inRange(a: Int, b: Int, c: Int) : Boolean {
    return a in b..c;
}

fun inRange(a: Double, b: Double, c: Double) : Boolean {
    return a in b..c;
}

fun increasePercentage(a: Double, b: Double) : Double {
    return a + percentage(a, b);
}

fun decreasePercentage(a: Double, b: Double) : Double {
    return a - percentage(a, b);
}

fun percentage(a: Double, b: Double) : Double {
    return (a * b / 100);
}

fun percentageChange(a: Double, b: Double) : Double {
    return ((b - a) / a) * 100;
}

fun average(a: Array<Double>) : Double {
    var sum = 0.0;
    for (i in 0 until a.size) {
        sum += a[i];
    }
    return sum / a.size;
}

fun median(a: Array<Double>) : Double {
    a.sort();
    if (a.size % 2 == 0) {
        return (a[a.size / 2] + a[a.size / 2 - 1]) / 2;
    } else {
        return a[a.size / 2];
    }
}

fun mode(a: Array<Double>) : Double {
    var mode = a[0];
    var maxCount = 0;
    for (i in 0 until a.size) {
        var count = 0;
        for (j in 0 until a.size) {
            if (a[j] == a[i]) {
                count++;
            }
        }
        if (count > maxCount) {
            maxCount = count;
            mode = a[i];
        }
    }
    return mode;
}

fun factorial(a: Int) : Int {
    var result = 1;
    for (i in 1..a) {
        result *= i;
    }
    return result;
}

fun fibonacci(a: Int) : Int {
    if (a <= 1) {
        return a;
    }
    return fibonacci(a - 1) + fibonacci(a - 2);
}

fun isPrime(a: Int) : Boolean {
    if (a <= 1) {
        return false;
    }
    for (i in 2..a / 2) {
        if (a % i == 0) {
            return false;
        }
    }
    return true;
}

fun isEven(a: Int) : Boolean {
    return a % 2 == 0;
}

fun isOdd(a: Int) : Boolean {
    return a % 2 != 0;
}

fun isPositive(a: Int) : Boolean {
    return a > 0;
}

fun isNegative(a: Int) : Boolean {
    return a < 0;
}

fun isZero(a: Int) : Boolean {
    return a == 0;
}

fun isDivisibleBy(a: Int, b: Int) : Boolean {
    return a % b == 0;
}

fun isPerfectSquare(a: Int) : Boolean {
    return sqrt(a.toDouble()) % 1 == 0.0;
}

fun isPerfectCube(a: Int) : Boolean {
    return cbrt(a.toDouble()) % 1 == 0.0;
}

fun isPowerOfTwo(a: Int) : Boolean {
    return a != 0 && (a and a - 1) == 0;
}


