package com.anatawa12.mcWrapper.internal.mc1710;

import com.anatawa12.mcWrapper.internal.WWorldImpl;
import rmw2.WBlock;
import rmw2.WTileEntity;
import rmw2.WWorld;
import rmw2._InternalAccessor;
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
        return new WBlock(world.func_147439_a(x, y, z), world.func_72805_g(x, y, z), null);
    }

    @Override
    public boolean setBlock(World world, int x, int y, int z, WBlock block, int flags) {
        return world.func_147465_d(x, y, z, _InternalAccessor.getRealBlock(block), block.getMeta(), flags);
    }

    @Override
    public WTileEntity getTileEntity(World world, int x, int y, int z) {
        return WTileEntity.wrap(world.func_147438_o(x, y, z));
    }

    @Override
    public void setTileEntity(World world, int x, int y, int z, WTileEntity tile) {
        world.func_147455_a(x, y, z, _InternalAccessor.getRealTile(tile));
    }

    private static WeakHashMap<World, WWorld> wWorldMap = new WeakHashMap<>();
}
