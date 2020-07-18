package com.anatawa12.mcWrapper.internal.mc1122;

import com.anatawa12.mcWrapper.internal.WBlockImpl;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public final class WBlockImplImpl implements WBlockImpl {
    @Override
    public Block getBlockByName(String name) {
        return Block.field_149771_c.func_82594_a(new ResourceLocation(name));
    }

    @Override
    public String getNameOfBlock(Block block) {
        return Block.field_149771_c.func_177774_c(block).toString();
    }

    @Override
    public void verifyState(Object state) {
        if (!(state instanceof IBlockState))
            throw new IllegalArgumentException("state is not instance of IBlockState");
    }

    @Override
    public Block getBlockByState(Object state) {
        return ((IBlockState)state).func_177230_c();
    }

    @Override
    public int getMeta(Object state) {
        Block block = ((IBlockState)state).func_177230_c();
        return block.func_149717_k((IBlockState) state);
    }

    @Override
    public Object makeState(int meta, Block block) {
        return block.func_176203_a(meta);
    }
}
