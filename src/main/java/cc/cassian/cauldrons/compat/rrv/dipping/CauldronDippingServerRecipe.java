package cc.cassian.cauldrons.compat.rrv.dipping;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class CauldronDippingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<CauldronDippingServerRecipe> TYPE = ReliableServerRecipeType.register(
            CauldronMod.of("dipping"),
            () -> new CauldronDippingServerRecipe(null, null, null)
    );
    private List<Ingredient> reagents;
    private CauldronContents potion;
    private ItemStack result;

    public CauldronDippingServerRecipe(List<Ingredient> reagents, CauldronContents potion, ItemStack result) {
        this.reagents = reagents;
        this.potion = potion;
        this.result = result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("reagent", TagUtil.writeList(reagents, (ingredient, tag2)->TagUtil.writeIngredient(ingredient)));
        tag.store("potion", CauldronContents.CODEC, potion);
        tag.put("result", TagUtil.encodeItemStackOnServer(result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagents = TagUtil.readList(tag, "reagent", TagUtil::readIngredient);
        potion = tag.read("potion", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        result = TagUtil.decodeItemStackOnClient(tag.getCompoundOrEmpty("result"));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

    public List<Ingredient> getReagents() {
        return reagents;
    }

    public CauldronContents getPotion() {
        return potion;
    }

    public ItemStack getResult() {
        return result;
    }
}