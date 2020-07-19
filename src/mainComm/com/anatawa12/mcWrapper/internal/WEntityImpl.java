package com.anatawa12.mcWrapper.internal;

import rmw2.WNBTCompound;
import net.minecraft.entity.Entity;

public interface WEntityImpl {
    WNBTCompound getWNBTCompoundByEntity(Entity entity);
}
