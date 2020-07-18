package com.anatawa12.mcWrapper.v2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import net.minecraft.nbt.*;

public abstract class WNBTBase<RealNBT extends NBTBase> {
    protected final RealNBT real;

    protected WNBTBase(RealNBT real) {
        this.real = real;
    }

    static WNBTBase<?> wrap(NBTBase param) {
        if (param instanceof NBTTagByte) {
            return new WNBTByte((NBTTagByte)param);
        } else if (param instanceof NBTTagShort) {
            return new WNBTShort((NBTTagShort)param);
        } else if (param instanceof NBTTagInt) {
            return new WNBTInt((NBTTagInt)param);
        } else if (param instanceof NBTTagLong) {
            return new WNBTLong((NBTTagLong)param);
        } else if (param instanceof NBTTagFloat) {
            return new WNBTFloat((NBTTagFloat)param);
        } else if (param instanceof NBTTagDouble) {
            return new WNBTDouble((NBTTagDouble)param);
        } else if (param instanceof NBTTagByteArray) {
            return new WNBTByteArray((NBTTagByteArray)param);
        } else if (param instanceof NBTTagString) {
            return new WNBTString((NBTTagString)param);
        } else if (param instanceof NBTTagList) {
            return new WNBTList((NBTTagList)param);
        } else if (param instanceof NBTTagCompound) {
            return new WNBTCompound((NBTTagCompound)param);
        } else if (param instanceof NBTTagIntArray) {
            return new WNBTIntArray((NBTTagIntArray)param);
        } else {
            return null;
        }
    }

    static NBTBase unwrap(Object param) {
        if (param == null) throw new IllegalArgumentException("can't unwrap null");
        if (param instanceof NBTBase) {
            return (NBTBase) param;
        } else if (JSUtil.isArray(param)) {
            if (Integer.valueOf(0).equals(JSUtil.get(param, "length"))
                    || JSUtil.typeof(param).equals("number"))
                return new WNBTIntArray(makeIntArray(param)).real;
            else
                return new WNBTList(param).real;
        }
        String typeof = JSUtil.typeof(param);
        switch (typeof) {
            case "object":
                return new WNBTCompound(param).real;
            case "number":
                return new WNBTDouble(((Number) param).doubleValue()).real;
            case "string":
                return new WNBTString(param.toString()).real;
            case "undefined":
            case "boolean":
            case "function":
            case "symbol":
            case "bigint":
            default:
                throw new IllegalArgumentException("can't unwrap " + typeof);
        }
    }

    private static int[] makeIntArray(Object array) {
        int length = (int) JSUtil.get(array, "length");
        int[] list = new int[length];
        for (int i = 0; i < length; i++) {
            list[i] = ((Number)JSUtil.get(array, i)).intValue();
        }
        return list;
    }

    public String getString() {
        return real.toString();
    }

    @Override
    public String toString() {
        return real.toString();
    }
}
