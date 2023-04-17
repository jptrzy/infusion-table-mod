package xyz.jptrzy.infusion_table.utils;

import net.minecraft.block.BlockState;

import java.util.function.ToIntFunction;

public class BlockLuminance implements ToIntFunction<BlockState> {
    protected int luminance = 0;

    public BlockLuminance (int luminance) {
        this.luminance = luminance;
    }

    @Override
    public int applyAsInt(BlockState blockState) {
        return luminance;
    }
}
