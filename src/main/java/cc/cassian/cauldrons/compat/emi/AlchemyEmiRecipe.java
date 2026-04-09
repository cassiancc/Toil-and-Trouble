package cc.cassian.cauldrons.compat.emi;

//? if <1.21.2 {

import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class AlchemyEmiRecipe extends BasicEmiRecipe {

    private final EmiStack potionForDisplay;
    private final boolean requiresHeat;
    private final List<Ingredient> reagents;

    public AlchemyEmiRecipe(RecipeHolder<AlchemyRecipe> recipe, RegistryAccess registryAccess) {
        super(CauldronModEmiPlugin.ALCHEMY_CATEGORY, recipe.id(), 100, height(recipe.value().getReagents().size()));
        // reagent
        this.reagents = recipe.value().getReagents();
        for (Ingredient reagent : recipe.value().getReagents()) {
            inputs.add(EmiIngredient.of(reagent));
        }
        // potion item
        var input = CauldronModEmiPlugin.getResultForDisplay(recipe.value().getContents());
        inputs.add(EmiIngredient.of(input.getA().getEmiStacks()));
        potionForDisplay = input.getB();
        // output
        var result = recipe.value().getResultItem();
        outputs.add(EmiStack.of(result));
        requiresHeat = recipe.value().requiresHeat();
    }

    public static int height(int size) {
        if (size > 6) {
            return 60;
        } else {
            return 40;
        }
    }

    public static int potionHeight(int size) {
        if (size > 6) {
            return 37;
        } else {
            return 20;
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        int positionPosition = potionHeight(reagents.size());
        List<Component> heatedText = new ArrayList<>();
        if (requiresHeat) {
            heatedText.add(Component.translatable("tooltip.toil_and_trouble.heated"));
        }
        int i = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (i < reagents.size())
                    widgetHolder.addSlot(EmiIngredient.of(reagents.get(i++)), 1 + x * 18, 1 + y * 18);
            }
        }
        widgetHolder.addSlot(potionForDisplay, 59, positionPosition);
        widgetHolder.addTexture(EmiTexture.FULL_ARROW, 55, 1).tooltipText(heatedText);
        widgetHolder.addSlot(getOutputs().getFirst(), 80, 1).recipeContext(this);

    }
}

//?}