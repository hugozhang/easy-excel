package com.github.xutils.poi;

public class Creator<T> {

    private Class<T> class1;

    Creator(Class<T> class1) {
        this.class1 = class1;
    }

    public T get() {
        try {
            return class1.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate object of type " + class1.getCanonicalName(), e);
        }
    }

}
