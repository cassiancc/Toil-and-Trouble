package cc.cassian.cauldrons.compat.rrv.brewing;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Ingredient;

public class CauldronBrewingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<CauldronBrewingServerRecipe> TYPE = ReliableServerRecipeType.register(
            CauldronMod.of("brewing"),
            () -> new CauldronBrewingServerRecipe(null, null, null, false)
    );
    private Ingredient reagent;
    private CauldronContents potion;
    private CauldronContents result;
    private boolean requiresHeat;

    public CauldronBrewingServerRecipe(Ingredient reagent, CauldronContents potion, CauldronContents result, boolean requiresHeat) {
        this.reagent = reagent;
        this.potion = potion;
        this.result = result;
        this.requiresHeat = requiresHeat;
    }


    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("reagent", TagUtil.writeIngredient(reagent));
        tag.store("potion", CauldronContents.CODEC, potion);
        tag.store("result", CauldronContents.CODEC, result);
        tag.putBoolean("requires_heat", requiresHeat);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagent = TagUtil.readIngredient(tag.getCompoundOrEmpty("reagent"));
        potion = tag.read("potion", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        result = tag.read("result", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        requiresHeat = tag.getBooleanOr("requires_heat", false);
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

    public CauldronContents getResult() {
        return result;
    }

	public boolean isHeated() {
		return requiresHeat;
	}
}
