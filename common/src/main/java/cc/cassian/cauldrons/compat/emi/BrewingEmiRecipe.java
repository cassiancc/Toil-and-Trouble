package cc.cassian.cauldrons.compat.emi;

import cc.cassian.cauldrons.recipe.BrewingRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BrewingEmiRecipe extends BasicEmiRecipe {

    private final EmiStack potionForDisplay;
    private final EmiStack resultForDisplay;

    public BrewingEmiRecipe(RecipeHolder<BrewingRecipe> recipeHolder, RegistryAccess registryAccess) {
        super(CauldronModEmiPlugin.BREWING_CATEGORY, recipeHolder.id().location(), 100, 18);
        var recipe = recipeHolder.value();
        // reagent
        inputs.add(EmiIngredient.of(recipe.getReagent()));
        // potion item
        var input = CauldronModEmiPlugin.getResultForDisplay(recipe.getPotion());
        inputs.add(EmiIngredient.of(input.getA().getEmiStacks()));
        potionForDisplay = input.getB();
        // output
        var output = CauldronModEmiPlugin.getResultForDisplay(recipe.getResultPotion());
        outputs.add(output.getA());
        resultForDisplay = output.getB();
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addSlot(inputs.getFirst(), 0, 0);
        widgetHolder.addTexture(EmiTexture.PLUS, 20, 2);
        widgetHolder.addSlot(potionForDisplay, 36, 0);
        widgetHolder.addTexture(EmiTexture.FULL_ARROW, 56, 1);
        widgetHolder.addSlot(resultForDisplay, 81, 0).recipeContext(this);
    }
}
