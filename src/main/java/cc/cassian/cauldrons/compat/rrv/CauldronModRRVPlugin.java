package cc.cassian.cauldrons.compat.rrv;

//? if >1.21.10 {

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingClientRecipe;
import cc.cassian.cauldrons.compat.rrv.dipping.CauldronDippingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.dipping.CauldronDippingViewRecipe;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.registry.CauldronModItems;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.util.Collections;
import java.util.Map;

public class CauldronModRRVPlugin implements ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        // register the server recipes
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.BREWING).forEach(recipe -> {
                recipeList.add(new CauldronBrewingServerRecipe(recipe.getReagent(), recipe.getPotion(), recipe.getResultPotion()));
            });
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.DIPPING).forEach(recipe -> {
                recipeList.add(new CauldronDippingServerRecipe(recipe.getReagent(), recipe.getPotion(), recipe.getResultItem()));
            });
        });

        // and all the client recipes
        ItemView.addClientRecipeWrapper(CauldronBrewingServerRecipe.TYPE, modRecipe -> {
			return Collections.singletonList(new CauldronBrewingClientRecipe(modRecipe));
		});
        ItemView.addClientRecipeWrapper(CauldronDippingServerRecipe.TYPE, modRecipe -> {
			return Collections.singletonList(new CauldronDippingViewRecipe(modRecipe));
		});

        // hide cauldron contents
        ItemView.excludeItem(CauldronModItems.CAULDRON_CONTENTS.get());
    }
}
//?}