package me.solar.apollo.apolloCore.utils;

public class RandomUtils {

    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static int getRandomInt(int max) {
        return getRandomInt(0, max);
    }

    public static double getRandomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }


}
