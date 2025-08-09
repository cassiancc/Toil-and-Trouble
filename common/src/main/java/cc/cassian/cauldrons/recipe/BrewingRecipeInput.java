package cc.cassian.cauldrons.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeInput;

public class BrewingRecipeInput implements RecipeInput {

    private final ItemStack reagent;
    private final PotionContents potion;

    public BrewingRecipeInput(ItemStack reagent, PotionContents potion) {
        this.reagent = reagent;
        this.potion = potion;
    }

    @Override
    public ItemStack getItem(int index) {
        return reagent;
    }

    public PotionContents getPotionContents() {
        return potion;
    }

    @Override
    public int size() {
        return 2;
    }
}
