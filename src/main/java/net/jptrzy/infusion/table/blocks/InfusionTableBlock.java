package net.jptrzy.infusion.table.blocks;

import net.jptrzy.infusion.table.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;



public class InfusionTableBlock extends Block implements BlockEntityProvider {

    public InfusionTableBlock(Settings settings) {
        super(settings);
    }

    public final static VoxelShape COLLISION_SHAPE;

    public final static BlockSoundGroup soundGroup = BlockSoundGroup.COPPER;

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionTableEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
       return ((InfusionTableEntity) world.getBlockEntity(pos)).onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            InfusionTableEntity blockEntity = (InfusionTableEntity) world.getBlockEntity(pos);
            blockEntity.onBreak(state, world, pos, null);
        }
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Main.INFUSION_TABLE_ENTITY, (world1, pos, state1, be) -> InfusionTableEntity.tick(world1, pos, state1, be));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        return this.soundGroup;
    }

    static{
        COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    }
}
