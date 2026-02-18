package cc.cassian.cauldrons.fabric.client;

//? if fabric {

import cc.cassian.cauldrons.client.CauldronModClient;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import net.fabricmc.api.ClientModInitializer;
//? if >26 {
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//?} else if >1.21.4 {
/*import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
*///?} else {
/*import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
*///?}
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class CauldronModFabricClient implements ClientModInitializer {

	@Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockEntityRenderers.register(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY, CauldronRenderer::new);

		BlockColorRegistry.register(CauldronModClient::getColor, CauldronModBlocks.BREWING_CAULDRON);

    }
}

//?}