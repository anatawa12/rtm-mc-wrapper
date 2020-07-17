package com.anatawa12.mcWrapper.internal;

import com.anatawa12.mcWrapper.v2.WNBTCompound;
import com.anatawa12.mcWrapper.v2.WTileEntity;
import net.minecraft.tileentity.TileEntity;

public interface WTileEntityImpl {
    TileEntity createTileFromNBT(WNBTCompound nbt);

    WNBTCompound getWNBTCompoundByTile(TileEntity tile);

    int getX(TileEntity tile);

    int getY(TileEntity tile);

    int getZ(TileEntity tile);

    String getTileEntityName(TileEntity tile);

    WTileEntity wrap(TileEntity entity);
}
