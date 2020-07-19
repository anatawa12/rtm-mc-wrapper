package rmw2.functions;

import net.minecraft.tileentity.TileEntity;

public interface TileEntityRenderFunction {
    void render(TileEntity entity, int pass, float partialTicks);
}
