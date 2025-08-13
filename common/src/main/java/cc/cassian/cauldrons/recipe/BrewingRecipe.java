package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
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
    private final int brewingTime;

    public BrewingRecipe(Ingredient reagent, PotionContents potion, PotionContents result, int brewingTime) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
        this.brewingTime = brewingTime;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        return reagent.test(input.getItem(0)) && input.getPotionContents().is(getPotion());
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(reagent);
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public Holder<Potion> getPotion() {
        return potion.potion().get();
    }

    public int getBrewingTime() {
        return brewingTime;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(r->r.reagent),
                PotionContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                PotionContents.CODEC.fieldOf("result").forGetter(r->r.result),
                Codec.INT.optionalFieldOf("brewing_time", CauldronMod.CONFIG.brewingTime.value()).forGetter(r -> r.brewingTime)
        ).apply(inst, BrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.of(BrewingRecipe.Serializer::toNetwork, BrewingRecipe.Serializer::fromNetwork);

        private static BrewingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var potion = PotionContents.STREAM_CODEC.decode(buf);
            var result = PotionContents.STREAM_CODEC.decode(buf);
            var brewingTime = buf.readInt();
            return new BrewingRecipe(reagent, potion, result, brewingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BrewingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.reagent);
            PotionContents.STREAM_CODEC.encode(buf, recipe.potion);
            PotionContents.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeInt(recipe.brewingTime);
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
