package com.anatawa12.mcWrapper.internal.mc1710;

import com.anatawa12.mcWrapper.internal.WTileEntityImpl;
import com.anatawa12.mcWrapper.v2.WNBTCompound;
import com.anatawa12.mcWrapper.v2.WTileEntity;
import com.anatawa12.mcWrapper.v2._InternalAccessor;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

public final class WTileEntityImplImpl implements WTileEntityImpl {
    @Override
    public TileEntity createTileFromNBT(WNBTCompound nbt) {
        return TileEntity.func_145827_c(_InternalAccessor.getReal(nbt));
    }

    @Override
    public WNBTCompound getWNBTCompoundByTile(TileEntity tile) {
        WNBTCompound compound = new WNBTCompound();
        tile.func_145841_b(_InternalAccessor.getReal(compound));
        return compound;
    }

    @Override
    public int getX(TileEntity tile) {
        return tile.field_145851_c;
    }

    @Override
    public int getY(TileEntity tile) {
        return tile.field_145848_d;
    }

    @Override
    public int getZ(TileEntity tile) {
        return tile.field_145849_e;
    }

    @Override
    public String getTileEntityName(TileEntity tile) {
        return getClassToNameMap().get(tile.getClass());
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

    // classToNameMap
    private static final Field field_145853_j_field;

    private Map<Class<?>, String> getClassToNameMap() {
        try {
            //noinspection unchecked
            return (Map<Class<?>, String>) field_145853_j_field.get(null);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    static {
        try {
            field_145853_j_field = TileEntity.class.getDeclaredField("field_145853_j");
            field_145853_j_field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    private static WeakHashMap<TileEntity, WTileEntity> wTileEntity = new WeakHashMap<>();
}
