package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BrewingRecipe implements Recipe<BrewingRecipeInput> {

    private final Ingredient reagent;
    private final CauldronContents contents;
    private final CauldronContents result;
    private final ParticleOptions particleType;
    private final boolean requiresHeat;
    private final boolean placeAsBlock;

    public BrewingRecipe(Ingredient reagent, CauldronContents contents, CauldronContents result, ParticleOptions particleType, boolean requiresHeat, boolean placeAsBlock) {
        this.reagent = reagent;
        this.contents = contents;
        this.result = result;
        this.particleType = particleType;
        this.requiresHeat = requiresHeat;
        this.placeAsBlock = placeAsBlock;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        if (!requiresHeat() || input.isHeated())
            return reagent.test(input.getItem(0)) && contents.test(input.getContents());
        return false;
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input, HolderLookup.Provider registries) {
        return assemble(input);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getResultItem().copy();
    }

    public ItemStack assemble(BrewingRecipeInput input) {
        return getResultItem();
    }

    public boolean requiresHeat() {
        return requiresHeat;
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public CauldronContents getContents() {
        return contents;
    }

    public ResourceLocation getContentsId() {
        return contents.id();
    }

    public boolean tryPlaceAsBlock() {
        return placeAsBlock;
    }

    public ItemStack getResultItem() {
        if (result.potion().isPresent()) {
            return CauldronContents.createItemStack(Items.POTION, result);
        } else {
            return BuiltInRegistries.BLOCK.get(result.id()).asItem().getDefaultInstance();
        }
    }

    public CauldronContents getResultPotion() {
        return result;
    }

    @Override
    public RecipeSerializer<BrewingRecipe> getSerializer() {
        return CauldronModRecipes.BREWING_SERIALIZER;
    }

    @Override
    public RecipeType<BrewingRecipe> getType() {
        return CauldronModRecipes.BREWING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    public ParticleOptions getParticleType() {
        return particleType;
    }

    public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(r->r.reagent),
                CauldronContents.CODEC.fieldOf("potion").forGetter(r->r.contents),
                CauldronContents.CODEC.fieldOf("result").forGetter(r->r.result),
                ParticleTypes.CODEC.optionalFieldOf("particle_type", ParticleTypes.BUBBLE).forGetter(r->r.particleType),
                Codec.BOOL.optionalFieldOf("requires_heat", CauldronMod.CONFIG.requiresHeat.value()).forGetter(r->r.requiresHeat),
                Codec.BOOL.optionalFieldOf("place_as_block", false).forGetter(r->r.placeAsBlock)
    ).apply(inst, BrewingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.of(BrewingRecipe::toNetwork, BrewingRecipe::fromNetwork);

    private static BrewingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        var reagent = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        var potion = CauldronContents.STREAM_CODEC.decode(buf);
        var result = CauldronContents.STREAM_CODEC.decode(buf);
        var particleType = ParticleTypes.STREAM_CODEC.decode(buf);
        var requiresHeat = buf.readBoolean();
        var placeAsBlock = buf.readBoolean();
        return new BrewingRecipe(reagent, potion, result, particleType, requiresHeat, placeAsBlock);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, BrewingRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.reagent);
        CauldronContents.STREAM_CODEC.encode(buf, recipe.contents);
        CauldronContents.STREAM_CODEC.encode(buf, recipe.result);
        ParticleTypes.STREAM_CODEC.encode(buf, recipe.particleType);
        buf.writeBoolean(recipe.requiresHeat);
        buf.writeBoolean(recipe.placeAsBlock);
    }
}
