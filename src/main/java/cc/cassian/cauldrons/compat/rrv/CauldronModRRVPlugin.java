package cc.cassian.cauldrons.compat.rrv;

//? if >1.21.10 {

import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingClientRecipe;
import cc.cassian.cauldrons.compat.rrv.dipping.CauldronDippingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.dipping.CauldronDippingClientRecipe;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.registry.CauldronModItems;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;

import java.util.Collections;

public class CauldronModRRVPlugin implements ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        // register the server recipes
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.BREWING).forEach(recipe -> {
                recipeList.add(new CauldronBrewingServerRecipe(recipe.getReagent(), recipe.getPotion(), recipe.getResultPotion()));
            });
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.DIPPING).forEach(recipe -> {
                recipeList.add(new CauldronDippingServerRecipe(recipe.getReagents(), recipe.getPotion(), recipe.getResultItem()));
            });
        });

        // and all the client recipes
        ItemView.addClientRecipeWrapper(CauldronBrewingServerRecipe.TYPE, modRecipe -> {
			return Collections.singletonList(new CauldronBrewingClientRecipe(modRecipe));
		});
        ItemView.addClientRecipeWrapper(CauldronDippingServerRecipe.TYPE, modRecipe -> {
			return Collections.singletonList(new CauldronDippingClientRecipe(modRecipe));
		});

        // hide cauldron contents
        ItemView.excludeItem(CauldronModItems.CAULDRON_CONTENTS.get());
    }
}
//?}