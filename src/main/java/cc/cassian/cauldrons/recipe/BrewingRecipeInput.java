package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class BrewingRecipeInput implements RecipeInput {

    private final List<ItemStack> reagent;
    private final CauldronContents contents;
    private final boolean isHeated;

    public BrewingRecipeInput(List<ItemStack> reagent, CauldronContents contents, boolean isHeated) {
        this.reagent = reagent;
        this.contents = contents;
        this.isHeated = isHeated;
    }

    @Override
    public ItemStack getItem(int index) {
        return reagent.get(index);
    }

    public CauldronContents getContents() {
        return contents;
    }

    @Override
    public int size() {
        return reagent.size();
    }

    public boolean isHeated() {
        return isHeated;
    }
}
