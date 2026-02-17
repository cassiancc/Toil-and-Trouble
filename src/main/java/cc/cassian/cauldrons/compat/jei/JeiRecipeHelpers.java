package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.DippingRecipe;
//? if fabric && >1.21.2
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

public class JeiRecipeHelpers {
	//? if >1.21.2 {
	private final SynchronizedRecipes synchronizedRecipes;
	public JeiRecipeHelpers() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;

		if (level != null) {
			synchronizedRecipes = level.recipeAccess().getSynchronizedRecipes();
		} else {
			throw new NullPointerException("minecraft world must not be null.");
		}
	}

	public List<RecipeHolder<BrewingRecipe>> getBrewingRecipes() {
		return List.copyOf(synchronizedRecipes.getAllOfType(CauldronModRecipes.BREWING));
	}

	public List<RecipeHolder<DippingRecipe>> getDippingRecipes() {
		return List.copyOf(synchronizedRecipes.getAllOfType(CauldronModRecipes.DIPPING));
	}
	//?} else {
	/*private final RecipeManager synchronizedRecipes;
	public JeiRecipeHelpers() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;

		if (level != null) {
			synchronizedRecipes = level.getRecipeManager();
		} else {
			throw new NullPointerException("minecraft world must not be null.");
		}
	}

	public List<RecipeHolder<BrewingRecipe>> getBrewingRecipes() {
		return synchronizedRecipes.getAllRecipesFor(CauldronModRecipes.BREWING.get());
	}

	public List<RecipeHolder<DippingRecipe>> getDippingRecipes() {
		return synchronizedRecipes.getAllRecipesFor(CauldronModRecipes.DIPPING.get());
	}
	*///?}



}
