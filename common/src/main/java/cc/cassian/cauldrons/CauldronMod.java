package cc.cassian.cauldrons;

import cc.cassian.cauldrons.config.ModConfig;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CauldronMod {
	public static final String MOD_ID = "toil_and_trouble";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModConfig CONFIG = ModConfig.createToml(PlatformMethods.getConfigDir(), "", MOD_ID, ModConfig.class);

	public static ResourceLocation of(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}


	public static void init() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Toil and trouble!");
		CauldronModRecipes.touch();
	}
}