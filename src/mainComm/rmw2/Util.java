package rmw2;

import com.anatawa12.dtsGenerator.TsSignature;
import com.anatawa12.mcWrapper.internal.McWrapper;
import com.anatawa12.mcWrapper.internal.utils.JSUtil;

public class Util {
    private Util() {}

    public final boolean is1710 = McWrapper.is1710;
    public final boolean is1122 = !is1710;

    @TsSignature("<T extends any[], R>(func: (entity: Packages.rmw2.WEntity, ...args: T) => R)" +
            ": (entity: t_unknown /* Entity */, ...args: T) => R")
    public static Object entityFunc(Object func) {
        JSUtil.checkInitialized();
        return JSUtil.WEntityWrapFunc.apply(func);
    }

    @TsSignature("<T extends any[], R>(func: (tile: Packages.rmw2.WTileEntity, ...args: T) => R)" +
            ": (tile: t_unknown /* TileEntity */, ...args: T) => R")
    public static Object tileEntityFunc(Object func) {
        JSUtil.checkInitialized();
        return JSUtil.WEntityWrapFunc.apply(func);
    }

}
