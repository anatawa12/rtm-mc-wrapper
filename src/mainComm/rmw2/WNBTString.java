package rmw2;

import net.minecraft.nbt.NBTTagString;

public final class WNBTString extends WNBTBase<NBTTagString> {
    public WNBTString(NBTTagString real) {
        super(real);
    }

    public WNBTString(String value) {
        super(new NBTTagString());
    }

    @Override
    public String getString() {
        return real.func_150285_a_();
    }
}
