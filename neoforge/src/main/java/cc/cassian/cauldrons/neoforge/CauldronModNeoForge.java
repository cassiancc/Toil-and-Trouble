package cc.cassian.cauldrons.neoforge;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import cc.cassian.cauldrons.registry.CauldronSoundEvents;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(CauldronMod.MOD_ID)
@EventBusSubscriber(modid = CauldronMod.MOD_ID)
public final class CauldronModNeoForge {
    public CauldronModNeoForge() {
        // Run our common setup.
        CauldronMod.init();
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            CauldronBlocks.touch();
        } else if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            CauldronBlockEntityTypes.touch();
        }  else if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) {
            CauldronSoundEvents.touch();
        }
    }
}
