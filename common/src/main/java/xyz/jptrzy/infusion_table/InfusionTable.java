package xyz.jptrzy.infusion_table;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jptrzy.infusion_table.block.InfusionTableBlock;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;
import xyz.jptrzy.infusion_table.utils.BlockEntityFactory;
import xyz.jptrzy.infusion_table.utils.BlockEntityTypeHelper;
import xyz.jptrzy.infusion_table.utils.BlockLuminance;

public class InfusionTable {
    public static final String MOD_ID = "infusion_table";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final String INFUSION_TABLE_PATH = "infusion_table";

    public static RegistrySupplier<Block> INFUSION_TABLE_BLOCK = BLOCKS.register(
            INFUSION_TABLE_PATH,
            () -> new InfusionTableBlock(
                    BlockBehaviour.Properties.of()
                            .setId(keyOfBlock(INFUSION_TABLE_PATH))
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(new BlockLuminance(7)))
    );

    public static RegistrySupplier<BlockItem> INFUSION_TABLE_BLOCK_ITEM = ITEMS.register(
            INFUSION_TABLE_PATH,
            () -> new BlockItem(INFUSION_TABLE_BLOCK.get(),
                    new Item.Properties()
                            .setId(keyOfItem(INFUSION_TABLE_PATH))
                            .arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                            .useBlockDescriptionPrefix()
            )
    );

    public static RegistrySupplier<BlockEntityType<InfusionTableBlockEntity>> INFUSION_TABLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    INFUSION_TABLE_PATH,  // ← String path only
                    () -> BlockEntityTypeHelper.create(InfusionTableBlockEntity::new, INFUSION_TABLE_BLOCK.get())
            );

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        BLOCK_ENTITIES.register();
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(InfusionTable.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(InfusionTable.MOD_ID, name));
    }
}