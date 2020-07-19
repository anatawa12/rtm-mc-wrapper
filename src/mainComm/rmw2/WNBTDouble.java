package rmw2;

import net.minecraft.nbt.NBTTagDouble;

public final class WNBTDouble extends WNBTPrimitive<NBTTagDouble> {
    public WNBTDouble(NBTTagDouble real) {
        super(real);
    }

    public WNBTDouble(double value) {
        super(new NBTTagDouble(value));
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
