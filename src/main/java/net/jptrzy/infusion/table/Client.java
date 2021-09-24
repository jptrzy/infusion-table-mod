package net.jptrzy.infusion.table;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.jptrzy.infusion.table.blocks.InfusionTableEntityRenderer;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Main.INFUSION_TABLE_ENTITY, InfusionTableEntityRenderer::new);
    }
}