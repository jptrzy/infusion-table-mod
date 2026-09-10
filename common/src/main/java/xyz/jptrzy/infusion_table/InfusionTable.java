package xyz.jptrzy.infusion_table;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jptrzy.infusion_table.block.InfusionTableBlock;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;
import xyz.jptrzy.infusion_table.utils.BlockEntityTypeHelper;
import xyz.jptrzy.infusion_table.utils.BlockLuminance;

public class InfusionTable {
    public static final String MOD_ID = "infusion_table";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Registrar<Item> ITEM_REGISTRAR = MANAGER.get().get(RegistryKeys.ITEM);
    public static final Registrar<Block> BLOCK_REGISTRAR = MANAGER.get().get(RegistryKeys.BLOCK);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRAR = MANAGER.get().get(RegistryKeys.BLOCK_ENTITY_TYPE);

    public static final Identifier INFUSION_TABLE_ID = Identifier.of(MOD_ID, "infusion_table");
    public static RegistrySupplier<Block> INFUSION_TABLE_BLOCK = BLOCK_REGISTRAR.register(
            INFUSION_TABLE_ID,
            () -> new InfusionTableBlock(
                    AbstractBlock.Settings.create()
                            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, INFUSION_TABLE_ID))
                            .strength(4.0f)
                            .requiresTool()
                            .luminance(new BlockLuminance(7)))
    );

    public static RegistrySupplier<BlockItem> INFUSION_TABLE_BLOCK_ITEM = ITEM_REGISTRAR.register(
            INFUSION_TABLE_ID,
            () -> new BlockItem(INFUSION_TABLE_BLOCK.get(),
                    new Item.Settings()
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM, INFUSION_TABLE_ID))
                            .arch$tab(ItemGroups.FUNCTIONAL))
    );
    public static RegistrySupplier<BlockEntityType<InfusionTableBlockEntity>> INFUSION_TABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPE_REGISTRAR.register(
            INFUSION_TABLE_ID,
            () -> BlockEntityTypeHelper.create(InfusionTableBlockEntity::new, INFUSION_TABLE_BLOCK.get())
    );

    public static void init() { }

    public static float aroundRadial(float angle){
        while(angle >= 3.1415927F) {
            angle -= 6.2831855F;
        }

        while(angle < -3.1415927F) {
            angle += 6.2831855F;
        }

        return angle;
    }
}