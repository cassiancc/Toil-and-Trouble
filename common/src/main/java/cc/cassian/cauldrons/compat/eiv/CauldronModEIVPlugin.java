package cc.cassian.cauldrons.compat.eiv;

import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.registry.CauldronModItems;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.builtin.brewing.BrewingServerRecipe;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.Collections;

public class CauldronModEIVPlugin implements IExtendedItemViewIntegration {
    @Override
    public void onIntegrationInitialize() {
        // register the server recipes
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.BREWING.get()).forEach(recipe -> {
                recipeList.add(new CauldronBrewingServerRecipe(recipe.getReagent(), new PotionContents(recipe.getPotion()), recipe.getResultPotion()));
            });
        });

        // and all the client recipes
        ItemView.registerRecipeWrapper(CauldronBrewingServerRecipe.TYPE, modRecipe -> {
            return Collections.singletonList(new CauldronBrewingViewRecipe(modRecipe));
        });

        // hide cauldron contents
        // TODO move this into an event if/when neoforge 1.21.8
        ItemView.excludeItem(CauldronModItems.CAULDRON_CONTENTS.get());
    }
}
