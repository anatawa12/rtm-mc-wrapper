package rmw2;

import com.anatawa12.mcWrapper.internal.InternalSignature;
import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import com.anatawa12.mcWrapper.internal.McWrapper;
import com.anatawa12.mcWrapper.internal.WBlockImpl;
import net.minecraft.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class WBlock {
    private static final WBlockImpl impl = McWrapper.getVersionedFactory("WBlockImplImpl");
    private static final List<String> colors = Arrays.asList(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", 
            "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );
    private int meta;
    private final String name;
    /* internal */ final Block block;
    private Object realState;

    public WBlock(Object object) {
        String name = JSUtil.requireInstanceOf(object, "name", String.class);
        Block block = JSUtil.requireInstanceOf(object, "block", Block.class);
        Integer meta = JSUtil.requireInstanceOf(object, "meta", Integer.class);
        Object state = JSUtil.requireInstanceOf(object, "state", Object.class);

        if (state != null) {
            impl.verifyState(state);
            block = impl.getBlockByState(state);
            meta = impl.getMeta(state);
        }

        if (meta == null) {
            meta = 0;
        }
        if (name != null) {
            block = impl.getBlockByName(name);
        } else if (block != null) {
            name = impl.getNameOfBlock(block);
        }

        this.meta = meta;
        this.name = name;
        this.block = block;
        this.realState = state;
    }

    public WBlock(Block block, int meta, InternalSignature signature) {
        Objects.requireNonNull(block);

        this.name = impl.getNameOfBlock(block);

        this.meta = meta;
        this.block = block;
        this.realState = null;
    }

    public WBlock(Object state, InternalSignature signature) {
        this.block = impl.getBlockByState(state);
        this.meta = impl.getMeta(state);
        this.name = impl.getNameOfBlock(block);
        this.realState = state;
    }

    public String getName() {
        return name;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
        realState = null;
    }

    public String getColor() {
        return colors.get(getMeta());
    }

    public void setColor(String color) {
        setMeta(colors.indexOf(color));
    }

    public Object makeBlockState() {
        if (realState != null) return realState;
        realState = impl.makeState(meta, block);
        return realState;
    }

    public static WBlock create(Object block) {
        if (block instanceof WBlock) return (WBlock) block;
        return new WBlock(block);
    }
}
