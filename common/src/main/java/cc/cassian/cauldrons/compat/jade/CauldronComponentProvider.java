package cc.cassian.cauldrons.compat.jade;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
            if (cauldronBlockEntity.getPotion() != null) {
                PotionContents.addPotionTooltip(cauldronBlockEntity.getPotion().value().getEffects(), iTooltip::add, 0, 0);
                if (cauldronBlockEntity.isPotionSplash())
                    iTooltip.add(Component.translatable("gui.toil_and_trouble.splash"));
                else if (cauldronBlockEntity.isPotionLingering())
                    iTooltip.add(Component.literal("gui.toil_and_trouble.lingering"));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return CauldronModJadePlugin.CAULDRON;
    }
}
