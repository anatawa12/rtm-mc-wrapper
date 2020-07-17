package com.anatawa12.mcWrapper.v2;

import com.anatawa12.mcWrapper.internal.McWrapper;
import com.anatawa12.mcWrapper.internal.WWorldImpl;
import net.minecraft.world.World;

import java.util.Objects;

public final class WWorld {
    private static final WWorldImpl impl = McWrapper.getVersionedFactory("WWorldImplImpl");

    /*internal*/ final World world;

    public WWorld(World world) {
        Objects.requireNonNull(world, "world");
        this.world = world;
    }

    public WBlock getBlock(int x, int y, int z) {
        return impl.getBlock(world, x, y, z);
    }

    public boolean setBlock(int x, int y, int z, Object block, int flags) {
        return impl.setBlock(world, x, y, z, WBlock.create(block), flags);
    }

    public boolean setBlock(int x, int y, int z, Object block) {
        return setBlock(x, y, z, block, 3);
    }

    public WTileEntity getTileEntity(int x, int y, int z) {
        return impl.getTileEntity(world, x, y, z);
    }

    public void setTileEntity(int x, int y, int z, WTileEntity tile) {
        impl.setTileEntity(world, x, y, z, tile);
    }

    public static WWorld wrap(World world) {
        return impl.wrap(world); 
    }
}
