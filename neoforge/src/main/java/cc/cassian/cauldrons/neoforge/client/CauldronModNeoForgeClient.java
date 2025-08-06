package cc.cassian.cauldrons.neoforge.client;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = CauldronMod.MOD_ID, value = Dist.CLIENT)
public final class CauldronModNeoForgeClient {

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(((state, view, pos, tintIndex) -> {
            if (view == null || tintIndex != 0) return 9551193;
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
                return cauldronBlockEntity.getPotionColour();
            }
            return 9551193;
        }), CauldronBlocks.BREWING_CAULDRON.get());

    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CauldronBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), CauldronRenderer::new);
    }
}
