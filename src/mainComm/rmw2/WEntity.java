package rmw2;

import com.anatawa12.mcWrapper.internal.McWrapper;
import com.anatawa12.mcWrapper.internal.WEntityImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public final class WEntity {
    private static final WEntityImpl impl = McWrapper.getVersionedFactory("WEntityImplImpl");
    private WNBTCompound nbt;
    private final Entity entity;
    private String entityId;

    public WEntity(Entity entity) {
        this.entity = entity;
    }

    public WEntity(WNBTCompound nbt, WWorld world) {
        this.nbt = nbt;
        this.entity = EntityList.func_75615_a(nbt.real, world.world);
    }

    public WEntity(Object nbtIn, WWorld world) {
        WNBTCompound nbt = WNBTCompound.unwrapCompound(nbtIn);
        this.nbt = nbt;
        this.entity = EntityList.func_75615_a(nbt.real, world.world);
    }

    public WNBTCompound getNBT() {
        if (nbt == null) {
            nbt = impl.getWNBTCompoundByEntity(this.entity);
        }
        return nbt;
    }

    public void resetNBT() {
        nbt = null;
    }

    public WNBTCompound resetAndGetNBT() {
        resetNBT();
        return getNBT();
    }

    public WWorld getWorld() {
        return WWorld.wrap(entity.field_70170_p);
    }

    public double getX() {
        return entity.field_70165_t;
    }

    public double getY() {
        return entity.field_70163_u;
    }

    public double getZ() {
        return entity.field_70161_v;
    }

    public String getEntityId() {
        if (entityId == null) {
            entityId = EntityList.func_75621_b(this.entity);
        }
        return entityId;
    }

    public static WEntity wrap(Entity entity) {
        return impl.wrap(entity);
    }
}
