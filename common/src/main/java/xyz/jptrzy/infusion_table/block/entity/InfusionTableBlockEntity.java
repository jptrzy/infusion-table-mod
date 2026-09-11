package xyz.jptrzy.infusion_table.block.entity;

import com.google.common.base.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import xyz.jptrzy.infusion_table.InfusionTable;

import java.util.Objects;
import java.util.Random;

public class InfusionTableBlockEntity extends BlockEntity implements SidedInventory {
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

    public static void tick(World world, BlockPos pos, BlockState state, InfusionTableBlockEntity entity) {
        //Book Rotation Animation
        // TODO Make it more clear
        if(world.isClient()) {
            entity.bookLastRot = entity.bookRot;

            PlayerEntity playerEntity = world.getClosestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 3.0D, false);
            if (playerEntity != null) {
                double d = playerEntity.getX() - ((double) pos.getX() + 0.5D);
                double e = playerEntity.getZ() - ((double) pos.getZ() + 0.5D);
                entity.bookRotDir = (float) MathHelper.atan2(e, d);
            } else {
                entity.bookRotDir += 0.02F;
            }
            entity.bookRot = wrapRadian(entity.bookRot);
            entity.bookRotDir = wrapRadian(entity.bookRotDir);
            entity.bookRotForce = entity.bookRotDir - entity.bookRot;
            entity.bookRotForce = wrapRadian(entity.bookRotForce);
            entity.bookRot += entity.bookRotForce * 0.4F;

            entity.bookLastOpenAngle = entity.bookOpenAngle;
        }

        if (entity.status == Status.Waiting) {
            if (entity.item.isEmpty()) {
                if (entity.bookOpenAngle <= 0) {
                    if (world.isClient()) return;

                    entity.status = Status.Passive;
                    entity.bookOpenAngle = 0;

                    entity.markDirty();
                } else {
                    entity.bookOpenAngle -= .1;
                }
            } else {
                if (entity.bookOpenAngle >= 1) {
                    if (world.isClient()) return;

                    entity.bookOpenAngle = 1;

                    entity.markDirty();
                } else {
                    entity.bookOpenAngle += .1;
                }
            }
        } else if (entity.status == Status.Enchanting) {
            if (entity.ticks > 60) {
                if(entity.bookOpenAngle > 0F) {
                    entity.bookOpenAngle -= 0.1F;
                }else{
                    if (world.isClient()) return;

                    world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, SoundCategory.BLOCKS, .8f, .8f);

                    entity.status = Status.Passive;
                    entity.ticks = 0;
                    entity.bookOpenAngle = 0;

                    entity.book = new ItemStack(Items.ENCHANTED_BOOK);

                    ItemEnchantmentsComponent component =  entity.item.getEnchantments();
                    EnchantmentHelper.set(entity.book, component);

                    entity.item.decrement(1);

                    entity.markDirty();
                }
            } else if(entity.ticks < 36) {
                Random random = new Random();
                // TODO Check if renders property in multiplayer
                world.addParticleClient(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D,
                        (random.nextBoolean() ? -1 : 1) * random.nextFloat(),
                        1,
                        (random.nextBoolean() ? -1 : 1) * random.nextFloat());
            }

            entity.ticks += 1;
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // TODO Don't return always successful output
        if (world.isClient()) return ActionResult.SUCCESS;

        ItemStack hand_item = player.getStackInHand(hand);

        if (status == Status.Passive) {
            if (book.isEmpty()) {
                if (hand_item.getItem() == Items.BOOK) {
                    world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT, SoundCategory.BLOCKS, .8f, .8f);

                    book = hand_item.copy();
                    book.setCount(1);

                    hand_item.decrement(1);

                    bookOpenAngle = 0;

                    markDirty();
                }
            } else if (item.isEmpty() && hand_item.hasEnchantments() && book.isOf(Items.BOOK)) {
                world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, SoundCategory.BLOCKS, .8f, 1.2f);

                // WARN book.hasEnchantments() and book.getEnchantments() doesnt work use book.getComponents() instead
                item = hand_item.copy();
                item.setCount(1);

                hand_item.decrement(1);

                status = Status.Waiting;

                markDirty();
            }
        } else if (status == Status.Waiting && bookOpenAngle >= 1 && hand_item.getItem() == Items.FLINT_AND_STEEL) {
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, .8f, .8f);
            world.playSound(null, pos, SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.BLOCKS, .9f, .6f);

            status = Status.Enchanting;

            markDirty();
        }

        world.playSound(null, pos, SoundEvents.BLOCK_CRAFTER_FAIL, SoundCategory.BLOCKS, .8f, 2f);

        return ActionResult.SUCCESS;
    }

    public void onBreak(@Nullable BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        if (world.isClient()) return;

        if (player == null) {
            dropStack(world, pos, item);
            dropStack(world, pos, book);

            cleanUp(world);
            return;
        } else if (status == Status.Passive || status == Status.Waiting) {
            if (!book.isEmpty()) {
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, .8f, .8f);

                dropStack(world, pos, (item.isEmpty() ? book : item).copy());

                if (item.isEmpty()) cleanUp(world);

                (item.isEmpty() ? book : item).decrement(1);

                markDirty();
                return;
            }
        }

        world.playSound(null, pos, SoundEvents.BLOCK_CRAFTER_FAIL, SoundCategory.BLOCKS, .6f, 2f);
    }

    public void onExplosion(World world, BlockPos pos, Explosion explosion) {
        onBreak(null, world, pos, null);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);

        if (!this.item.isEmpty()) {
            view.put("Item", ItemStack.CODEC, this.item);
        }
        if (!this.book.isEmpty()) {
            view.put("Book", ItemStack.CODEC, this.book);
        }
        view.putString("Status", this.status.name());
        view.putFloat("Ticks", this.ticks);
        view.putFloat("Angle", this.bookOpenAngle);
    }

    protected void readData(ReadView view) {
        super.readData(view);

        // Removes the error message

        this.item = view.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.book = view.read("Book", ItemStack.CODEC).orElse(ItemStack.EMPTY);
//        if (!nbt.getCompound("Item").isEmpty()) {

        // TODO Check if default values don't introduce errors
        this.status = Status.valueOf(view.getString("Status", "Passive"));
        this.ticks = view.getFloat("Ticks", 0);
        this.bookOpenAngle = view.getFloat("Angle", 0);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        // TODO I removed bellow super call cause is useless, but it might be important in the future
        //NbtCompound baseNbt = super.toInitialChunkDataNbt(registries);

        NbtWriteView view = NbtWriteView.create(ErrorReporter.EMPTY, registries);

        this.writeData(view);

        return view.getNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    // Utils

    public void cleanUp(World world){
        if (world.isClient()) return;

        this.status = Status.Passive;
        this.ticks = 0;
        this.bookOpenAngle = 0;
        this.item = ItemStack.EMPTY;
        this.book = ItemStack.EMPTY;

        this.markDirty();
    }

    public static void dropStack(World world, BlockPos pos, ItemStack stack) {
        float f = EntityType.ITEM.getHeight() / 2.0F;
        double d = (float)pos.getX() + 0.5F;
        double e = (double)((float)pos.getY() + 1F) - (double)f;
        double g = ( float)pos.getZ() + 0.5F;
        dropStack(world, () -> new ItemEntity(world, d, e, g, stack), stack);
    }

    private static void dropStack(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        if (!world.isClient() && !stack.isEmpty() && Objects.requireNonNull(world.getServer()).getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ItemEntity itemEntity = itemEntitySupplier.get();
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    // Hopper Support

    /*
        Inventory

        0 -  book
        1 -  item
     */

    enum Slot {
        BOOK(0),
        ITEM(1);

        private final int value;

        Slot(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }

    // TODO make it more dependent on calls up the code

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN || side == Direction.UP )
            return new int[]{0};
        return new int[]{1};
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (status == Status.Passive && stack.getCount() == 1) {
            if (slot == 0) {
                return book.isEmpty() && stack.isOf(Items.BOOK);
            } else if (slot == 1) {
                return !book.isEmpty() && book.isOf(Items.BOOK) && item.isEmpty() && stack.hasEnchantments();
            } else {
                InfusionTable.LOGGER.error("Asking for insertion of unexpected slot {}", slot);

                return false;
            }
        }

        return false;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && status == Status.Passive && book.isOf(Items.ENCHANTED_BOOK);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == 0 ? book : item;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = getStack(slot).copy();
        stack.setCount(amount);

        getStack(slot).decrement(amount);

        markDirty();

        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 0) {
            book = stack;
        } else if (slot == 1) {
            if (book.isOf(Items.BOOK)) {
                item = stack;
                status = Status.Enchanting;
            }
        } else {
            InfusionTable.LOGGER.error("Asking for changing stack of unexpected slot {}", slot);
        }

        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        InfusionTable.LOGGER.error("Clearing infusion table");
    }

    public static float wrapRadian(float angle){
        angle = angle % ((float) Math.TAU);
        if (angle >= ((float) Math.PI)) angle -= ((float) Math.TAU);
        if (angle < -((float) Math.PI)) angle += ((float) Math.TAU);
        return angle;
    }
}