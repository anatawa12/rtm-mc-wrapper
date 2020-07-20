package com.anatawa12.mcWrapper.test.impl;

import com.anatawa12.mcWrapper.internal.WEntityImpl;
import net.minecraft.entity.Entity;
import rmw2.WEntity;
import rmw2.WNBTCompound;

public class WEntityImplImpl implements WEntityImpl {
    @Override
    public WNBTCompound getWNBTCompoundByEntity(Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WEntity wrap(Entity entity) {
        return new WEntity(entity);
    }
}
