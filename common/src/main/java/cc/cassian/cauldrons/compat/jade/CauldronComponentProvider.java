package cc.cassian.cauldrons.compat.jade;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CauldronComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (!cauldronBlockEntity.getItem().isEmpty()) {
                iTooltip.add(cauldronBlockEntity.getItem().getHoverName());
                iTooltip.add(Component.empty());
            }
            if (cauldronBlockEntity.getPotion() != null) {
                iTooltip.add(Component.translatable("gui.toil_and_trouble.doses", blockAccessor.getBlockState().getValue(BrewingCauldronBlock.POTION_QUANTITY)).withStyle(ChatFormatting.DARK_PURPLE));
                var item = Items.POTION;
                if (cauldronBlockEntity.isPotionSplash())
                    item = Items.SPLASH_POTION;
                else if (cauldronBlockEntity.isPotionLingering())
                    item = Items.LINGERING_POTION;
                iTooltip.add(CauldronBlockEntity.createItemStack(item, cauldronBlockEntity.getPotionContents()).getHoverName());
                if (Screen.hasShiftDown())
                    PotionContents.addPotionTooltip(cauldronBlockEntity.getPotion().value().getEffects(), iTooltip::add, 0, 0);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return CauldronModJadePlugin.CAULDRON;
    }
}
