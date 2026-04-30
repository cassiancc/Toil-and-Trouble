package cc.cassian.cauldrons.compat.rrv;

import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingClientRecipe;
import cc.cassian.cauldrons.compat.rrv.alchemy.CauldronAlchemyClientRecipe;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;

public class CauldronModRRVPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        // register the client recipes
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.BREWING).forEach(recipeHolder -> {
                var recipe = recipeHolder.value();
                recipeList.add(new CauldronBrewingClientRecipe(recipeHolder.id().identifier(), SlotContent.of(recipe.getReagent()), recipe.getContents(), recipe.getResultPotion(), recipe.requiresHeat()));
            });
            ClientRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.ALCHEMY).forEach(recipeHolder -> {
                var recipe = recipeHolder.value();
                recipeList.add(new CauldronAlchemyClientRecipe(recipeHolder.id().identifier(), recipe.getReagents().stream().map(SlotContent::of).toList(), recipe.getContents(), SlotContent.of(recipe.getResultItem()), recipe.requiresHeat()));
            });
        });
    }
}
