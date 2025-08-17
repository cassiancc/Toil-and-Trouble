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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class DippingRecipe implements Recipe<BrewingRecipeInput> {

    private final Ingredient reagent;
    private final CauldronContents potion;
    private final ItemStack result;
    private final ParticleOptions particleType;
    private final boolean requiresHeat;

    public DippingRecipe(Ingredient reagent, CauldronContents potion, ItemStack result, ParticleOptions particleType, boolean requiresHeat) {
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
        return this.result.copy();
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public CauldronContents getPotion() {
        return potion;
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

    public ParticleOptions getParticleType() {
        return particleType;
    }

    public static class Serializer implements RecipeSerializer<DippingRecipe> {
        public static final MapCodec<DippingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(r->r.reagent),
                CauldronContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                ItemStack.CODEC.fieldOf("result").forGetter(r->r.result),
                ParticleTypes.CODEC.optionalFieldOf("particle_type", ParticleTypes.BUBBLE).forGetter(r->r.particleType),
                Codec.BOOL.optionalFieldOf("requires_heat", CauldronMod.CONFIG.requiresHeat.value()).forGetter(r->r.requiresHeat)
        ).apply(inst, DippingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DippingRecipe> STREAM_CODEC = StreamCodec.of(DippingRecipe.Serializer::toNetwork, DippingRecipe.Serializer::fromNetwork);

        private static DippingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var potion = CauldronContents.STREAM_CODEC.decode(buf);
            var result = ItemStack.STREAM_CODEC.decode(buf);
            var particleType = ParticleTypes.STREAM_CODEC.decode(buf);
            var requiresHeat = buf.readBoolean();
            return new DippingRecipe(reagent, potion, result, particleType, requiresHeat);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, DippingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.reagent);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.potion);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            ParticleTypes.STREAM_CODEC.encode(buf, recipe.particleType);
            buf.writeBoolean(recipe.requiresHeat);
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
