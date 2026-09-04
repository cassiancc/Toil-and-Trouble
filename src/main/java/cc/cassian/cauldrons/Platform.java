package cc.cassian.cauldrons;

//? if neoforge {
/*import cc.cassian.cauldrons.neoforge.CauldronModNeoForge;
import cc.cassian.cauldrons.neoforge.client.CauldronModNeoForgeClient;
*///?}
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class Platform {

	public static <T extends Recipe<R>, R extends RecipeInput> Optional<RecipeHolder<T>> getFirstRecipe(RecipeType<T> brewing, R input, Level level) {
		return level.getRecipeManager().getRecipeFor(brewing, input, level);
	}
}
