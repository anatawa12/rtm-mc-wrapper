package rmw2.functions;

import jp.ngt.rtm.render.ModelObject;
import rmw2.WEntity;

public interface WEntityInitFunction {
    void init(WEntity entity, ModelObject object);
}
