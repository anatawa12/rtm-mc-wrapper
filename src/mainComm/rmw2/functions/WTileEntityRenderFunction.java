package rmw2.functions;

import rmw2.WTileEntity;

public interface WTileEntityRenderFunction {
    void render(WTileEntity entity, int pass, float partialTicks);
}
