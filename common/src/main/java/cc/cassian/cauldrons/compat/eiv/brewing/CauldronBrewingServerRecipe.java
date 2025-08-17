package cc.cassian.cauldrons.compat.eiv.brewing;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
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
    private CauldronContents potion;
    private CauldronContents result;

    public CauldronBrewingServerRecipe(Ingredient reagent, CauldronContents potion, CauldronContents result) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
    }


    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("reagent", EivTagUtil.writeIngredient(reagent));
        tag.put("potion", CauldronContents.CODEC.encodeStart(NbtOps.INSTANCE, potion).result().get());
        tag.put("result", CauldronContents.CODEC.encodeStart(NbtOps.INSTANCE, result).result().get());
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagent = EivTagUtil.readIngredient(tag.getCompoundOrEmpty("reagent"));
        potion = CauldronContents.CODEC.decode(NbtOps.INSTANCE, tag.get("potion")).result().get().getFirst();
        result = CauldronContents.CODEC.decode(NbtOps.INSTANCE, tag.get("result")).result().get().getFirst();
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

    public CauldronContents getResult() {
        return result;
    }
}
