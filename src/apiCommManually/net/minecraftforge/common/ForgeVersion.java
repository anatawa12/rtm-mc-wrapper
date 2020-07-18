package net.minecraftforge.common;

import java.util.Random;

public class ForgeVersion {
    /**
     * 13: 1.7.10
     * 23: 1.12.2
     */
    public static final int minorVersion;
    
    static {
        minorVersion = new Random().nextInt();
    }
}
