package xyz.jptrzy.infusion_table.utils.neoforge;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import xyz.jptrzy.infusion_table.utils.BlockEntityFactory;

import java.util.Set;

public class BlockEntityTypeHelperImpl {
    public static <T extends BlockEntity> BlockEntityType<T> create(
            BlockEntityFactory<T> factory, Block... blocks) {
        return new BlockEntityType<>(factory::create, Set.of(blocks));
    }
}
