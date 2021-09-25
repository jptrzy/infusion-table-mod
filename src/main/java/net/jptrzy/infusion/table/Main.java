package net.jptrzy.infusion.table;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.jptrzy.infusion.table.blocks.InfusionTableBlock;
import net.jptrzy.infusion.table.blocks.InfusionTableEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {

	public static final String MOD_ID = "infusion_table";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final Block INFUSION_TABLE = new InfusionTableBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
	public static BlockEntityType<InfusionTableEntity> INFUSION_TABLE_ENTITY;

	@Override
	public void onInitialize() {
		INFUSION_TABLE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Main.MOD_ID + ":infusion_table_entity", FabricBlockEntityTypeBuilder.create(InfusionTableEntity::new, INFUSION_TABLE).build(null));

		Registry.register(Registry.BLOCK, new Identifier(Main.MOD_ID, "infusion_table"), INFUSION_TABLE);
		Registry.register(Registry.ITEM, new Identifier(Main.MOD_ID, "infusion_table"), new BlockItem(INFUSION_TABLE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
	}
}
