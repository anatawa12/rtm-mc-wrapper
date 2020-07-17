package com.anatawa12.mcWrapper.v2;

import net.minecraft.nbt.NBTTagByteArray;

public final class WNBTByteArray extends WNBTBase<NBTTagByteArray> {
    public WNBTByteArray(NBTTagByteArray real) {
        super(real);
    }

    public WNBTByteArray(byte[] real) {
        super(new NBTTagByteArray(real));
    }

    public byte[] getByteArray() {
        return real.func_150292_c();
    }
}
