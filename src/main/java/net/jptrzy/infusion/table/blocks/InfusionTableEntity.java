package net.jptrzy.infusion.table.blocks;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.jptrzy.infusion.table.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class InfusionTableEntity extends BlockEntity implements BlockEntityClientSerializable {

    private ItemStack item = new ItemStack(Items.AIR);
    private ItemStack book = new ItemStack(Items.AIR);
    //private boolean crafting = false;

    public InfusionTableEntity(BlockPos pos, BlockState state) {
        super(Main.INFUSION_TABLE_ENTITY, pos, state);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Main.LOGGER.warn("USE");
        return ActionResult.SUCCESS;
    }

    public void onExplosion(World world, BlockPos pos, Explosion explosion) {
        onBreak(null, world, pos, null);
    }

    public void onBreak(@Nullable BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        Main.LOGGER.warn("BREAK");
//        if(!this.item.isEmpty()){
//            if (!world.isClient) {
//                dropStack(world, pos.up(0), this.item);
//                this.item = new ItemStack(Items.AIR);
//                this.sync();
//            }else{
//                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
//            }
//        }
    }

    public static void dropStack(World world, BlockPos pos, ItemStack stack) {
        float f = EntityType.ITEM.getHeight() / 2.0F;
        double d = (double)((float)pos.getX() + 0.5F);
        double e = (double)((float)pos.getY() + 1F) - (double)f;
        double g = (double)((float)pos.getZ() + 0.5F);
        dropStack(world, () -> { return new ItemEntity(world, d, e, g, stack);}, stack);
    }

    private static void dropStack(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ItemEntity itemEntity = (ItemEntity)itemEntitySupplier.get();
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, InfusionTableEntity be) { }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.put("Item", this.item.writeNbt(new NbtCompound()));

        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        this.item = ItemStack.fromNbt(tag.getCompound("Item"));
    }

    @Override
    public void fromClientTag(NbtCompound nbt){ this.readNbt(nbt); }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt){ return this.writeNbt(nbt);}

    public ItemStack getItem(){return this.item;}
    public ItemStack getBook(){return this.book;}
}