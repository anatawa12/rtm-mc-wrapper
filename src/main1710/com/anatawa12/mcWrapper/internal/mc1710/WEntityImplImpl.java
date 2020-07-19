package com.anatawa12.mcWrapper.internal.mc1710;

import com.anatawa12.mcWrapper.internal.WEntityImpl;
import rmw2.WNBTCompound;
import rmw2._InternalAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public final class WEntityImplImpl implements WEntityImpl {
    @Override
    public WNBTCompound getWNBTCompoundByEntity(Entity entity) {
        WNBTCompound compound = new WNBTCompound();
        entity.func_70109_d(_InternalAccessor.getReal(compound));
        compound.set("id", EntityList.func_75621_b(entity));
        return compound;
    }
}
