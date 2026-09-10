package xyz.jptrzy.infusion_table.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockEntityFactory<T extends BlockEntity> {
    T create(BlockPos pos, BlockState state);
}
