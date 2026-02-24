package cc.cassian.cauldrons;

//? if fabric
import net.fabricmc.loader.api.FabricLoader;
//? if neoforge {
/*import cc.cassian.cauldrons.neoforge.CauldronModNeoForge;
import cc.cassian.cauldrons.neoforge.client.CauldronModNeoForgeClient;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
*///?}
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.nio.file.Path;
import java.util.Optional;

public class Platform {
    public static Path getConfigDir() {
        //? if fabric {
        return FabricLoader.getInstance().getConfigDir();
        //?} else {
        /*return FMLPaths.CONFIGDIR.get();
        *///?}
    }

	public static boolean isModLoaded(String mod) {
		//? fabric
		return FabricLoader.getInstance().isModLoaded(mod);
		//? neoforge
		//return ModList.get().isLoaded(mod);
	}

	public static boolean isDev() {
		//? fabric
		return FabricLoader.getInstance().isDevelopmentEnvironment();
		//? neoforge
		//return !FMLEnvironment.isProduction();
	}

	public static <T extends Recipe<BrewingRecipeInput>> Optional<RecipeHolder<T>> getFirstRecipe(RecipeType<T> brewing, BrewingRecipeInput input, Level level) {
		return level.getRecipeManager().getRecipeFor(brewing, input, level);
	}
}
