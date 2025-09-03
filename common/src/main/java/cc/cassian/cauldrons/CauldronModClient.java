package cc.cassian.cauldrons;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class CauldronModClient {
    public static List<Component> getForWaila(BlockState state, CauldronBlockEntity entity) {
        List<Component> iTooltip = new ArrayList<>();
        if (entity.getContents() != CauldronContents.EMPTY) {
            iTooltip.add(Component.translatable("gui.toil_and_trouble.doses", state.getValue(BrewingCauldronBlock.POTION_QUANTITY)).withStyle(ChatFormatting.DARK_PURPLE));
            if (entity.getContents().isPotion()) {
                var item = Items.POTION;
                if (entity.isPotionSplash())
                    item = Items.SPLASH_POTION;
                else if (entity.isPotionLingering())
                    item = Items.LINGERING_POTION;
                iTooltip.add(CauldronContents.createItemStack(item, entity.getContents()).getHoverName());
                if (Minecraft.getInstance().hasShiftDown())
                    PotionContents.addPotionTooltip(entity.getContents().getAllEffects(), iTooltip::add, 0, 0);
            } else {
                iTooltip.add(Component.translatableWithFallback(entity.getContents().id().toLanguageKey("cauldron"), WordUtils.capitalize(entity.getContents().id().getPath().replace("_", " "))));
            }
            if (!entity.getItem().isEmpty()) {
                iTooltip.add(Component.empty());
            }
        }
        return iTooltip;
    }
}
