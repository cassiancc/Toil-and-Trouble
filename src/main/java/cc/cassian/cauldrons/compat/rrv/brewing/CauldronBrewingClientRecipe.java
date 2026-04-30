package cc.cassian.cauldrons.compat.rrv.brewing;

import cc.cassian.cauldrons.compat.rrv.CauldronModRRVPlugin;
import cc.cassian.cauldrons.compat.rrv.Constants;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class CauldronBrewingClientRecipe implements ReliableClientRecipe {
    private final SlotContent reagent;
    private final CauldronContents potion;
    private final CauldronContents result;
    private final boolean heated;
    private final Identifier id;

    public CauldronBrewingClientRecipe(Identifier id, SlotContent reagent, CauldronContents potion, CauldronContents resultPotion, boolean heated) {
        this.id = id;
        this.reagent = reagent;
        this.potion = potion;
        this.result = resultPotion;
        this.heated = heated;
    }

    @Override
    public ReliableClientRecipeType getType() {
        return CauldronBrewingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindOptionalSlot(0, reagent, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(1, Constants.getResultForDisplay(potion).getB(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        if (heated) {
            slotFillContext.addAdditionalStackModifier(0, (stack, tooltip)-> {
                        tooltip.add(Component.translatable("tooltip.toil_and_trouble.heated"));
            });
			slotFillContext.addAdditionalStackModifier(1, (stack, tooltip)-> {
				tooltip.add(Component.translatable("tooltip.toil_and_trouble.heated"));
		    });
		}
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

	@Override
	public Identifier getId() {
		return id;
	}
}
