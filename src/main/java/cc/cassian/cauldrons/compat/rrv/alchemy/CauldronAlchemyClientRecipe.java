package cc.cassian.cauldrons.compat.rrv.alchemy;

import cc.cassian.cauldrons.compat.rrv.Constants;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CauldronAlchemyClientRecipe implements ReliableClientRecipe {
    private final List<SlotContent> reagents = new ArrayList<>();
    private final CauldronContents potion;
    private final SlotContent result;
    private final boolean heated;
    private final Identifier id;

    public CauldronAlchemyClientRecipe(Identifier identifier, List<SlotContent> list, CauldronContents contents, SlotContent result, boolean heated) {
		this.id = identifier;
        reagents.addAll(list);
        this.potion = contents;
        this.result = result;
        this.heated = heated;
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return CauldronAlchemyClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
		for (int i = 0; i < reagents.size(); i++) {
			SlotContent reagent = reagents.get(i);
            slotFillContext.bindOptionalSlot(i, reagent, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
		}
        slotFillContext.bindOptionalSlot(9, Constants.getResultForDisplay(potion).getB(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        if (heated)
            slotFillContext.addAdditionalStackModifier(9, (stack, tooltip)-> {
                tooltip.add(Component.translatable("tooltip.toil_and_trouble.heated"));
        });
        slotFillContext.bindSlot(10, result);
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

	@Override
	public Identifier getId() {
		return id;
	}
}