package cc.cassian.cauldrons.compat.jade;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CauldronBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

import java.util.Locale;

public enum CauldronComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof CauldronBlockEntity cauldronBlockEntity) {
            for (Component component : cauldronBlockEntity.getForWaila(blockAccessor.getBlockState())) {
                iTooltip.add(component);
            }
            iTooltip.replace(JadeIds.CORE_OBJECT_NAME, Component.translatable("block.toil_and_trouble.cauldron." + blockAccessor.getBlockState().getValue(BrewingCauldronBlock.CONTENTS).name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.WHITE));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return CauldronModJadePlugin.CAULDRON;
    }
}
