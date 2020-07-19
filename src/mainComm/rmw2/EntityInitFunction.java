package rmw2;

import jp.ngt.rtm.render.ModelObject;
import net.minecraft.entity.Entity;

public interface EntityInitFunction {
    public void init(Entity entity, ModelObject object);
}
