package xyz.jptrzy.infusion_table.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.jptrzy.infusion_table.InfusionTable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.jptrzy.infusion_table.client.InfusionTableBlockEntityRenderer;

@Mod(InfusionTable.MOD_ID)
public class InfusionTableForge {
    public InfusionTableForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(InfusionTable.MOD_ID, eventBus);

        InfusionTable.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (DistExecutor.SafeRunnable) () -> eventBus.addListener(this::setupClient));
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(final FMLClientSetupEvent event) {
        BlockEntityRendererRegistry.register(InfusionTable.INFUSION_TABLE_BLOCK_ENTITY.get(), InfusionTableBlockEntityRenderer::new);
    }
}