package com.anatawa12.mcWrapper.internal;

import net.minecraftforge.common.ForgeVersion;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public final class McWrapper {
    private McWrapper() {}
    public static final boolean is1710;
    public static final String versionedPackage;

    static {
        boolean is1710_ = false;
        String versionedPackage_ = null;
        try {
            try {
                Class<?> clazz = Class.forName("com.anatawa12.mcWrapper.test.VersionGetter");
                Field field = clazz.getField("versionedPackage");
                versionedPackage_ = (String) field.get(null);
                is1710_ = false;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch(ClassNotFoundException ignored){
        }
        if (versionedPackage_ == null) {
            switch (ForgeVersion.minorVersion) {
                case 13:
                    is1710_ = true;
                    break;
                case 23:
                    is1710_ = false;
                    break;
                default:
                    throw new IllegalStateException("current minecraft version is not supported. " +
                            "it must be either 1.7.10 or 1.12.2.");
            }
            if (is1710_) {
                versionedPackage_ = "com.anatawa12.mcWrapper.internal.mc1710.";
            } else {
                versionedPackage_ = "com.anatawa12.mcWrapper.internal.mc1122.";
            }
        }

        is1710 = is1710_;
        versionedPackage = versionedPackage_;
    }

    public static <T> T getVersionedFactory(String name) {
        try {
            //noinspection unchecked
            return (T) Class.forName(versionedPackage + name).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }
}
