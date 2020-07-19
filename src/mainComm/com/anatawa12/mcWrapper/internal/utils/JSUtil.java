package com.anatawa12.mcWrapper.internal.utils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class JSUtil {
    private static Predicate<Object> isArray;
    private static BiFunction<Object, Object, Object> arrayGetter;
    private static ArraySetter arraySetter;
    private static Function<Object, String> typeof;
    private static KeysGetter keys;

    public static <T> T requireInstanceOf(Object object, String name, Class<T> clazz) {
        Object value = arrayGetter.apply(object, name);
        if (value == null) return null; 
        if (!clazz.isInstance(value))
            throw new IllegalArgumentException(name + " must be " + clazz.getSimpleName());
        return clazz.cast(value);
    }

    public static <T> T get(Object object, String name, Class<T> clazz, T defaultValue) {
        Object value = arrayGetter.apply(object, name);
        if (!clazz.isInstance(value)) return defaultValue;
        return clazz.cast(value);
    }

    public static Object get(Object object, Object key) {
        return arrayGetter.apply(object, key);
    }

    public static void set(Object object, Object key, Object value) {
        arraySetter.apply(object, key, value);
    }

    public static boolean isArray(Object object) {
        return isArray.test(object);
    }

    public static String typeof(Object object) {
        return typeof.apply(object);
    }

    public static Object[] keys(Object object) {
        return keys.keys(object);
    }

    // setters called by js

    public static void setIsArray(Predicate<Object> isArray) {
        JSUtil.isArray = isArray;
    }

    public static void setArrayGetter(BiFunction<Object, Object, Object> arrayGetter) {
        JSUtil.arrayGetter = arrayGetter;
    }

    public static void setArraySetter(ArraySetter arrayGetter) {
        JSUtil.arraySetter = arrayGetter;
    }

    public static void setTypeof(Function<Object, String> arrayGetter) {
        JSUtil.typeof = arrayGetter;
    }

    public static void setKeys(KeysGetter keys) {
        JSUtil.keys = keys;
    }
}
