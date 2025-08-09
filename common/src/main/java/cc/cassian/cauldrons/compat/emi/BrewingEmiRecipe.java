package cc.cassian.cauldrons.compat.emi;

import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.registry.CauldronModItems;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BrewingEmiRecipe extends BasicEmiRecipe {

    private final ItemStack potionForDisplay;
    private final ItemStack resultForDisplay;

    public BrewingEmiRecipe(RecipeHolder<BrewingRecipe> recipe, RegistryAccess registryAccess) {
        super(CauldronModEmiPlugin.CAULDRON_CATEGORY, recipe.id(), 100, 18);
        // reagent
        inputs.add(EmiIngredient.of(recipe.value().getReagent()));
        // potion item
        var potion = PotionContents.createItemStack(Items.POTION, recipe.value().getPotion());
        inputs.add(EmiStack.of(potion));
        this.potionForDisplay = PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), recipe.value().getPotion());
        // output
        var result = recipe.value().getResultItem(registryAccess);
        outputs.add(EmiStack.of(result));
        this.resultForDisplay = PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), recipe.value().getResultPotion(registryAccess).potion().get());
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addSlot(inputs.getFirst(), 0, 0);
        widgetHolder.addTexture(EmiTexture.PLUS, 20, 2);
        widgetHolder.addSlot(EmiStack.of(potionForDisplay), 36, 0);
        widgetHolder.addTexture(EmiTexture.FULL_ARROW, 56, 1);
        widgetHolder.addSlot(EmiStack.of(resultForDisplay), 81, 0).recipeContext(this);

    }
}
