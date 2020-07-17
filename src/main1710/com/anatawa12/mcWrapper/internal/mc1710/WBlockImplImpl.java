package com.anatawa12.mcWrapper.internal.mc1710;

import com.anatawa12.mcWrapper.internal.WBlockImpl;
import net.minecraft.block.Block;

public final class WBlockImplImpl implements WBlockImpl {
    @Override
    public Block getBlockByName(String name) {
        return (Block) Block.field_149771_c.func_82594_a(name);
    }

    @Override
    public String getNameOfBlock(Block block) {
        return Block.field_149771_c.func_148750_c(block);
    }

    @Override
    public void verifyState(Object state) {
        throw new IllegalArgumentException("state is not supported on 1.7.10");
    }

    @Override
    public Block getBlockByState(Object state) {
        throw new IllegalArgumentException("state is not supported on 1.7.10");
    }

    @Override
    public int getMeta(Object state) {
        throw new IllegalArgumentException("state is not supported on 1.7.10");
    }

    @Override
    public Object makeState(int meta, Block block) {
        throw new IllegalArgumentException("state is not supported on 1.7.10");
    }
}
