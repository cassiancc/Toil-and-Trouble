package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class DippingRecipe implements Recipe<BrewingRecipeInput> {

    private final List<Ingredient> reagents;
    private final CauldronContents potion;
    private final ItemStackTemplate result;
    private final ParticleOptions particleType;
    private final boolean requiresHeat;

    public DippingRecipe(List<Ingredient> reagent, CauldronContents potion, ItemStackTemplate result, ParticleOptions particleType, boolean requiresHeat) {
        this.reagents = reagent;
        this.potion = potion;
        this.result = result;
        this.particleType = particleType;
        this.requiresHeat = requiresHeat;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        if (!requiresHeat() || input.isHeated())
            return input.ingredientAmount() == this.reagents.size() && input.stackedContents().canCraft(this, null);
        return false;
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input) {
        return this.result.create();
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
        return result.create();
    }

    @Override
    public RecipeSerializer<DippingRecipe> getSerializer() {
        return CauldronModRecipes.DIPPING_SERIALIZER;
    }

    @Override
    public RecipeType<DippingRecipe> getType() {
        return CauldronModRecipes.DIPPING;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.reagents);
    }

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
        return "dipping";
    }

    public ParticleOptions getParticleType() {
        return particleType;
    }

        public static final MapCodec<DippingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                CauldronModHelpers.INGREDIENT_LIST_CODEC.fieldOf("reagent").forGetter(r->r.reagents),
                CauldronContents.CODEC.fieldOf("potion").forGetter(r->r.potion),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(r->r.result),
                ParticleTypes.CODEC.optionalFieldOf("particle_type", ParticleTypes.BUBBLE).forGetter(r->r.particleType),
                Codec.BOOL.optionalFieldOf("requires_heat", CauldronMod.CONFIG.requiresHeat.value()).forGetter(r->r.requiresHeat)
        ).apply(inst, DippingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DippingRecipe> STREAM_CODEC = StreamCodec.of(DippingRecipe::toNetwork, DippingRecipe::fromNetwork);

        private static DippingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = CauldronModHelpers.INGREDIENT_LIST_STREAM_CODEC.decode(buf);
            var potion = CauldronContents.STREAM_CODEC.decode(buf);
            var result = ItemStackTemplate.STREAM_CODEC.decode(buf);
            var particleType = ParticleTypes.STREAM_CODEC.decode(buf);
            var requiresHeat = buf.readBoolean();
            return new DippingRecipe(reagent, potion, result, particleType, requiresHeat);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, DippingRecipe recipe) {
            CauldronModHelpers.INGREDIENT_LIST_STREAM_CODEC.encode(buf, recipe.reagents);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.potion);
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result);
            ParticleTypes.STREAM_CODEC.encode(buf, recipe.particleType);
            buf.writeBoolean(recipe.requiresHeat);
        }
}
