package rmw2.functions;

import rmw2.WEntity;

public interface WEntityRenderFunction {
    void render(WEntity entity, int pass, float partialTicks);
}
