package rmw2;

import net.minecraft.nbt.NBTTagIntArray;

public final class WNBTIntArray extends WNBTBase<NBTTagIntArray> {
    public WNBTIntArray(NBTTagIntArray real) {
        super(real);
    }

    public WNBTIntArray(int[] array) {
        super(new NBTTagIntArray(array));
    }

    public int[] getIntArray() {
        return real.func_150302_c();
    }
}
