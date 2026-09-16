package xyz.jptrzy.infusion_table.client;


import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import xyz.jptrzy.infusion_table.InfusionTable;

public class InfusionTableClient {
    public void init() {
        InfusionTable.INFUSION_TABLE_BLOCK_ENTITY.listen(entity -> {
            BlockEntityRendererRegistry.register(entity, InfusionTableBlockEntityRenderer::new);
        });
    }
}
