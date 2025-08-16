package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
    private final CauldronContents potion;
    private final CauldronContents result;
    private final ParticleOptions particleType;
    private final boolean requiresHeat;

    public BrewingRecipe(Ingredient reagent, CauldronContents potion, CauldronContents result, ParticleOptions particleType, boolean requiresHeat) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
        this.particleType = particleType;
        this.requiresHeat = requiresHeat;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        if (!requiresHeat || input.isHeated())
            return reagent.test(input.getItem(0)) && potion.test(input.getContents());
        return false;
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

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return PotionContents.createItemStack(Items.POTION, result.potion().get());
    }

    public CauldronContents getResultPotion(HolderLookup.Provider registries) {
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

    public ParticleOptions getParticleType() {
        return particleType;
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(r->r.reagent),
                CauldronContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                CauldronContents.CODEC.fieldOf("result").forGetter(r->r.result),
                ParticleTypes.CODEC.optionalFieldOf("particle_type", ParticleTypes.BUBBLE).forGetter(r->r.particleType),
                Codec.BOOL.optionalFieldOf("requires_heat", CauldronMod.CONFIG.requiresHeat.value()).forGetter(r->r.requiresHeat)
        ).apply(inst, BrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.of(BrewingRecipe.Serializer::toNetwork, BrewingRecipe.Serializer::fromNetwork);

        private static BrewingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var potion = CauldronContents.STREAM_CODEC.decode(buf);
            var result = CauldronContents.STREAM_CODEC.decode(buf);
            var particleType = ParticleTypes.STREAM_CODEC.decode(buf);
            var requiresHeat = buf.readBoolean();
            return new BrewingRecipe(reagent, potion, result, particleType, requiresHeat);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BrewingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.reagent);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.potion);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.result);
            ParticleTypes.STREAM_CODEC.encode(buf, recipe.particleType);
            buf.writeBoolean(recipe.requiresHeat);
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
