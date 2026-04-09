package cc.cassian.cauldrons.neoforge.client;

//? if neoforge {

/*import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.client.CauldronModClient;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.items.CauldronContentsItem;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
//? if <1.21.5 {
import net.minecraft.util.FastColor;
//?}
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.List;

@EventBusSubscriber(modid = CauldronMod.MOD_ID, value = Dist.CLIENT)
public final class CauldronModNeoForgeClient {

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(CauldronModClient.getColor()), CauldronModBlocks.BREWING_CAULDRON);

    }

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(CauldronModClient::getColor, CauldronModItems.CAULDRON_CONTENTS);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY, CauldronRenderer::new);
    }
}
*///?}