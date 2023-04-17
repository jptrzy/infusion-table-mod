package xyz.jptrzy.infusion_table.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
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
import xyz.jptrzy.infusion_table.InfusionTable;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;

import javax.annotation.Nullable;

public class InfusionTableBlock extends BlockWithEntity {
    public final static VoxelShape COLLISION_SHAPE;
    public final static BlockSoundGroup soundGroup;

    public InfusionTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        return this.soundGroup;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    static{
        COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
        soundGroup = BlockSoundGroup.DEEPSLATE_TILES;
    }

    // Block Entity

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return checkType(type, InfusionTable.INFUSION_TABLE_BLOCK_ENTITY.get(), InfusionTableBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) != null) {
            return ((InfusionTableBlockEntity) world.getBlockEntity(pos)).onUse(state, world, pos, player, hand, hit);
        }
        return ActionResult.PASS;
    }


    @Override public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if(world.getBlockEntity(pos) != null) {
                ((InfusionTableBlockEntity) world.getBlockEntity(pos)).onBreak(state, world, pos, null);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if(world.getBlockEntity(pos) != null) {
            ((InfusionTableBlockEntity) world.getBlockEntity(pos)).onBreak(state, world, pos, player);
        }
    }
}
