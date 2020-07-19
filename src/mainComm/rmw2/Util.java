package rmw2;

import com.anatawa12.mcWrapper.internal.McWrapper;

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
