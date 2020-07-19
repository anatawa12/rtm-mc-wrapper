package rmw2;

import jp.ngt.rtm.render.ModelObject;
import net.minecraft.tileentity.TileEntity;

public interface TileEntityInitFunction {
    public void init(TileEntity entity, ModelObject object);
}
