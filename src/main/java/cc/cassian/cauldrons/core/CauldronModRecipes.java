package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.InsertingRecipe;
import cc.cassian.cauldrons.registry.CommonRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CauldronModRecipes {
    public static final RecipeType<BrewingRecipe> BREWING = CommonRegistry.registerRecipe("brewing", () -> registerRecipeType("brewing"));
    public static final RecipeType<AlchemyRecipe> ALCHEMY = CommonRegistry.registerRecipe("alchemy", () -> registerRecipeType("alchemy"));
    public static final RecipeType<InsertingRecipe> INSERTING = CommonRegistry.registerRecipe("inserting", () -> registerRecipeType("inserting"));

    public static final RecipeSerializer<BrewingRecipe> BREWING_SERIALIZER = CommonRegistry.registerRecipeSerializer("brewing", ()-> new RecipeSerializer<>(BrewingRecipe.CODEC, BrewingRecipe.STREAM_CODEC));
    public static final RecipeSerializer<AlchemyRecipe> ALCHEMY_SERIALIZER = CommonRegistry.registerRecipeSerializer("alchemy", ()-> new RecipeSerializer<>(AlchemyRecipe.CODEC, AlchemyRecipe.STREAM_CODEC));
    public static final RecipeSerializer<InsertingRecipe> INSERTION_SERIALIZER = CommonRegistry.registerRecipeSerializer("inserting", ()-> new RecipeSerializer<>(InsertingRecipe.CODEC, InsertingRecipe.STREAM_CODEC));


    private static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>() {
            public String toString() {
                return CauldronMod.MOD_ID + ":" + identifier;
            }
        };
    }

    public static void touch() {
        BuiltInRegistries.RECIPE_TYPE.addAlias(CauldronMod.of("dipping"), CauldronMod.of("alchemy"));
        BuiltInRegistries.RECIPE_SERIALIZER.addAlias(CauldronMod.of("dipping"), CauldronMod.of("alchemy"));
    }

}
