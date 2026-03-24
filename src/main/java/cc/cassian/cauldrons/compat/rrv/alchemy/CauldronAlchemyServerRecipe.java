package cc.cassian.cauldrons.compat.rrv.alchemy;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class CauldronAlchemyServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<CauldronAlchemyServerRecipe> TYPE = ReliableServerRecipeType.register(
            CauldronMod.of("dipping"),
            () -> new CauldronAlchemyServerRecipe(null, null, null, false)
    );
    private List<SlotContent> reagents;
    private CauldronContents potion;
    private SlotContent result;
	private boolean requiresHeat;

	public CauldronAlchemyServerRecipe(List<SlotContent> reagents, CauldronContents potion, SlotContent result, boolean requiresHeat) {
        this.reagents = reagents;
        this.potion = potion;
        this.result = result;
		this.requiresHeat = requiresHeat;
	}

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("reagent", TagUtil.writeList(reagents, (ingredient, tag2)->TagUtil.writeSlotContent(ingredient)));
        tag.store("potion", CauldronContents.CODEC, potion);
        tag.put("result", TagUtil.writeSlotContent(result));
        tag.putBoolean("requires_heat", requiresHeat);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        reagents = TagUtil.readList(tag, "reagent", TagUtil::readSlotContent);
        potion = tag.read("potion", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        result = TagUtil.readSlotContent(tag.getCompoundOrEmpty("result"));
        requiresHeat = tag.getBooleanOr("requires_heat", false);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

    public List<SlotContent> getReagents() {
        return reagents;
    }

    public CauldronContents getPotion() {
        return potion;
    }

    public SlotContent getResult() {
        return result;
    }

    public boolean requiresHeat() {
        return requiresHeat;
    }

}