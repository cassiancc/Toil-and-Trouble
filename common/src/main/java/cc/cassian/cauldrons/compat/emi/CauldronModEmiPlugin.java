package cc.cassian.cauldrons.compat.emi;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class CauldronModEmiPlugin implements EmiPlugin {

    public static final EmiStack CAULDRON_WORKSTATION = EmiStack.of(Items.CAULDRON);

    public static final EmiRecipeCategory CAULDRON_CATEGORY
            = new EmiRecipeCategory(CauldronMod.of("brewing"), CAULDRON_WORKSTATION);

    @Override
    public void register(EmiRegistry registry) {
        RecipeManager manager = registry.getRecipeManager();
        var access = Minecraft.getInstance().level.registryAccess();

        registry.addCategory(CAULDRON_CATEGORY);
        registry.addWorkstation(CAULDRON_CATEGORY, CAULDRON_WORKSTATION);
//        for (RecipeHolder<BrewingRecipe> recipe : manager.getAllRecipesFor(CauldronModRecipes.BREWING.get())) {
//            registry.addRecipe(new BrewingEmiRecipe(recipe, access));
//        }

    }
}
