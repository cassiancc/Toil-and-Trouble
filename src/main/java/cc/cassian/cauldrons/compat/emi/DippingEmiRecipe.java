package cc.cassian.cauldrons.compat.emi;

//? if <1.21.2 {

/*import cc.cassian.cauldrons.recipe.DippingRecipe;
import cc.cassian.cauldrons.registry.CauldronModItems;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class DippingEmiRecipe extends BasicEmiRecipe {

    private final EmiStack potionForDisplay;
    private final boolean requiresHeat;

    public DippingEmiRecipe(RecipeHolder<DippingRecipe> recipe, RegistryAccess registryAccess) {
        super(CauldronModEmiPlugin.DIPPING_CATEGORY, recipe.id(), 100, 18);
        // reagent
        inputs.add(EmiIngredient.of(recipe.value().getReagent()));
        // potion item
        var input = CauldronModEmiPlugin.getResultForDisplay(recipe.value().getPotion());
        inputs.add(EmiIngredient.of(input.getA().getEmiStacks()));
        potionForDisplay = input.getB();
        // output
        var result = recipe.value().getResultItem();
        outputs.add(EmiStack.of(result));
        requiresHeat = recipe.value().requiresHeat();
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        List<Component> heatedText = new ArrayList<>();
        if (requiresHeat) {
            heatedText.add(Component.translatable("tooltip.toil_and_trouble.heated"));
        }
        widgetHolder.addSlot(inputs.getFirst(), 0, 0);
        widgetHolder.addTexture(EmiTexture.PLUS, 20, 2);
        widgetHolder.addSlot(potionForDisplay, 36, 0);
        widgetHolder.addTexture(EmiTexture.FULL_ARROW, 56, 1).tooltipText(heatedText);
        widgetHolder.addSlot(getOutputs().getFirst(), 81, 0).recipeContext(this);

    }
}

*///?}