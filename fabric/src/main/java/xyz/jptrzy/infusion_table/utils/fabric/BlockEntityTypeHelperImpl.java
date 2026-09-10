package xyz.jptrzy.infusion_table.utils.fabric;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import xyz.jptrzy.infusion_table.utils.BlockEntityFactory;

public class BlockEntityTypeHelperImpl {
    public static <T extends BlockEntity> BlockEntityType<T> create(
            BlockEntityFactory<T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
    }
}
