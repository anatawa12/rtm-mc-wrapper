package com.anatawa12.mcWrapper.v2;

import net.minecraft.nbt.NBTTagInt;

public final class WNBTInt extends WNBTPrimitive<NBTTagInt> {
    public WNBTInt(NBTTagInt real) {
        super(real);
    }

    public WNBTInt(int value) {
        super(new NBTTagInt(value));
    }

    @Override
    public long asLong() {
        return real.func_150291_c();
    }

    @Override
    public int asInt() {
        return real.func_150287_d();
    }

    @Override
    public short asShort() {
        return real.func_150289_e();
    }

    @Override
    public byte asByte() {
        return real.func_150290_f();
    }

    @Override
    public double asDouble() {
        return real.func_150286_g();
    }

    @Override
    public float asFloat() {
        return real.func_150288_h();
    }
}
