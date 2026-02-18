package cc.cassian.cauldrons.recipe;

import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class InsertingRecipe implements Recipe<BrewingRecipeInput> {

    private final ItemStackTemplate reagent;
    private final CauldronContents potion;
    private final ItemStackTemplate resultItem;
    private final CauldronContents resultContents;
    private final boolean addPotionComponents;
    private final int amount;

    public InsertingRecipe(ItemStackTemplate reagent, CauldronContents currentPotion, ItemStackTemplate resultItem, CauldronContents resultContents, boolean addPotionComponents, int amount) {
        this.reagent = reagent;
        this.potion = currentPotion;
        this.resultItem = resultItem;
        this.resultContents = resultContents;
        this.addPotionComponents = addPotionComponents;
        this.amount = amount;
    }

    @Override
    public boolean matches(BrewingRecipeInput input, Level level) {
        return ItemStack.isSameItemSameComponents(reagent.create(), input.getItem(0)) && potion.test(input.getContents());
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput input) {
        return this.resultItem.create();
    }

    public ItemStack getReagent() {
        return reagent.create().copy();
    }

    public CauldronContents getPotion() {
        return potion;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getResultItem() {
        return resultItem.create();
    }

    @Override
    public RecipeSerializer<InsertingRecipe> getSerializer() {
        return CauldronModRecipes.INSERTION_SERIALIZER;
    }

    @Override
    public RecipeType<InsertingRecipe> getType() {
        return CauldronModRecipes.INSERTING;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
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
        return "";
    }

    public CauldronContents getResultContents() {
        return this.resultContents;
    }

    public static final MapCodec<InsertingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(r->r.reagent),
            CauldronContents.CODEC.fieldOf("contents").forGetter(r->r.potion),
            ItemStackTemplate.CODEC.fieldOf("result_item").forGetter(r->r.resultItem),
            CauldronContents.CODEC.fieldOf("result_contents").forGetter(r->r.resultContents),
            Codec.BOOL.optionalFieldOf("add_potion_components", false).forGetter(r->r.addPotionComponents),
            Codec.INT.optionalFieldOf("amount", 0).forGetter(r->r.amount)

    ).apply(inst, InsertingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InsertingRecipe> STREAM_CODEC = StreamCodec.of(InsertingRecipe::toNetwork, InsertingRecipe::fromNetwork);

    private static InsertingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        var reagent = ItemStackTemplate.STREAM_CODEC.decode(buf);
        var potion = CauldronContents.STREAM_CODEC.decode(buf);
        var result = ItemStackTemplate.STREAM_CODEC.decode(buf);
        var resultPotion = CauldronContents.STREAM_CODEC.decode(buf);
        var addPotionComponents = buf.readBoolean();
        var amount = buf.readInt();
        return new InsertingRecipe(reagent, potion, result, resultPotion, addPotionComponents, amount);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, InsertingRecipe recipe) {
        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.reagent);
        CauldronContents.STREAM_CODEC.encode(buf, recipe.potion);
        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.resultItem);
        CauldronContents.STREAM_CODEC.encode(buf, recipe.resultContents);
        buf.writeBoolean(recipe.addPotionComponents);
        buf.writeInt(recipe.amount);
    }
}
