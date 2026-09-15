package xyz.jptrzy.infusion_table.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.jptrzy.infusion_table.InfusionTable;
import xyz.jptrzy.infusion_table.client.InfusionTableClient;

@Mod(InfusionTable.MOD_ID)
public class InfusionTableNeoForge {
    public InfusionTableNeoForge(IEventBus eventBus, ModContainer container) {
        InfusionTable.init();

        eventBus.addListener(this::setupClient);
    }

    @SubscribeEvent
    private void setupClient(final FMLClientSetupEvent event) {
        new InfusionTableClient().init();
    }
}