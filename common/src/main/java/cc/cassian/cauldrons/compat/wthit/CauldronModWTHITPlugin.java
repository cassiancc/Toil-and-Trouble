package cc.cassian.cauldrons.compat.wthit;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.network.chat.Component;
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
            for (Component component : cauldronBlockEntity.getForWaila(blockAccessor.getBlockState())) {
                iTooltip.addLine(component);
            }
        }
    }
}
