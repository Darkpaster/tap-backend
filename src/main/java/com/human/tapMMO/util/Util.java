package com.human.tapMMO.util;

public abstract class Util {

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max + min) - min);
    }

    public static int randomInt(int max) {
        return (int) (Math.random() * max);
    }


    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static float getDistanceX(float x1, float x2) {
        return Math.abs(Math.abs(x1) - Math.abs(x2));
    }

    public static float getDistanceY(float y1, float y2) {
        return Math.abs(Math.abs(y1) - Math.abs(y2));
    }
}
