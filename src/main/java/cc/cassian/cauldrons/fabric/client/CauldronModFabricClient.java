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
        BlockEntityRenderers.register(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), CauldronRenderer::new);

		//? >26 {
		BlockColorRegistry.register(CauldronModClient::getColor, CauldronModBlocks.BREWING_CAULDRON.get());
		//?} else {
		/*net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register(CauldronModClient::getColor, CauldronModBlocks.BREWING_CAULDRON.get());
		*///?}


        //? if <1.21.4 {
        /*net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register(((itemStack, i) -> {
            var contents = itemStack.getComponents().get(DataComponents.POTION_CONTENTS);
            if (contents != null)
                return contents.getColor();
            return -1;
        }), CauldronModItems.CAULDRON_CONTENTS.get());
        *///?}

        //? if >26 {
        ChunkSectionLayerMap.putBlocks(ChunkSectionLayer.TRANSLUCENT,
        //?} else if >1.21.4 {
        /*BlockRenderLayerMap.putBlocks(ChunkSectionLayer.TRANSLUCENT,
        *///?} else {
        /*BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(),
        *///?}
        CauldronModBlocks.BREWING_CAULDRON.get());


    }
}

//?}