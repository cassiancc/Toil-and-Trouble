package cc.cassian.cauldrons.compat.rrv.brewing;
//? if >1.21.10 {

import cc.cassian.cauldrons.compat.rrv.CauldronModRRVPlugin;
import cc.cassian.cauldrons.compat.rrv.Constants;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;

import java.util.List;

public class CauldronBrewingClientRecipe implements ReliableClientRecipe {
    private final SlotContent reagent;
    private final CauldronContents potion;
    private final CauldronContents result;

    public CauldronBrewingClientRecipe(CauldronBrewingServerRecipe modRecipe) {
        this.reagent = SlotContent.of(modRecipe.getReagent());
        this.potion = modRecipe.getPotion();
        this.result = modRecipe.getResult();
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return CauldronBrewingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindOptionalSlot(0, reagent, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(1, Constants.getResultForDisplay(potion).getB(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(2, Constants.getResultForDisplay(result).getB(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(reagent, Constants.getResultForDisplay(potion).getA());
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(Constants.getResultForDisplay(result).getA());
    }
}
//?}