package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronContents;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class BrewingRecipeInput implements RecipeInput {

    private final List<ItemStack> items;
    private final CauldronContents contents;
    private final StackedContents stackedContents = new StackedContents();
    private final boolean isHeated;
    private final int ingredientCount;

    public BrewingRecipeInput(List<ItemStack> items, CauldronContents contents, boolean isHeated) {
        this.items = items;
        this.contents = contents;
        this.isHeated = isHeated;
        int ingredientCount = 0;
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                ingredientCount++;
                this.stackedContents.accountStack(item, 1);
            }
        }
        this.ingredientCount = ingredientCount;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    public CauldronContents getContents() {
        return contents;
    }

    @Override
    public int size() {
        return items.size();
    }

    public boolean isHeated() {
        return isHeated;
    }

    public StackedContents stackedContents() {
        return this.stackedContents;
    }

    public List<ItemStack> items() {
        return this.items;
    }

	public int ingredientAmount() {
		return ingredientCount;
	}
}
