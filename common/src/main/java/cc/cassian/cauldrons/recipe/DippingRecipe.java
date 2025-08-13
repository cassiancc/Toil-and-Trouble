package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class DippingRecipe implements Recipe<BrewingRecipeInput> {

    private final Ingredient reagent;
    private final PotionContents potion;
    private final ItemStack result;

    public DippingRecipe(Ingredient reagent, PotionContents potion, ItemStack result) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        return reagent.test(input.getItem(0)) && input.getPotionContents().is(getPotion());
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public Holder<Potion> getPotion() {
        return potion.potion().get();
    }

    public ItemStack getResultItem() {
        return result.copy();
    }

    @Override
    public RecipeSerializer<DippingRecipe> getSerializer() {
        return CauldronModRecipes.DIPPING_SERIALIZER.get();
    }

    @Override
    public RecipeType<DippingRecipe> getType() {
        return CauldronModRecipes.DIPPING.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    public static class Serializer implements RecipeSerializer<DippingRecipe> {
        public static final MapCodec<DippingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(r->r.reagent),
                PotionContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                ItemStack.CODEC.fieldOf("result").forGetter(r->r.result)
        ).apply(inst, DippingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DippingRecipe> STREAM_CODEC = StreamCodec.of(DippingRecipe.Serializer::toNetwork, DippingRecipe.Serializer::fromNetwork);

        private static DippingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var potion = PotionContents.STREAM_CODEC.decode(buf);
            var result = ItemStack.STREAM_CODEC.decode(buf);
            return new DippingRecipe(reagent, potion, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, DippingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.reagent);
            PotionContents.STREAM_CODEC.encode(buf, recipe.potion);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }

        @Override
        public MapCodec<DippingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DippingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
