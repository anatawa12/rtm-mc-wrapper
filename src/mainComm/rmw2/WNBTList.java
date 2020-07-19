package rmw2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import net.minecraft.nbt.NBTTagList;

public final class WNBTList extends WNBTBase<NBTTagList> {
    public WNBTList(NBTTagList real) {
        super(real);
    }
    public WNBTList(Object array) {
        super(makeListFromArray(array));
    }

    private static NBTTagList makeListFromArray(Object array) {
        if (!JSUtil.isArray(array))
            throw new IllegalArgumentException("parameter of WNBTList must be array");
        NBTTagList list = new NBTTagList();
        int length = (int) JSUtil.get(array, "length");
        for (int i = 0; i < length; i++) {
            list.func_74742_a(unwrap(JSUtil.get(array, i)));
        }
        return list;
    }

    public WNBTCompound getCompoundAt(int index) {
        return new WNBTCompound(real.func_150305_b(index));
    }

    public int[] getIntArrayAt(int index) {
        return real.func_150306_c(index);
    }

    public double getDoubleAt(int index) {
        return real.func_150309_d(index);
    }

    public float getFloatAt(int index) {
        return real.func_150308_e(index);
    }

    public String getStringAt(int index) {
        return real.func_150307_f(index);
    }

    public void set(int index, Object value) {
        real.func_150304_a(index, unwrap(value));
    }

    public void add(Object value) {
        real.func_74742_a(unwrap(value));
    }

    @SuppressWarnings("rawtypes")
    public WNBTBase remove(int index) {
        return wrap(real.func_74744_a(index));
    }

    public int getSize() {
        return real.func_74745_c();
    }

    
}
