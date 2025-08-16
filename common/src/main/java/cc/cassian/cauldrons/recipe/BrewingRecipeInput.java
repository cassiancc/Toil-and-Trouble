package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class BrewingRecipeInput implements RecipeInput {

    private final ItemStack reagent;
    private final CauldronContents potion;
    private final boolean isHeated;

    public BrewingRecipeInput(ItemStack reagent, CauldronContents potion, boolean isHeated) {
        this.reagent = reagent;
        this.potion = potion;
        this.isHeated = isHeated;
    }

    @Override
    public ItemStack getItem(int index) {
        return reagent;
    }

    public CauldronContents getPotionContents() {
        return potion;
    }

    @Override
    public int size() {
        return 2;
    }

    public boolean isHeated() {
        return isHeated;
    }
}
