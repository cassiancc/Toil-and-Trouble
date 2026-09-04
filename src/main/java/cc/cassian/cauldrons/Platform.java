package cc.cassian.cauldrons;

//? if neoforge {
/*import cc.cassian.cauldrons.neoforge.CauldronModNeoForge;
import cc.cassian.cauldrons.neoforge.client.CauldronModNeoForgeClient;
*///?}
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class Platform {

	public static <T extends Recipe<BrewingRecipeInput>> Optional<RecipeHolder<T>> getFirstRecipe(RecipeType<T> brewing, BrewingRecipeInput input, Level level) {
		if (level instanceof ServerLevel serverLevel)
			return serverLevel.recipeAccess().getRecipeFor(brewing, input, level);
		//? fabric
		return level.recipeAccess().getSynchronizedRecipes().getFirstMatch(brewing, input, level);
		//? neoforge {
		/*return CauldronModNeoForgeClient.map.getRecipesFor(brewing, input, level).findFirst();
		*///?}
	}
}
