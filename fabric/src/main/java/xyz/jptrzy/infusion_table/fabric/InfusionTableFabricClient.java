package xyz.jptrzy.infusion_table.fabric;

import net.fabricmc.api.ClientModInitializer;
import xyz.jptrzy.infusion_table.client.InfusionTableClient;

public class InfusionTableFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new InfusionTableClient().init();
    }
}
