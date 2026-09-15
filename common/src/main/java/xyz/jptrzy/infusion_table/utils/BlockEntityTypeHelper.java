package xyz.jptrzy.infusion_table.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeHelper {
    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> create(
            BlockEntityFactory<T> factory, Block... blocks) {
        throw new AssertionError();
    }
}
