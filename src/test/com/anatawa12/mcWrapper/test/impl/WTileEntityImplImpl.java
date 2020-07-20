package com.anatawa12.mcWrapper.test.impl;

import com.anatawa12.mcWrapper.internal.WTileEntityImpl;
import net.minecraft.tileentity.TileEntity;
import rmw2.WNBTCompound;
import rmw2.WTileEntity;

public class WTileEntityImplImpl implements WTileEntityImpl {
    @Override
    public TileEntity createTileFromNBT(WNBTCompound nbt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WNBTCompound getWNBTCompoundByTile(TileEntity tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getX(TileEntity tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getY(TileEntity tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getZ(TileEntity tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTileEntityName(TileEntity tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WTileEntity wrap(TileEntity entity) {
        return new WTileEntity(entity);
    }
}
