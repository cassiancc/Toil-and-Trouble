package cc.cassian.cauldrons.compat.eiv.dipping;

import cc.cassian.cauldrons.compat.eiv.CauldronModEIVPlugin;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.registry.CauldronModItems;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class CauldronDippingViewRecipe implements IEivViewRecipe {
    private final SlotContent reagent;
    private final CauldronContents potion;
    private final SlotContent result;

    public CauldronDippingViewRecipe(CauldronDippingServerRecipe modRecipe) {
        this.reagent = SlotContent.of(modRecipe.getReagent());
        this.potion = modRecipe.getPotion();
        this.result = SlotContent.of(modRecipe.getResult());
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return CauldronDippingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindOptionalSlot(0, reagent, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(1, CauldronModEIVPlugin.getResultForDisplay(potion).getB(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(2, result, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(reagent, CauldronModEIVPlugin.getResultForDisplay(potion).getA());
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(result);
    }
}
