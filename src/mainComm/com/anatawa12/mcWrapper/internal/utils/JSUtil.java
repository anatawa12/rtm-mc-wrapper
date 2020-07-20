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
    public static Function<Object, Object> WEntityWrapFunc;
    public static Function<Object, Object> WTileEntityWrapFunc;

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

    public static void checkInitialized() {
        if (JSUtil.isArray == null) throw new IllegalStateException("isArray is not initialized");
        if (JSUtil.arrayGetter == null) throw new IllegalStateException("arrayGetter is not initialized");
        if (JSUtil.arraySetter == null) throw new IllegalStateException("arraySetter is not initialized");
        if (JSUtil.typeof == null) throw new IllegalStateException("typeof is not initialized");
        if (JSUtil.keys == null) throw new IllegalStateException("keys is not initialized");
        if (JSUtil.WEntityWrapFunc == null) throw new IllegalStateException("WEntityWrapFunc is not initialized");
        if (JSUtil.WTileEntityWrapFunc == null) throw new IllegalStateException("WTileEntityWrapFunc is not initialized");
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

    public static void setWEntityWrapFunc(Function<Object, Object> wEntityWrapFunc) {
        JSUtil.WEntityWrapFunc = wEntityWrapFunc;
    }

    public static void setWTileEntityWrapFunc(Function<Object, Object> wTileEntityWrapFunc) {
        JSUtil.WTileEntityWrapFunc = wTileEntityWrapFunc;
    }
}
