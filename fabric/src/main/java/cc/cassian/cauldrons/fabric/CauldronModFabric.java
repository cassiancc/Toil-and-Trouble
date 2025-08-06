package cc.cassian.cauldrons.fabric;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import cc.cassian.cauldrons.registry.CauldronSoundEvents;
import net.fabricmc.api.ModInitializer;

public final class CauldronModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CauldronMod.init();
        CauldronBlocks.touch();
        CauldronBlockEntityTypes.touch();
        CauldronSoundEvents.touch();
    }
}
