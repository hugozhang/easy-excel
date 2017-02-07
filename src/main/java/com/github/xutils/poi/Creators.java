package com.github.xutils.poi;

public class Creators {

    public static <T> Creator<T> of(Class<T> class1) {
        return new Creator<T>(class1);
    }
}
