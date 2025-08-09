package cc.cassian.cauldrons.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

public class CauldronContentsItem extends PotionItem {
    public CauldronContentsItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
        return potionContents != null ? potionContents.getName(Items.POTION.getDescriptionId() + ".effect.") : super.getName(stack);
    }
}
