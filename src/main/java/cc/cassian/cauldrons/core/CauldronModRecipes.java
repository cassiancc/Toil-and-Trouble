package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.InsertingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CauldronModRecipes {
    public static final RecipeType<BrewingRecipe> BREWING = CauldronMod.REGISTRAR.registerRecipeType("brewing");
    public static final RecipeType<AlchemyRecipe> ALCHEMY = CauldronMod.REGISTRAR.registerRecipeType("alchemy");
    public static final RecipeType<InsertingRecipe> INSERTING = CauldronMod.REGISTRAR.registerRecipeType("inserting");

    public static final RecipeSerializer<BrewingRecipe> BREWING_SERIALIZER = registerRecipeSerializer("brewing", new RecipeSerializer<>(BrewingRecipe.CODEC, BrewingRecipe.STREAM_CODEC));
    public static final RecipeSerializer<AlchemyRecipe> ALCHEMY_SERIALIZER = registerRecipeSerializer("alchemy", new RecipeSerializer<>(AlchemyRecipe.CODEC, AlchemyRecipe.STREAM_CODEC));
    public static final RecipeSerializer<InsertingRecipe> INSERTION_SERIALIZER = registerRecipeSerializer("inserting", new RecipeSerializer<>(InsertingRecipe.CODEC, InsertingRecipe.STREAM_CODEC));

    public static <B extends RecipeSerializer<?>> B registerRecipeSerializer(String name, B supplier) {
        return CauldronMod.REGISTRAR.register(name, supplier, BuiltInRegistries.RECIPE_SERIALIZER);
    }

    public static void touch() {
        BuiltInRegistries.RECIPE_TYPE.addAlias(CauldronMod.of("dipping"), CauldronMod.of("alchemy"));
        BuiltInRegistries.RECIPE_SERIALIZER.addAlias(CauldronMod.of("dipping"), CauldronMod.of("alchemy"));
    }

}
