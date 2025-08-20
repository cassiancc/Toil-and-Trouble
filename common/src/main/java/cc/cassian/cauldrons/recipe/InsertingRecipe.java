package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class InsertingRecipe implements Recipe<BrewingRecipeInput> {

    private final ItemStack reagent;
    private final CauldronContents potion;
    private final ItemStack resultItem;
    private final CauldronContents resultPotion;
    private final boolean addPotionComponents;
    private final int amount;

    public InsertingRecipe(ItemStack reagent, CauldronContents currentPotion, ItemStack resultItem, CauldronContents resultPotion, boolean addPotionComponents, int amount) {
        this.reagent = reagent;
        this.potion = currentPotion;
        this.resultItem = resultItem;
        this.resultPotion = resultPotion;
        this.addPotionComponents = addPotionComponents;
        this.amount = amount;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        return ItemStack.isSameItemSameComponents(reagent, input.getItem(0)) && potion.test(input.getContents());
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input, HolderLookup.Provider registries) {
        return this.resultItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(reagent));
    }

    public ItemStack getReagent() {
        return reagent;
    }

    public CauldronContents getPotion() {
        return potion;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return resultItem.copy();
    }

    @Override
    public RecipeSerializer<InsertingRecipe> getSerializer() {
        return CauldronModRecipes.INSERTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<InsertingRecipe> getType() {
        return CauldronModRecipes.INSERTING.get();
    }

    public CauldronContents getResultPotion() {
        return this.resultPotion;
    }

    public static class Serializer implements RecipeSerializer<InsertingRecipe> {
        public static final MapCodec<InsertingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ItemStack.CODEC.fieldOf("item").forGetter(r->r.reagent),
                CauldronContents.CODEC.fieldOf("contents").forGetter(r->r.potion),
                ItemStack.CODEC.fieldOf("result_item").forGetter(r->r.resultItem),
                CauldronContents.CODEC.fieldOf("result_contents").forGetter(r->r.resultPotion),
                Codec.BOOL.optionalFieldOf("add_potion_components", false).forGetter(r->r.addPotionComponents),
                Codec.INT.optionalFieldOf("amount", 0).forGetter(r->r.amount)

        ).apply(inst, InsertingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InsertingRecipe> STREAM_CODEC = StreamCodec.of(InsertingRecipe.Serializer::toNetwork, InsertingRecipe.Serializer::fromNetwork);

        private static InsertingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var reagent = ItemStack.STREAM_CODEC.decode(buf);
            var potion = CauldronContents.STREAM_CODEC.decode(buf);
            var result = ItemStack.STREAM_CODEC.decode(buf);
            var resultPotion = CauldronContents.STREAM_CODEC.decode(buf);
            var addPotionComponents = buf.readBoolean();
            var amount = buf.readInt();
            return new InsertingRecipe(reagent, potion, result, resultPotion, addPotionComponents, amount);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, InsertingRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buf, recipe.reagent);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.potion);
            ItemStack.STREAM_CODEC.encode(buf, recipe.resultItem);
            CauldronContents.STREAM_CODEC.encode(buf, recipe.resultPotion);
            buf.writeBoolean(recipe.addPotionComponents);
            buf.writeInt(recipe.amount);
        }

        @Override
        public MapCodec<InsertingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InsertingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
