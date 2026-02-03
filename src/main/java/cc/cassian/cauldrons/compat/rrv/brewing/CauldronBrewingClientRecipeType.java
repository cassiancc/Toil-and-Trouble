package cc.cassian.cauldrons.compat.rrv.brewing;
//? if >1.21.7 {

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;

public class CauldronBrewingClientRecipeType implements ReliableClientRecipeType {

    public static final CauldronBrewingClientRecipeType INSTANCE = new CauldronBrewingClientRecipeType();

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
    public Identifier getGuiTexture() {
        return CauldronMod.of("textures/gui/eiv/brewing.png");
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 5, 4);
        slotDefinition.addItemSlot(1, 41, 4);
        slotDefinition.addItemSlot(2, 78, 4);
    }

    @Override
    public Identifier getId() {
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
//?}