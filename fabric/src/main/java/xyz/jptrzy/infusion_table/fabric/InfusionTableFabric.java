package xyz.jptrzy.infusion_table.fabric;

import xyz.jptrzy.infusion_table.InfusionTable;
import net.fabricmc.api.ModInitializer;

public class InfusionTableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        InfusionTable.init();
    }
}