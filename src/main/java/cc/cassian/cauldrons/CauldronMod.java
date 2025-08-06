package cc.cassian.cauldrons;

import cc.cassian.cauldrons.config.ModConfig;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import cc.cassian.cauldrons.registry.CauldronSoundEvents;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CauldronMod implements ModInitializer {
	public static final String MOD_ID = "tt_cauldrons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
//	public static final ModConfig CONFIG = ModConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, ModConfig.class);

	public static ResourceLocation of(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Toil and trouble!");
		CauldronBlocks.touch();
		CauldronBlockEntityTypes.touch();
		CauldronSoundEvents.touch();
	}
}