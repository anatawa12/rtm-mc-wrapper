package rmw2;

import net.minecraft.entity.Entity;

public interface EntityRenderFunction {
    void render(Entity entity, int pass, float partialTicks);
}
