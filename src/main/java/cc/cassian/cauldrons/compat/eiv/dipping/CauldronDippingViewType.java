package cc.cassian.cauldrons.compat.eiv.dipping;

import cc.cassian.cauldrons.CauldronMod;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;

public class CauldronDippingViewType implements IEivRecipeViewType {

    public static final CauldronDippingViewType INSTANCE = new CauldronDippingViewType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("emi.category.toil_and_trouble.dipping");
    }

    @Override
    public int getDisplayWidth() {
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 25;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return CauldronMod.of("textures/gui/eiv/brewing.png");
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 5, 5);
        slotDefinition.addItemSlot(1, 41, 5);
        slotDefinition.addItemSlot(2, 78, 5);
    }

    @Override
    public ResourceLocation getId() {
        return CauldronMod.of("dipping");
    }

    @Override
    public ItemStack getIcon() {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return Collections.singletonList(Items.CAULDRON.getDefaultInstance());
    }
}
