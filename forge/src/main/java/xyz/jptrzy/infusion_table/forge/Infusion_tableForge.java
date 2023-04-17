package xyz.jptrzy.infusion_table.forge;

import dev.architectury.platform.forge.EventBuses;
import xyz.jptrzy.infusion_table.InfusionTable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InfusionTable.MOD_ID)
public class Infusion_tableForge {
    public Infusion_tableForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(InfusionTable.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        InfusionTable.init();
    }
}