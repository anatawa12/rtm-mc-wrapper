package rmw2.functions;

import jp.ngt.rtm.render.ModelObject;
import net.minecraft.entity.Entity;

public interface EntityInitFunction {
    void init(Entity entity, ModelObject object);
}
