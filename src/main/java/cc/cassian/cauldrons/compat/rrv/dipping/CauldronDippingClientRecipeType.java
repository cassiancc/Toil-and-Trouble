package cc.cassian.cauldrons.compat.rrv.dipping;

//? if >1.21.10 {

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;

public class CauldronDippingClientRecipeType implements ReliableClientRecipeType {

    public static final CauldronDippingClientRecipeType INSTANCE = new CauldronDippingClientRecipeType();

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
    public Identifier getGuiTexture() {
        return CauldronMod.of("textures/gui/eiv/brewing.png");
    }

    @Override
    public int getSlotCount() {
        return 11;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        // reagents
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                slotDefinition.addItemSlot(x + y * 3, 1 + x * 18,  1 + y * 18);
            }
        }
        // cauldron contents
        slotDefinition.addItemSlot(9, 41, 5);
        // results
        slotDefinition.addItemSlot(10, 78, 5);
    }

    @Override
    public Identifier getId() {
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

//?}