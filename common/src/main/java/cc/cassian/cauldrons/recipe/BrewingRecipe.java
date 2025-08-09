package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BrewingRecipe implements Recipe<BrewingRecipeInput> {

    private final Ingredient reagent;
    private final PotionContents potion;
    private final PotionContents result;

    public BrewingRecipe(Ingredient reagent, PotionContents potion, PotionContents result) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        if (reagent.test(input.getItem(0)) && input.getPotionContents().is(getPotion())) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem();
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public Holder<Potion> getPotion() {
        return potion.potion().get();
    }

    public ItemStack getResultItem() {
        return PotionContents.createItemStack(Items.POTION, result.potion().get());
    }

    public PotionContents getResultPotion(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public RecipeSerializer<BrewingRecipe> getSerializer() {
        return CauldronModRecipes.BREWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<BrewingRecipe> getType() {
        return CauldronModRecipes.BREWING.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(r->r.reagent),
                PotionContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                PotionContents.CODEC.fieldOf("result").forGetter(r->r.result)
        ).apply(inst, BrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.of(BrewingRecipe.Serializer::toNetwork, BrewingRecipe.Serializer::fromNetwork);

        private static BrewingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var potion = PotionContents.STREAM_CODEC.decode(buf);
            var result = PotionContents.STREAM_CODEC.decode(buf);
            return new BrewingRecipe(reagent, potion, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BrewingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.reagent);
            PotionContents.STREAM_CODEC.encode(buf, recipe.potion);
            PotionContents.STREAM_CODEC.encode(buf, recipe.result);
        }

        @Override
        public MapCodec<BrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
