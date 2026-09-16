package xyz.jptrzy.infusion_table.block.entity;

import com.google.common.base.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import xyz.jptrzy.infusion_table.InfusionTable;

import java.util.Random;

public class InfusionTableBlockEntity extends BlockEntity implements WorldlyContainer {
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

    public InfusionTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(InfusionTable.INFUSION_TABLE_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    public static float wrapRadian(float angle){
        angle = angle % ((float) Math.TAU);
        if (angle >= ((float) Math.PI)) angle -= ((float) Math.TAU);
        if (angle < -((float) Math.PI)) angle += ((float) Math.TAU);
        return angle;
    }

    public void animateBook(Level level, BlockPos blockPos) {
        bookLastRot = bookRot;

        Player player = level.getNearestPlayer((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D, 3.0D, false);

        if (player == null) {
            bookRotDir += 0.02F;
        } else {
            double d = player.getX() - ((double) blockPos.getX() + 0.5D);
            double e = player.getZ() - ((double) blockPos.getZ() + 0.5D);
            bookRotDir = (float) Mth.atan2(e, d);
        }

        bookRot = wrapRadian(bookRot);
        bookRotDir = wrapRadian(bookRotDir);
        bookRotForce = bookRotDir - bookRot;
        bookRotForce = wrapRadian(bookRotForce);
        bookRot += bookRotForce * 0.4F;

        bookLastOpenAngle = bookOpenAngle;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, InfusionTableBlockEntity entity) {
        if (level.isClientSide()) {
            entity.animateBook(level, blockPos);
        }

        if (entity.status == Status.Waiting) {
            if (entity.item.isEmpty()) {
                if (entity.bookOpenAngle <= 0) {
                    if (level.isClientSide()) return;

                    entity.status = Status.Passive;
                    entity.bookOpenAngle = 0;

                    entity.setChanged();
                } else {
                    entity.bookOpenAngle -= .1;
                }
            } else {
                if (entity.bookOpenAngle >= 1) {
                    if (level.isClientSide()) return;

                    entity.bookOpenAngle = 1;

                    entity.setChanged();
                } else {
                    entity.bookOpenAngle += .1;
                }
            }
        } else if (entity.status == Status.Enchanting) {
            if (entity.ticks > 60) {
                if(entity.bookOpenAngle > 0F) {
                    entity.bookOpenAngle -= 0.1F;
                }else{
                    if (level.isClientSide()) return;

                    level.playSound(null, blockPos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.BLOCKS, .8f, .8f);

                    entity.status = Status.Passive;
                    entity.ticks = 0;
                    entity.bookOpenAngle = 0;

                    entity.book = new ItemStack(Items.ENCHANTED_BOOK);

                    ItemEnchantments component =  entity.item.getEnchantments();
                    EnchantmentHelper.setEnchantments(entity.book, component);

                    entity.item.shrink(1);

                    entity.setChanged();
                }
            } else if(entity.ticks < 36) {
                Random random = new Random();
                // TODO Check if renders property in multiplayer
                level.addParticle(ParticleTypes.ENCHANT, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 1.0D, (double)blockPos.getZ() + 0.5D,
                        (random.nextBoolean() ? -1 : 1) * random.nextFloat(),
                        1,
                        (random.nextBoolean() ? -1 : 1) * random.nextFloat());
            }

            entity.ticks += 1;
        }
    }

    public InteractionResult useItem(Level level, BlockPos blockPos, ItemStack playerItem) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (status == Status.Passive) {
            if (book.isEmpty()) {
                if (playerItem.getItem() == Items.BOOK) {
                    level.playSound(null, blockPos, SoundEvents.CHISELED_BOOKSHELF_INSERT, SoundSource.BLOCKS, .8f, .8f);

                    book = playerItem.copy();
                    book.setCount(1);

                    playerItem.shrink(1);

                    bookOpenAngle = 0;

                    setChanged();
                }
            } else if (item.isEmpty() && playerItem.isEnchanted() && book.is(Items.BOOK)) {
                level.playSound(null, blockPos, SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED, SoundSource.BLOCKS, .8f, 1.2f);

                // WARN book.hasEnchantments() and book.getEnchantments() doesnt work use book.getComponents() instead
                item = playerItem.copy();
                item.setCount(1);

                playerItem.shrink(1);

                status = Status.Waiting;

                setChanged();
            }
        } else if (status == Status.Waiting && bookOpenAngle >= 1 && playerItem.getItem() == Items.FLINT_AND_STEEL) {
            level.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, .8f, .8f);
            level.playSound(null, blockPos, SoundEvents.GUARDIAN_ATTACK, SoundSource.BLOCKS, .9f, .6f);

            status = Status.Enchanting;

            setChanged();
        }

        level.playSound(null, blockPos, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, .8f, .2f);

        return InteractionResult.SUCCESS;
    }

    public void attack(Level level, BlockPos blockPos) {
        if (level.isClientSide()) return;

        // TODO stop enchanting
        if (status == Status.Passive || status == Status.Waiting) {
            if (!book.isEmpty()) {
                level.playSound(null, blockPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, .8f, .8f);

                dropStack(level, blockPos, (item.isEmpty() ? book : item).copy());

                if (item.isEmpty()) cleanUp(level);

                (item.isEmpty() ? book : item).shrink(1);

                setChanged();
                return;
            }
        }

        level.playSound(null, blockPos, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, .6f, .2f);
    }

    @Override
    public void preRemoveSideEffects(BlockPos blockPos, BlockState state) {
        super.preRemoveSideEffects(blockPos, state);

        dropStack(level, blockPos, item);
        dropStack(level, blockPos, book);

        cleanUp(level);
    }

//    public void onExplosion(World world, BlockPos pos, Explosion explosion) {
//        onBreak(null, world, pos, null);
//    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (!this.item.isEmpty()) {
            output.store("Item", ItemStack.CODEC, this.item);
        }
        if (!this.book.isEmpty()) {
            output.store("Book", ItemStack.CODEC, this.book);
        }
        output.putString("Status", this.status.name());
        output.putFloat("Ticks", this.ticks);
        output.putFloat("Angle", this.bookOpenAngle);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        this.item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.book = input.read("Book", ItemStack.CODEC).orElse(ItemStack.EMPTY);

        // TODO Check if default values don't introduce errors
        this.status = Status.valueOf(input.getStringOr("Status", "Passive"));
        this.ticks = input.getFloatOr("Ticks", 0);
        this.bookOpenAngle = input.getFloatOr("Angle", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void cleanUp(Level level){
        if (level.isClientSide()) return;

        this.status = Status.Passive;
        this.ticks = 0;
        this.bookOpenAngle = 0;
        this.item = ItemStack.EMPTY;
        this.book = ItemStack.EMPTY;

        this.setChanged();
    }

    public static void dropStack(Level level, BlockPos pos, ItemStack stack) {
        float f = EntityTypes.ITEM.getHeight() / 2.0F;
        double d = (float)pos.getX() + 0.5F;
        double e = (double)((float)pos.getY() + 1F) - (double)f;
        double g = ( float)pos.getZ() + 0.5F;
        dropStack(level, () -> new ItemEntity(level, d, e, g, stack), stack);
    }

    private static void dropStack(Level level, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        // GameRules.DO_TILE_DROPS
        if (level instanceof ServerLevel serverWorld && !stack.isEmpty() && serverWorld.getGameRules().get(GameRules.BLOCK_DROPS)) {
            ItemEntity itemEntity = itemEntitySupplier.get();
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

//    // Hopper Support
//
//    /*
//        Inventory
//
//        0 -  book
//        1 -  item
//     */
//
//    enum Slot {
//        BOOK(0),
//        ITEM(1);
//
//        private final int value;
//
//        Slot(final int newValue) {
//            value = newValue;
//        }
//
//        public int getValue() { return value; }
//    }
//
//    // TODO make it more dependent on calls up the code


    @Override
    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN || direction == Direction.UP )
            return new int[]{0};
        return new int[]{1};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        if (status == Status.Passive && itemStack.getCount() == 1) {
            if (slot == 0) {
                return book.isEmpty() && itemStack.is(Items.BOOK);
            } else if (slot == 1) {
                return !book.isEmpty() && book.is(Items.BOOK) && item.isEmpty() && itemStack.isEnchanted();
            } else {
                InfusionTable.LOGGER.error("Asking for insertion of unexpected slot {}", slot);

                return false;
            }
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return slot == 0 && status == Status.Passive && book.is(Items.ENCHANTED_BOOK);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty() && book.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? book : item;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot).copy();
        stack.setCount(count);

        getItem(slot).shrink(count);

        setChanged();

        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        // TODO what does noUpdate mean
        return removeItem(slot, 1);
    }


    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (slot == 0) {
            book = itemStack;
        } else if (slot == 1) {
            if (book.is(Items.BOOK)) {
                item = itemStack;
                status = Status.Enchanting;
            }
        } else {
            InfusionTable.LOGGER.error("Asking for changing stack of unexpected slot {}", slot);
        }

        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        // TODO what does it do?
        return false;
    }

    @Override
    public void clearContent() {
        InfusionTable.LOGGER.error("Clearing infusion table");
    }

}