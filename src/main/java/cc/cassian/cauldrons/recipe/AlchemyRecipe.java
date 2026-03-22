package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    private final CauldronContents potion;
    private final ItemStack result;
    private final ParticleOptions particleType;
    private final boolean requiresHeat;
    private final boolean copyComponents;

    public AlchemyRecipe(List<Ingredient> reagent, CauldronContents potion, ItemStack result, ParticleOptions particleType, boolean requiresHeat, boolean copyComponents) {
        this.reagents = reagent;
        this.potion = potion;
        this.result = result;
        this.particleType = particleType;
        this.requiresHeat = requiresHeat;
        this.copyComponents = copyComponents;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        if (!requiresHeat() || input.isHeated())
            return input.ingredientAmount() == this.reagents.size() && input.stackedContents().canCraft(this, null);
        return false;
    }

    @Override
    public ItemStack assemble(@Nullable BrewingRecipeInput input) {
        ItemStack itemStack = this.result.create();
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

    public CauldronContents getPotion() {
        return potion;
    }

    public ItemStack getResultItem() {
        return assemble(null);
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
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.reagents);
    }

    @SuppressWarnings("all")
    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "alchemy";
    }

    public ParticleOptions getParticleType() {
        return particleType;
    }

        public static final MapCodec<AlchemyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                CauldronModHelpers.INGREDIENT_LIST_CODEC.fieldOf("reagent").forGetter(r->r.reagents),
                CauldronContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                ItemStack.CODEC.fieldOf("result").forGetter(r->r.result),
                ParticleTypes.CODEC.optionalFieldOf("particle_type", ParticleTypes.BUBBLE).forGetter(r->r.particleType),
                Codec.BOOL.optionalFieldOf("requires_heat", CauldronMod.CONFIG.requiresHeat.value()).forGetter(r->r.requiresHeat),
                Codec.BOOL.optionalFieldOf("copy_components", false).forGetter(r->r.copyComponents)
        ).apply(inst, AlchemyRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyRecipe> STREAM_CODEC = StreamCodec.of(AlchemyRecipe::toNetwork, AlchemyRecipe::fromNetwork);

        private static AlchemyRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = CauldronModHelpers.INGREDIENT_LIST_STREAM_CODEC.decode(buf);
            var potion = CauldronContents.STREAM_CODEC.decode(buf);
            var result = ItemStack.STREAM_CODEC.decode(buf);
            var particleType = ParticleTypes.STREAM_CODEC.decode(buf);
            var requiresHeat = buf.readBoolean();
            var copyComponents = buf.readBoolean();
            return new AlchemyRecipe(reagent, potion, result, particleType, requiresHeat, copyComponents);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, AlchemyRecipe recipe) {
            CauldronModHelpers.INGREDIENT_LIST_STREAM_CODEC.encode(buf, recipe.reagents);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.potion);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            ParticleTypes.STREAM_CODEC.encode(buf, recipe.particleType);
            buf.writeBoolean(recipe.requiresHeat);
            buf.writeBoolean(recipe.copyComponents);
        }
}
