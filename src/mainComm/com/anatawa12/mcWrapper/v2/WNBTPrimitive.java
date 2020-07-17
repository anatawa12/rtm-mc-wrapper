package com.anatawa12.mcWrapper.v2;

import net.minecraft.nbt.NBTBase;

public abstract class WNBTPrimitive<RealNBT extends NBTBase> extends WNBTBase<RealNBT> {
    public WNBTPrimitive(RealNBT real) {
        super(real);
    }

    public abstract long asLong();

    public abstract int asInt();

    public abstract short asShort();

    public abstract byte asByte();

    public abstract double asDouble();

    public abstract float asFloat();
}
