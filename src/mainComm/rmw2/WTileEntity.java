package rmw2;

import com.anatawa12.mcWrapper.internal.McWrapper;
import com.anatawa12.mcWrapper.internal.WTileEntityImpl;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public final class WTileEntity {
    private static final WTileEntityImpl impl = McWrapper.getVersionedFactory("WTileEntityImplImpl");
    private WNBTCompound nbt;
    /*internal*/ final TileEntity tile;
    private String tileId;

    public WTileEntity(Object real) {
        if (real instanceof TileEntity) {
            Objects.requireNonNull(real, "entity");
            this.tile = (TileEntity)real;
        } else if (real instanceof WNBTCompound) {
            this.nbt = (WNBTCompound) real;
            this.tile = impl.createTileFromNBT(nbt);
        } else {
            this.nbt = new WNBTCompound(real);
            this.tile = impl.createTileFromNBT(nbt);
        }
    }

    public WNBTCompound getNbt() {
        if (nbt == null)
            nbt = impl.getWNBTCompoundByTile(tile);
        return nbt;
    }

    public void resetNBT() {
        nbt = null;
    }

    public WNBTCompound resetAndGetNBT() {
        resetNBT();
        return getNbt();
    }

    public WWorld getWorld() {
        return WWorld.wrap(tile.func_145831_w());
    }

    public int getX() {
        return impl.getX(tile);
    }

    public int getY() {
        return impl.getY(tile);
    }

    public int getZ() {
        return impl.getZ(tile);
    }

    public String getTileId() {
        if (tileId == null) {
            tileId = impl.getTileEntityName(tile);
        }
        return tileId;
    }

    public static WTileEntity wrap(TileEntity entity) {
        if (entity == null) return null;
        return impl.wrap(entity);
    }
}
