package com.anatawa12.mcWrapper.internal.mc1122;

import com.anatawa12.mcWrapper.internal.WTileEntityImpl;
import com.anatawa12.mcWrapper.v1.WNBTCompound;
import com.anatawa12.mcWrapper.v1.WTileEntity;
import com.anatawa12.mcWrapper.v1._InternalAccessor;
import net.minecraft.tileentity.TileEntity;

import java.util.WeakHashMap;

public final class WTileEntityImplImpl implements WTileEntityImpl {
    @Override
    public TileEntity createTileFromNBT(WNBTCompound nbt) {
        return TileEntity.func_190200_a(null, _InternalAccessor.getReal(nbt));
    }

    @Override
    public WNBTCompound getWNBTCompoundByTile(TileEntity tile) {
        WNBTCompound compound = new WNBTCompound();
        tile.func_189515_b(_InternalAccessor.getReal(compound));
        return compound;
    }

    @Override
    public int getX(TileEntity tile) {
        return tile.func_174877_v().func_177958_n();
    }

    @Override
    public int getY(TileEntity tile) {
        return tile.func_174877_v().func_177956_o();
    }

    @Override
    public int getZ(TileEntity tile) {
        return tile.func_174877_v().func_177952_p();
    }

    @Override
    public String getTileEntityName(TileEntity tile) {
        return TileEntity.func_190559_a(tile.getClass()).toString();
    }

    @Override
    public WTileEntity wrap(TileEntity entity) {
        WTileEntity impl = wTileEntity.get(entity);
        if (impl == null) {
            impl = new WTileEntity(entity);
            wTileEntity.put(entity, impl);
        }
        return impl;
    }

    private static WeakHashMap<TileEntity, WTileEntity> wTileEntity = new WeakHashMap<>();
}
