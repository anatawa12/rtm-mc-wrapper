package rmw2.functions;

import jp.ngt.rtm.render.ModelObject;
import net.minecraft.tileentity.TileEntity;

public interface TileEntityInitFunction {
    void init(TileEntity entity, ModelObject object);
}
