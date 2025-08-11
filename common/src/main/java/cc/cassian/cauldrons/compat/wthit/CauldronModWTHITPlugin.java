package cc.cassian.cauldrons.compat.wthit;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;

public class CauldronModWTHITPlugin implements IWailaPlugin, IBlockComponentProvider {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(CauldronMod.of("cauldron"), true );
        registrar.addComponent(this, TooltipPosition.BODY, Block.class, 2000 );
    }


    @Override
    public void appendBody(ITooltip iTooltip, IBlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlockEntity() instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (!cauldronBlockEntity.getItem().isEmpty()) {
                iTooltip.addLine(cauldronBlockEntity.getItem().getHoverName());
                iTooltip.addLine(Component.empty());
            }
            if (cauldronBlockEntity.getPotion() != null) {
                iTooltip.addLine(Component.translatable("gui.toil_and_trouble.doses", blockAccessor.getBlockState().getValue(BrewingCauldronBlock.POTION_QUANTITY)).withStyle(ChatFormatting.DARK_PURPLE));
                var item = Items.POTION;
                if (cauldronBlockEntity.isPotionSplash())
                    item = Items.SPLASH_POTION;
                else if (cauldronBlockEntity.isPotionLingering())
                    item = Items.LINGERING_POTION;
                iTooltip.addLine(CauldronBlockEntity.createItemStack(item, cauldronBlockEntity.getPotionContents()).getHoverName());
                if (Screen.hasShiftDown())
                    PotionContents.addPotionTooltip(cauldronBlockEntity.getPotion().value().getEffects(), iTooltip::addLine, 0, 0);
            }
        }
    }
}
