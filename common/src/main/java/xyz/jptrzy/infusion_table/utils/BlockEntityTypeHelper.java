package xyz.jptrzy.infusion_table.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntityTypeHelper {
    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> create(
            BlockEntityFactory<T> factory, Block... blocks) {
        throw new AssertionError();
    }
}
