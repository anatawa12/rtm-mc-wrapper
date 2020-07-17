package com.anatawa12.mcWrapper.internal.mc1122;

import com.anatawa12.mcWrapper.internal.InternalSignature;
import com.anatawa12.mcWrapper.internal.WWorldImpl;
import com.anatawa12.mcWrapper.v1.WBlock;
import com.anatawa12.mcWrapper.v1.WTileEntity;
import com.anatawa12.mcWrapper.v1.WWorld;
import com.anatawa12.mcWrapper.v1._InternalAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.WeakHashMap;

public final class WWorldImplImpl implements WWorldImpl {
    @Override
    public WWorld wrap(World world) {
        WWorld impl = wWorldMap.get(world);
        if (impl == null) {
            impl = new WWorld(world);
            wWorldMap.put(world, impl);
        }
        return impl;
    }

    @Override
    public WBlock getBlock(World world, int x, int y, int z) {
        IBlockState state = world.func_180495_p(new BlockPos(x, y, z));
        return new WBlock(state, null);
    }

    @Override
    public boolean setBlock(World world, int x, int y, int z, WBlock block, int flags) {
        return world.func_180501_a(new BlockPos(x, y, z), (IBlockState) block.makeBlockState(), flags);
    }

    @Override
    public WTileEntity getTileEntity(World world, int x, int y, int z) {
        return WTileEntity.wrap(world.func_175625_s(new BlockPos(x, y, z)));
    }

    @Override
    public void setTileEntity(World world, int x, int y, int z, WTileEntity tile) {
        world.func_175690_a(new BlockPos(x, y, z), _InternalAccessor.getRealTile(tile));
    }

    private static WeakHashMap<World, WWorld> wWorldMap = new WeakHashMap<>();
}
