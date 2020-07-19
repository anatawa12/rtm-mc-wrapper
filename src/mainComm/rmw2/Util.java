package rmw2;

import com.anatawa12.mcWrapper.internal.McWrapper;
import rmw2.functions.EntityInitFunction;
import rmw2.functions.EntityRenderFunction;
import rmw2.functions.TileEntityInitFunction;
import rmw2.functions.TileEntityRenderFunction;
import rmw2.functions.WEntityInitFunction;
import rmw2.functions.WEntityRenderFunction;
import rmw2.functions.WTileEntityInitFunction;
import rmw2.functions.WTileEntityRenderFunction;

public class Util {
    private Util() {}

    public final boolean is1710 = McWrapper.is1710;
    public final boolean is1122 = !is1710;

    public static EntityInitFunction entityInit(WEntityInitFunction init) {
        return (entity, object) -> init.init(WEntity.wrap(entity), object);
    }

    public static TileEntityInitFunction entityInit(WTileEntityInitFunction init) {
        return (entity, object) -> init.init(WTileEntity.wrap(entity), object);
    }

    public static EntityRenderFunction entityRender(WEntityRenderFunction init) {
        return (entity, pass, partialTicks) -> init.render(WEntity.wrap(entity), pass, partialTicks);
    }

    public static TileEntityRenderFunction entityRender(WTileEntityRenderFunction init) {
        return (entity, pass, partialTicks) -> init.render(WTileEntity.wrap(entity), pass, partialTicks);
    }
}
