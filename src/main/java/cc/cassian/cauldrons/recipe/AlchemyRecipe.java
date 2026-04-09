package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AlchemyRecipe implements Recipe<BrewingRecipeInput> {

    private final List<Ingredient> reagents;
    private final CauldronContents contents;
    private final ItemStack result;
    private final ParticleOptions particleType;
    private final boolean requiresHeat;
    private final boolean copyComponents;
    private final boolean placeAsBlock;

    public AlchemyRecipe(List<Ingredient> reagent, CauldronContents contents, ItemStack result, ParticleOptions particleType, boolean requiresHeat, boolean copyComponents, boolean placeAsBlock) {
        this.reagents = reagent;
        this.contents = contents;
        this.result = result;
        this.particleType = particleType;
        this.requiresHeat = requiresHeat;
        this.copyComponents = copyComponents;
        this.placeAsBlock = placeAsBlock;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        if (!requiresHeat() || input.isHeated())
            return input.ingredientAmount() == this.reagents.size() && input.stackedContents().canCraft(this, null) && contents.test(input.getContents());
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getResultItem();
    }

    @Override
    public ItemStack assemble(@Nullable BrewingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack itemStack = this.result.copy();
        DataComponentPatch originalComponents = itemStack.getComponentsPatch();
        if (input != null && copyComponents) {
            DataComponentPatch copiedComponents = input.getItem(0).getComponentsPatch();
            itemStack.applyComponents(copiedComponents);
            itemStack.applyComponents(originalComponents);
        }
        return itemStack;
    }

    public boolean requiresHeat() {
        return requiresHeat;
    }

    public List<Ingredient> getReagents() {
        return reagents;
    }

    public CauldronContents getContents() {
        return contents;
    }

    public boolean tryPlaceAsBlock() {
        return placeAsBlock;
    }

    public ItemStack getResultItem() {
        return assemble(null, null);
    }

    @Override
    public RecipeSerializer<AlchemyRecipe> getSerializer() {
        return CauldronModRecipes.ALCHEMY_SERIALIZER;
    }

    @Override
    public RecipeType<AlchemyRecipe> getType() {
        return CauldronModRecipes.ALCHEMY;
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

        public static final MapCodec<AlchemyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                CauldronModHelpers.INGREDIENT_LIST_CODEC.fieldOf("reagent").forGetter(r->r.reagents),
                CauldronContents.CODEC.fieldOf("potion").forGetter(r->r.contents),
                ItemStack.CODEC.fieldOf("result").forGetter(r->r.result),
                ParticleTypes.CODEC.optionalFieldOf("particle_type", ParticleTypes.BUBBLE).forGetter(r->r.particleType),
                Codec.BOOL.optionalFieldOf("requires_heat", CauldronMod.CONFIG.requiresHeat.value()).forGetter(r->r.requiresHeat),
                Codec.BOOL.optionalFieldOf("copy_components", false).forGetter(r->r.copyComponents),
                Codec.BOOL.optionalFieldOf("place_as_block", false).forGetter(r->r.placeAsBlock)
        ).apply(inst, AlchemyRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyRecipe> STREAM_CODEC = StreamCodec.of(AlchemyRecipe::toNetwork, AlchemyRecipe::fromNetwork);

        private static AlchemyRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = CauldronModHelpers.INGREDIENT_LIST_STREAM_CODEC.decode(buf);
            var potion = CauldronContents.STREAM_CODEC.decode(buf);
            var result = ItemStack.STREAM_CODEC.decode(buf);
            var particleType = ParticleTypes.STREAM_CODEC.decode(buf);
            var requiresHeat = buf.readBoolean();
            var copyComponents = buf.readBoolean();
            var placeAsBlock = buf.readBoolean();
            return new AlchemyRecipe(reagent, potion, result, particleType, requiresHeat, copyComponents, placeAsBlock);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, AlchemyRecipe recipe) {
            CauldronModHelpers.INGREDIENT_LIST_STREAM_CODEC.encode(buf, recipe.reagents);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.contents);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            ParticleTypes.STREAM_CODEC.encode(buf, recipe.particleType);
            buf.writeBoolean(recipe.requiresHeat);
            buf.writeBoolean(recipe.copyComponents);
            buf.writeBoolean(recipe.placeAsBlock);
        }

}
