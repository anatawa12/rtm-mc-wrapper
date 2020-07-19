package com.anatawa12.mcWrapper.internal.mc1122;

import com.anatawa12.mcWrapper.internal.WEntityImpl;
import rmw2.WNBTCompound;
import rmw2.WEntity;
import rmw2._InternalAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import java.util.WeakHashMap;

public final class WEntityImplImpl implements WEntityImpl {
    @Override
    public WNBTCompound getWNBTCompoundByEntity(Entity entity) {
        WNBTCompound compound = new WNBTCompound();
        entity.func_189511_e(_InternalAccessor.getReal(compound));
        compound.set("id", EntityList.func_191301_a(entity).toString());
        return compound;
    }

    @Override
    public WEntity wrap(Entity entity) {
        WEntity impl = wEntity.get(entity);
        if (impl == null) {
            impl = new WEntity(entity);
            wEntity.put(entity, impl);
        }
        return impl;
    }

    private static WeakHashMap<Entity, WEntity> wEntity = new WeakHashMap<>();
}
