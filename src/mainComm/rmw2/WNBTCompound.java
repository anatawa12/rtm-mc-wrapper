package rmw2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import net.minecraft.nbt.NBTTagCompound;

public final class WNBTCompound extends WNBTBase<NBTTagCompound> {
    public WNBTCompound(Object real) {
        super(convertAll(real));
    }

    public WNBTCompound(NBTTagCompound real) {
        super(real);
    }

    public WNBTCompound() {
        super(new NBTTagCompound());
    }

    private static NBTTagCompound convertAll(Object real) {
        NBTTagCompound compound = new NBTTagCompound();
        for (Object key : JSUtil.keys(real)) {
            compound.func_74782_a(key.toString(), unwrap(JSUtil.get(real, key)));
        }
        return compound;
    }

    @SuppressWarnings("rawtypes")
    public WNBTBase get(String name) {
        return wrap(real.func_74781_a(name));
    }

    public void set(String name, Object value) {
        real.func_74782_a(name, unwrap(value));
    }

    public static WNBTCompound unwrapCompound(Object nbt) {
        if (nbt instanceof WNBTCompound)
            return (WNBTCompound) nbt;
        return new WNBTCompound(nbt);
    }
}
