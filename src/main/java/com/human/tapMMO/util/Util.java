package com.human.tapMMO.util;

public abstract class Util {

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max + min) - min);
    }

    public static int randomInt(int max) {
        return (int) (Math.random() * max);
    }
}
