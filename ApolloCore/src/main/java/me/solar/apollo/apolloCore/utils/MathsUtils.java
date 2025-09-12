package me.solar.apollo.apolloCore.utils;

import java.util.*;

public final class MathsUtils {

    private MathsUtils() {}

    public static int add(int a, int b) { return a + b; }
    public static int subtract(int a, int b) { return a - b; }
    public static int multiply(int a, int b) { return a * b; }
    public static int divide(int a, int b) { return a / b; }

    public static Map<Integer, String> getRomanNumerals() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "I"); map.put(2, "II"); map.put(3, "III"); map.put(4, "IV"); map.put(5, "V");
        map.put(6, "VI"); map.put(7, "VII"); map.put(8, "VIII"); map.put(9, "IX"); map.put(10, "X");
        map.put(40, "XL"); map.put(50, "L"); map.put(90, "XC"); map.put(100, "C"); map.put(400, "CD");
        map.put(500, "D"); map.put(900, "CM"); map.put(1000, "M");
        return map;
    }

    public static String convertToRomanNumeral(int number) {
        Map<Integer, String> romanNumerals = getRomanNumerals();
        int remainingNumber = number;
        StringBuilder result = new StringBuilder();
        List<Integer> keys = new ArrayList<>(romanNumerals.keySet());
        Collections.sort(keys, Collections.reverseOrder());
        for (int key : keys) {
            while (remainingNumber >= key) {
                result.append(romanNumerals.get(key));
                remainingNumber -= key;
            }
        }
        return result.toString();
    }

    public static int max(int[] a) {
        int max = a[0];
        for (int i = 1; i < a.length; i++) if (a[i] > max) max = a[i];
        return max;
    }

    public static int min(int[] a) {
        int min = a[0];
        for (int i = 1; i < a.length; i++) if (a[i] < min) min = a[i];
        return min;
    }

    public static double floor(double a) { return Math.floor(a); }
    public static double ceil(double a) { return Math.ceil(a); }
    public static long round(double a) { return Math.round(a); }
    public static double abs(double a) { return Math.abs(a); }
    public static boolean inRange(int a, int b, int c) { return a >= b && a <= c; }
    public static boolean inRange(double a, double b, double c) { return a >= b && a <= c; }
    public static double increasePercentage(double a, double b) { return a + percentage(a, b); }
    public static double decreasePercentage(double a, double b) { return a - percentage(a, b); }
    public static double percentage(double a, double b) { return a * b / 100; }
    public static double percentageChange(double a, double b) { return ((b - a) / a) * 100; }
    public static double average(double[] a) {
        double sum = 0.0;
        for (double v : a) sum += v;
        return sum / a.length;
    }
    public static double median(double[] a) {
        double[] copy = Arrays.copyOf(a, a.length);
        Arrays.sort(copy);
        if (copy.length % 2 == 0)
            return (copy[copy.length / 2] + copy[copy.length / 2 - 1]) / 2;
        else
            return copy[copy.length / 2];
    }
    public static double mode(double[] a) {
        Map<Double, Integer> freq = new HashMap<>();
        for (double v : a) freq.put(v, freq.getOrDefault(v, 0) + 1);
        double mode = a[0];
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : freq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }
    public static int factorial(int a) {
        int result = 1;
        for (int i = 1; i <= a; i++) result *= i;
        return result;
    }
    public static int fibonacci(int a) {
        if (a <= 1) return a;
        return fibonacci(a - 1) + fibonacci(a - 2);
    }
    public static boolean isPrime(int a) {
        if (a <= 1) return false;
        for (int i = 2; i <= a / 2; i++) if (a % i == 0) return false;
        return true;
    }
    public static boolean isEven(int a) { return a % 2 == 0; }
    public static boolean isOdd(int a) { return a % 2 != 0; }
    public static boolean isPositive(int a) { return a > 0; }
    public static boolean isNegative(int a) { return a < 0; }
    public static boolean isZero(int a) { return a == 0; }
    public static boolean isDivisibleBy(int a, int b) { return a % b == 0; }
    public static boolean isPerfectSquare(int a) { return Math.sqrt(a) % 1 == 0.0; }
    public static boolean isPerfectCube(int a) { return Math.cbrt(a) % 1 == 0.0; }
    public static boolean isPowerOfTwo(int a) { return a != 0 && (a & (a - 1)) == 0; }
}

