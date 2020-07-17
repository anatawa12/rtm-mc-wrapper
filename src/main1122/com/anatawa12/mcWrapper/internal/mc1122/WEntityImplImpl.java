package com.anatawa12.mcWrapper.internal.mc1122;

import com.anatawa12.mcWrapper.internal.WEntityImpl;
import com.anatawa12.mcWrapper.v2.WNBTCompound;
import com.anatawa12.mcWrapper.v2._InternalAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public final class WEntityImplImpl implements WEntityImpl {
    @Override
    public WNBTCompound getWNBTCompoundByEntity(Entity entity) {
        WNBTCompound compound = new WNBTCompound();
        entity.func_189511_e(_InternalAccessor.getReal(compound));
        compound.set("id", EntityList.func_191301_a(entity).toString());
        return compound;
    }
}
