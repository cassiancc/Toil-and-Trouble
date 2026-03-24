package cc.cassian.cauldrons.compat.rrv;

import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingClientRecipe;
import cc.cassian.cauldrons.compat.rrv.alchemy.CauldronAlchemyServerRecipe;
import cc.cassian.cauldrons.compat.rrv.alchemy.CauldronAlchemyClientRecipe;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.registry.CauldronModItems;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;

import java.util.Collections;

public class CauldronModRRVPlugin implements ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        // register the server recipes
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.BREWING).forEach(recipe -> {
                recipeList.add(new CauldronBrewingServerRecipe(SlotContent.of(recipe.getReagent()), recipe.getPotion(), recipe.getResultPotion(), recipe.requiresHeat()));
            });
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.ALCHEMY).forEach(recipe -> {
                recipeList.add(new CauldronAlchemyServerRecipe(recipe.getReagents().stream().map(SlotContent::of).toList(), recipe.getPotion(), SlotContent.of(recipe.getResultItem()), recipe.requiresHeat()));
            });
        });

        // and all the client recipes
        ItemView.addClientRecipeWrapper(CauldronBrewingServerRecipe.TYPE, modRecipe -> {
			return Collections.singletonList(new CauldronBrewingClientRecipe(modRecipe));
		});
        ItemView.addClientRecipeWrapper(CauldronAlchemyServerRecipe.TYPE, modRecipe -> {
			return Collections.singletonList(new CauldronAlchemyClientRecipe(modRecipe));
		});

        // hide cauldron contents
        ItemView.excludeItem(CauldronModItems.CAULDRON_CONTENTS);
    }
}
