package xyz.jptrzy.infusion_table.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.jptrzy.infusion_table.InfusionTable;

public class InfusionTableClient {
    @Environment(EnvType.CLIENT)
    public void init() {
        BlockEntityRendererRegistry.register(InfusionTable.INFUSION_TABLE_BLOCK_ENTITY.get(), InfusionTableBlockEntityRenderer::new);
    }
}
