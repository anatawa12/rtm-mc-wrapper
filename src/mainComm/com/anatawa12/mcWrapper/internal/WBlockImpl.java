package com.anatawa12.mcWrapper.internal;

import net.minecraft.block.Block;

public interface WBlockImpl {
    Block getBlockByName(String name);

    String getNameOfBlock(Block block);

    void verifyState(Object state);

    Block getBlockByState(Object state);

    int getMeta(Object state);

    Object makeState(int meta, Block block);
}
