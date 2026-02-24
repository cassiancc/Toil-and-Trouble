package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.core.CauldronModRecipes;

import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

public class JeiRecipeHelpers {

	private final RecipeManager synchronizedRecipes;
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
		return List.copyOf(synchronizedRecipes.getAllRecipesFor(CauldronModRecipes.BREWING));
	}

	public List<RecipeHolder<AlchemyRecipe>> getDippingRecipes() {
		return List.copyOf(synchronizedRecipes.getAllRecipesFor(CauldronModRecipes.ALCHEMY));
	}



}
