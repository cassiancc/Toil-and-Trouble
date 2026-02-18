package cc.cassian.cauldrons.fabric;

//? if fabric {

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModItems;
import cc.cassian.cauldrons.registry.CauldronModSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;

public final class CauldronModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CauldronMod.init();
        CauldronModBlocks.touch();
        CauldronModItems.touch();
        CauldronModBlockEntityTypes.touch();
        CauldronModSoundEvents.touch();
        CauldronModRecipes.touch();

		RecipeSynchronization.synchronizeRecipeSerializer(CauldronModRecipes.BREWING_SERIALIZER);
		RecipeSynchronization.synchronizeRecipeSerializer(CauldronModRecipes.INSERTION_SERIALIZER);
		RecipeSynchronization.synchronizeRecipeSerializer(CauldronModRecipes.ALCHEMY_SERIALIZER);
    }
}

//?}