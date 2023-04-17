package xyz.jptrzy.infusion_table.block.entity;

import com.google.common.base.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import xyz.jptrzy.infusion_table.InfusionTable;

public class InfusionTableBlockEntity extends BlockEntity {
    public enum Status {
        Passive,
        Waiting,
        Enchanting
    }

    public ItemStack item = ItemStack.EMPTY;
    public ItemStack book = ItemStack.EMPTY;

    public Status status = Status.Passive;
    public float ticks = 0f;
    public float bookOpenAngle = 0f;

    public float bookLastOpenAngle = 0F;

    // Following Book
    public float bookRot = 0;
    public float bookLastRot = 0;
    public float bookRotDir = 0;
    public float bookRotForce = 0;

    public InfusionTableBlockEntity(BlockPos pos, BlockState state) {
        super(InfusionTable.INFUSION_TABLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, InfusionTableBlockEntity blockEntity) {

    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ItemStack hand_item = player.getStackInHand(hand);

        if (status == Status.Passive) {
            if (book.isEmpty() && hand_item.getItem() == Items.BOOK) {
                book = hand_item.copy();
                book.setCount(1);

                hand_item.decrement(1);

                bookOpenAngle = 0;

                notifyListeners();
            } else if (item.isEmpty() && hand_item.hasEnchantments()) {
                item = hand_item.copy();
                item.setCount(1);

                hand_item.decrement(1);

                status = Status.Waiting;

                notifyListeners();
            }
        } else if (status == Status.Passive && hand_item.getItem() == Items.FLINT_AND_STEEL) {
            status = Status.Enchanting;
            notifyListeners();
        }

        return ActionResult.SUCCESS;
    }

    public void onBreak(@Nullable BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        if (world.isClient()) {
            return;
        }

        if (player == null) {
            dropStack(world, pos, item);
            dropStack(world, pos, book);

            cleanUp(world);
        } else if (status == Status.Passive || status == Status.Waiting) {
            if (!book.isEmpty()) {
                dropStack(world, pos, item.isEmpty() ? book : item);

                if (status == Status.Waiting) {
                    status = Status.Passive;
                }

                notifyListeners();
            }
        }

    }

    public void onExplosion(World world, BlockPos pos, Explosion explosion) {
        onBreak(null, world, pos, null);
    }

    // NBT

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.put("Item", this.item.writeNbt(new NbtCompound()));
        tag.put("Book", this.book.writeNbt(new NbtCompound()));
        tag.putString("Status", this.status.name());
        tag.putFloat("Ticks", this.ticks);
        tag.putFloat("Angle", this.bookOpenAngle);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        this.item = ItemStack.fromNbt(tag.getCompound("Item"));
        this.book = ItemStack.fromNbt(tag.getCompound("Book"));
        this.status = Status.valueOf(tag.getString("Status"));
        this.ticks = tag.getFloat("Ticks");
        this.bookOpenAngle = tag.getFloat("Angle");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        writeNbt(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void notifyListeners() {
        this.markDirty();

        if (world != null && !world.isClient()) {
            world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    // Utils

    public void cleanUp(World world){
        if (world.isClient()) {
            return;
        }

        this.status = Status.Passive;
        this.ticks = 0;
        this.bookOpenAngle = 0;
        this.item = ItemStack.EMPTY;
        this.book = ItemStack.EMPTY;

        this.notifyListeners();
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

}