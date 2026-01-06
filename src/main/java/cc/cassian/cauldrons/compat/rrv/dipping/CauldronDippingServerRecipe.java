package cc.cassian.cauldrons.compat.rrv.dipping;

//? if >1.21.10 {


import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CauldronDippingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<CauldronDippingServerRecipe> TYPE = ReliableServerRecipeType.register(
            CauldronMod.of("dipping"),
            () -> new CauldronDippingServerRecipe(null, null, null)
    );
    private Ingredient reagent;
    private CauldronContents potion;
    private ItemStack result;

    public CauldronDippingServerRecipe(Ingredient reagent, CauldronContents potion, ItemStack result) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
    }


    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("reagent", TagUtil.writeIngredient(reagent));
        tag.store("potion", CauldronContents.CODEC, potion);
        tag.put("result", TagUtil.encodeItemStackOnServer(result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagent = TagUtil.readIngredient(tag.getCompoundOrEmpty("reagent"));
        potion = tag.read("potion", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        result = TagUtil.decodeItemStackOnClient(tag.getCompoundOrEmpty("result"));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public CauldronContents getPotion() {
        return potion;
    }

    public ItemStack getResult() {
        return result;
    }
}

//?}