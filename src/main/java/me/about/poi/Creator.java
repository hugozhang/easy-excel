package me.about.poi;

public class Creator {

    public static <T> T of(Class<T> class1) {
        try {
            return class1.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate object of type " + class1.getCanonicalName(), e);
        }
    }
}
