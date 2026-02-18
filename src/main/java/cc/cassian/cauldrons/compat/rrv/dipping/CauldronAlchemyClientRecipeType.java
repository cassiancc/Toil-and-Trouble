package cc.cassian.cauldrons.compat.rrv.dipping;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;

public class CauldronAlchemyClientRecipeType implements ReliableClientRecipeType {

    public static final CauldronAlchemyClientRecipeType INSTANCE = new CauldronAlchemyClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("emi.category.toil_and_trouble.alchemy");
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath("rrv", "textures/gui/type/crafting.png");
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
        slotDefinition.addItemSlot(9, 61, 37);
        // results
        slotDefinition.addItemSlot(10, 95, 19);
    }

    @Override
    public Identifier getId() {
        return CauldronMod.of("alchemy");
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
