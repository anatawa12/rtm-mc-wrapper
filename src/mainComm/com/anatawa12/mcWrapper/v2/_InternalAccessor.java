package com.anatawa12.mcWrapper.v2;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;

public final class _InternalAccessor {
    public static <T extends NBTBase> T getReal(WNBTBase<T> base) {
        return base.real;
    }

    public static Block getRealBlock(WBlock base) {
        return base.block;
    }

    public static TileEntity getRealTile(WTileEntity tile) {
        return tile.tile;
    }
}
