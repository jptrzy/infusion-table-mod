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

    public final static VoxelShape COLLISION_SHAPE;
    public final static BlockSoundGroup soundGroup = BlockSoundGroup.DEEPSLATE_TILES;
//    public boolean startBreaking = false;


    public InfusionTableBlock(Settings settings) {
        super(settings);
    }



    @Override public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionTableEntity(pos, state);
    }

    @Override public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) != null) {
            return ((InfusionTableEntity) world.getBlockEntity(pos)).onUse(state, world, pos, player, hand, hit);
        }else if(!Main.CARRIER_MOD_LOADED){
            Main.LOGGER.error("BlockEntity (inside onUse function) was null. This shouldn't had happened.");
        }
        return ActionResult.FAIL;
    }

    @Override public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if(world.getBlockEntity(pos) != null) {
                ((InfusionTableEntity) world.getBlockEntity(pos)).onBreak(state, world, pos, null);
            }else if(!Main.CARRIER_MOD_LOADED){
                Main.LOGGER.error("BlockEntity (inside onStateReplaced function) was null. This shouldn't had happened.");
            }
            super.onStateReplaced(state, world, pos, newState, moved); // Suggested by gliscowo #5
        }
    }



    @Override public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if(world.getBlockEntity(pos) != null) {
            ((InfusionTableEntity) world.getBlockEntity(pos)).onBreak(state, world, pos, player);
//            startBreaking = ((InfusionTableEntity) world.getBlockEntity(pos)).getState() != 0;
        }else if(!Main.CARRIER_MOD_LOADED){
            Main.LOGGER.error("BlockEntity (inside onBlockBreakStart function) was null. This shouldn't had happened.");
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
//        if(player.isCreative() && startBreaking){
//            startBreaking = false;
//            return;
//        }
        super.onBreak(world, pos, state, player);
    }

    @Nullable protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Main.INFUSION_TABLE_ENTITY, (world1, pos, state1, be) -> InfusionTableEntity.tick(world1, pos, state1, be));
    }

    @Override public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override public BlockSoundGroup getSoundGroup(BlockState state) {
        return this.soundGroup;
    }



    static{
        COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    }
}
