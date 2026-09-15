package xyz.jptrzy.infusion_table;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
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

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Registrar<Item> ITEM_REGISTRAR = MANAGER.get().get(BuiltInRegistries.ITEM);
    public static final Registrar<Block> BLOCK_REGISTRAR = MANAGER.get().get(BuiltInRegistries.BLOCK);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRAR = MANAGER.get().get(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final Identifier INFUSION_TABLE_ID = Identifier.fromNamespaceAndPath(MOD_ID, "infusion_table");
    public static RegistrySupplier<Block> INFUSION_TABLE_BLOCK = BLOCK_REGISTRAR.register(
            INFUSION_TABLE_ID,
            () -> new InfusionTableBlock(
                    BlockBehaviour.Properties.of()
                            .setId(keyOfBlock("infusion_table"))
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(new BlockLuminance(7)))
    );

    public static RegistrySupplier<BlockItem> INFUSION_TABLE_BLOCK_ITEM = ITEM_REGISTRAR.register(
            INFUSION_TABLE_ID,
            () -> new BlockItem(INFUSION_TABLE_BLOCK.get(),
                    new Item.Properties()
                            .setId(keyOfItem("infusion_table"))
                            .arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                            .useBlockDescriptionPrefix()
            )
    );
    public static RegistrySupplier<BlockEntityType<InfusionTableBlockEntity>> INFUSION_TABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPE_REGISTRAR.register(
            INFUSION_TABLE_ID,
            () -> BlockEntityTypeHelper.create(InfusionTableBlockEntity::new, INFUSION_TABLE_BLOCK.get())
    );

    public static void init() { }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(InfusionTable.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(InfusionTable.MOD_ID, name));
    }
}