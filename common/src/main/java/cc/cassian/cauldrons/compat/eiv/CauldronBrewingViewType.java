package cc.cassian.cauldrons.compat.eiv;

import cc.cassian.cauldrons.CauldronMod;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;

public class CauldronBrewingViewType implements IEivRecipeViewType {

    public static final CauldronBrewingViewType INSTANCE = new CauldronBrewingViewType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("emi.category.toil_and_trouble.brewing");
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
        return CauldronMod.of("brewing");
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
