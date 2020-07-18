package com.anatawa12.mcWrapper.internal;

import net.minecraftforge.common.ForgeVersion;

import java.util.Arrays;
import java.util.Map;

public final class McWrapper {
    private McWrapper() {}
    public static boolean is1710;
    public static String versionedPackage;

    static {
        switch (ForgeVersion.minorVersion) {
            case 13:
                is1710 = true;
                break;
            case 23:
                is1710 = false;
                break;
            default:
                throw new IllegalStateException("current minecraft version is not supported. " +
                        "it must be either 1.7.10 or 1.12.2.");
        }
        if (is1710) {
            versionedPackage = "com.anatawa12.mcWrapper.internal.mc1710.";
        } else {
            versionedPackage = "com.anatawa12.mcWrapper.internal.mc1122.";
        }
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
