package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.core.CauldronModRecipes;

import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
//? if fabric {
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
//?} else {
/*import cc.cassian.cauldrons.neoforge.client.CauldronModNeoForgeClient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class JeiRecipeHelpers {
	//? if fabric {
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

	public List<RecipeHolder<AlchemyRecipe>> getDippingRecipes() {
		return List.copyOf(synchronizedRecipes.getAllOfType(CauldronModRecipes.ALCHEMY));
	}
	//?} else {
	/*private final RecipeMap synchronizedRecipes;
	public JeiRecipeHelpers() {
		synchronizedRecipes = CauldronModNeoForgeClient.map;
	}

	public List<RecipeHolder<BrewingRecipe>> getBrewingRecipes() {
		return List.copyOf(synchronizedRecipes.byType(CauldronModRecipes.BREWING));
	}

	public List<RecipeHolder<AlchemyRecipe>> getDippingRecipes() {
		return List.copyOf(synchronizedRecipes.byType(CauldronModRecipes.ALCHEMY));
	}
	*///?}



}
