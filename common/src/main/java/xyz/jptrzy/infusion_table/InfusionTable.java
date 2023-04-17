package xyz.jptrzy.infusion_table;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jptrzy.infusion_table.block.InfusionTableBlock;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;
import xyz.jptrzy.infusion_table.utils.BlockLuminance;

public class InfusionTable {
    public static final String MOD_ID = "infusion_table";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));

    public static final Registrar<Item> ITEM_REGISTRAR = REGISTRIES.get().get(Registry.ITEM_KEY);
    public static final Registrar<Block> BLOCK_REGISTRAR = REGISTRIES.get().get(Registry.BLOCK_KEY);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRAR = REGISTRIES.get().get(Registry.BLOCK_ENTITY_TYPE);

    public static final Identifier INFUSION_TABLE_ID = new Identifier(MOD_ID, "infusion_table");
    // TODO Luminance
    public static RegistrySupplier<Block> INFUSION_TABLE_BLOCK = BLOCK_REGISTRAR.register(INFUSION_TABLE_ID, () -> new InfusionTableBlock(AbstractBlock.Settings.of(Material.METAL).strength(4.0f).luminance(new BlockLuminance(7))));
    public static RegistrySupplier<BlockItem> INFUSION_TABLE_BLOCK_ITEM = ITEM_REGISTRAR.register(INFUSION_TABLE_ID, () -> new BlockItem(INFUSION_TABLE_BLOCK.get(), new Item.Settings().group(ItemGroup.REDSTONE)));
    public static RegistrySupplier<BlockEntityType<InfusionTableBlockEntity>> INFUSION_TABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPE_REGISTRAR.register(INFUSION_TABLE_ID, () -> BlockEntityType.Builder.create(InfusionTableBlockEntity::new, INFUSION_TABLE_BLOCK.get()).build(null));


    public static void init() { }
}