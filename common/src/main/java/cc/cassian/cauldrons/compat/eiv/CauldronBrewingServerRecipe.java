package cc.cassian.cauldrons.compat.eiv;

import cc.cassian.cauldrons.CauldronMod;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;

public class CauldronBrewingServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<CauldronBrewingServerRecipe> TYPE = EivRecipeType.register(
            CauldronMod.of("brewing"),
            () -> new CauldronBrewingServerRecipe(null, null, null)
    );
    private Ingredient reagent;
    private PotionContents potion;
    private PotionContents result;

    public CauldronBrewingServerRecipe(Ingredient reagent, PotionContents potion, PotionContents result) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
    }


    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("reagent", EivTagUtil.writeIngredient(reagent));
        tag.put("potion", PotionContents.CODEC.encodeStart(NbtOps.INSTANCE, potion).result().get());
        tag.put("result", PotionContents.CODEC.encodeStart(NbtOps.INSTANCE, result).result().get());
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagent = EivTagUtil.readIngredient(tag.getCompoundOrEmpty("reagent"));
        potion = PotionContents.CODEC.decode(NbtOps.INSTANCE, tag.get("potion")).result().get().getFirst();
        result = PotionContents.CODEC.decode(NbtOps.INSTANCE, tag.get("result")).result().get().getFirst();
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public PotionContents getPotion() {
        return potion;
    }

    public PotionContents getResult() {
        return result;
    }
}
