package cc.cassian.cauldrons.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class CauldronContentsItem extends PotionItem {
    public CauldronContentsItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return Potion.getName(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion(), Items.POTION.getDescriptionId() + ".effect.");
    }
}
