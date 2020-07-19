package com.anatawa12.mcWrapper.internal;

import rmw2.WBlock;
import rmw2.WTileEntity;
import rmw2.WWorld;
import net.minecraft.world.World;

public interface WWorldImpl {
    WBlock getBlock(World world, int x, int y, int z);
    boolean setBlock(World world, int x, int y, int z, WBlock block, int flags);
    WTileEntity getTileEntity(World world, int x, int y, int z);
    void setTileEntity(World world, int x, int y, int z, WTileEntity tile);
    WWorld wrap(World world);
}
