package cc.cassian.cauldrons.compat.eiv.dipping;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;

public class CauldronDippingServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<CauldronDippingServerRecipe> TYPE = EivRecipeType.register(
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
        tag.put("reagent", EivTagUtil.writeIngredient(reagent));
        tag.put("potion", CauldronContents.CODEC.encodeStart(NbtOps.INSTANCE, potion).result().get());
        tag.put("result", ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, result).result().orElseThrow());
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagent = EivTagUtil.readIngredient(tag.getCompoundOrEmpty("reagent"));
        potion = CauldronContents.CODEC.decode(NbtOps.INSTANCE, tag.get("potion")).result().get().getFirst();
        result = ItemStack.CODEC.decode(NbtOps.INSTANCE, tag.get("result")).result().get().getFirst();
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
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
