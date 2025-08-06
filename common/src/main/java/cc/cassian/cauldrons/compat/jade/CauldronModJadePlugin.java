package cc.cassian.cauldrons.compat.jade;

import cc.cassian.cauldrons.CauldronMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CauldronBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CauldronModJadePlugin implements IWailaPlugin {
    public static final ResourceLocation CAULDRON = CauldronMod.of("cauldron");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CauldronComponentProvider.INSTANCE, CauldronBlock.class);
    }
}
