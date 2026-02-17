package cc.cassian.cauldrons.compat.rrv.dipping;

import cc.cassian.cauldrons.compat.rrv.Constants;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;

import java.util.ArrayList;
import java.util.List;

public class CauldronDippingClientRecipe implements ReliableClientRecipe {
    private final List<SlotContent> reagents = new ArrayList<>();
    private final CauldronContents potion;
    private final SlotContent result;

    public CauldronDippingClientRecipe(CauldronDippingServerRecipe modRecipe) {
        modRecipe.getReagents().forEach(reagent -> {
            reagents.add(SlotContent.of(reagent));
        });
        this.potion = modRecipe.getPotion();
        this.result = SlotContent.of(modRecipe.getResult());
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return CauldronDippingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
		for (int i = 0; i < reagents.size(); i++) {
			SlotContent reagent = reagents.get(i);
            slotFillContext.bindOptionalSlot(i, reagent, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
		}
        slotFillContext.bindOptionalSlot(9, Constants.getResultForDisplay(potion).getB(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(10, result, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public List<SlotContent> getIngredients() {
        var list = new ArrayList<>(reagents);
        list.add(Constants.getResultForDisplay(potion).getA());
        return list;
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(result);
    }
}