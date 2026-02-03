package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.DippingRecipe;
import cc.cassian.cauldrons.recipe.InsertingRecipe;
import cc.cassian.cauldrons.registry.CommonRegistry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class CauldronModRecipes {
    public static final Supplier<RecipeType<BrewingRecipe>> BREWING = CommonRegistry.registerRecipe("brewing", () -> registerRecipeType("brewing"));
    public static final Supplier<RecipeType<DippingRecipe>> DIPPING = CommonRegistry.registerRecipe("dipping", () -> registerRecipeType("dipping"));
    public static final Supplier<RecipeType<InsertingRecipe>> INSERTING = CommonRegistry.registerRecipe("inserting", () -> registerRecipeType("inserting"));

    //? if >26 {
    public static final Supplier<RecipeSerializer<BrewingRecipe>> BREWING_SERIALIZER = CommonRegistry.registerRecipeSerializer("brewing", ()-> new RecipeSerializer<>(BrewingRecipe.CODEC, BrewingRecipe.STREAM_CODEC));
    public static final Supplier<RecipeSerializer<DippingRecipe>> DIPPING_SERIALIZER = CommonRegistry.registerRecipeSerializer("dipping", ()-> new RecipeSerializer<>(DippingRecipe.CODEC, DippingRecipe.STREAM_CODEC));
    public static final Supplier<RecipeSerializer<InsertingRecipe>> INSERTION_SERIALIZER = CommonRegistry.registerRecipeSerializer("inserting", ()-> new RecipeSerializer<>(InsertingRecipe.CODEC, InsertingRecipe.STREAM_CODEC));
    //?} else {
    /*public static final Supplier<BrewingRecipe.Serializer> BREWING_SERIALIZER = CommonRegistry.registerRecipeSerializer("brewing", BrewingRecipe.Serializer::new);
    public static final Supplier<DippingRecipe.Serializer> DIPPING_SERIALIZER = CommonRegistry.registerRecipeSerializer("dipping", DippingRecipe.Serializer::new);
    public static final Supplier<InsertingRecipe.Serializer> INSERTION_SERIALIZER = CommonRegistry.registerRecipeSerializer("inserting", InsertingRecipe.Serializer::new);
    *///?}


    private static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>() {
            public String toString() {
                return CauldronMod.MOD_ID + ":" + identifier;
            }
        };
    }

    public static void touch() {

    }

}
