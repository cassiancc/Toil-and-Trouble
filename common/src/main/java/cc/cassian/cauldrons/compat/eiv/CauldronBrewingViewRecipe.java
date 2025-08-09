package cc.cassian.cauldrons.compat.eiv;

import cc.cassian.cauldrons.registry.CauldronModItems;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class CauldronBrewingViewRecipe implements IEivViewRecipe {
    private final SlotContent reagent;
    private final PotionContents potion;
    private final PotionContents result;

    public CauldronBrewingViewRecipe(CauldronBrewingServerRecipe modRecipe) {
        this.reagent = SlotContent.of(modRecipe.getReagent());
        this.potion = modRecipe.getPotion();
        this.result = modRecipe.getResult();
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return CauldronBrewingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindOptionalSlot(0, reagent, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(1, SlotContent.of(PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), potion.potion().get())), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(2, SlotContent.of(PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), result.potion().get())), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(reagent, SlotContent.of(PotionContents.createItemStack(Items.POTION, potion.potion().get())));
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(SlotContent.of(PotionContents.createItemStack(Items.POTION, result.potion().get())));
    }
}
