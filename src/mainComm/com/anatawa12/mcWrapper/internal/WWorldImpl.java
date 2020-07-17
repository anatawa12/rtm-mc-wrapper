package com.anatawa12.mcWrapper.internal;

import com.anatawa12.mcWrapper.v2.WBlock;
import com.anatawa12.mcWrapper.v2.WTileEntity;
import com.anatawa12.mcWrapper.v2.WWorld;
import net.minecraft.world.World;

public interface WWorldImpl {
    WBlock getBlock(World world, int x, int y, int z);
    boolean setBlock(World world, int x, int y, int z, WBlock block, int flags);
    WTileEntity getTileEntity(World world, int x, int y, int z);
    void setTileEntity(World world, int x, int y, int z, WTileEntity tile);
    WWorld wrap(World world);
}
