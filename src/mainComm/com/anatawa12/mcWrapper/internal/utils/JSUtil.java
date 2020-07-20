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
        JSUtil.checkInitialized();
        Object value = arrayGetter.apply(object, name);
        if (value == null) return null; 
        if (!clazz.isInstance(value))
            throw new IllegalArgumentException(name + " must be " + clazz.getSimpleName());
        return clazz.cast(value);
    }

    public static <T> T get(Object object, String name, Class<T> clazz, T defaultValue) {
        JSUtil.checkInitialized();
        Object value = arrayGetter.apply(object, name);
        if (!clazz.isInstance(value)) return defaultValue;
        return clazz.cast(value);
    }

    public static Object get(Object object, Object key) {
        JSUtil.checkInitialized();
        return arrayGetter.apply(object, key);
    }

    public static void set(Object object, Object key, Object value) {
        JSUtil.checkInitialized();
        arraySetter.apply(object, key, value);
    }

    public static boolean isArray(Object object) {
        JSUtil.checkInitialized();
        return isArray.test(object);
    }

    public static String typeof(Object object) {
        JSUtil.checkInitialized();
        return typeof.apply(object);
    }

    public static Object[] keys(Object object) {
        JSUtil.checkInitialized();
        return keys.keys(object);
    }

    public static void checkInitialized() {
        if (JSUtil.isArray == null 
                || JSUtil.arrayGetter == null 
                || JSUtil.arraySetter == null 
                || JSUtil.typeof == null 
                || JSUtil.keys == null 
                || JSUtil.WEntityWrapFunc == null 
                || JSUtil.WTileEntityWrapFunc == null
        ) {
            throw new IllegalStateException("Packages.rmw2.Init.init(eval) is not called. " +
                    "please call it before use rmw2.");
        }
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
