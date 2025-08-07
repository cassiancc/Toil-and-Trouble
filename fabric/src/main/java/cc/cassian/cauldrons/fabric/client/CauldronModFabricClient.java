package cc.cassian.cauldrons.fabric.client;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public final class CauldronModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockEntityRenderers.register(CauldronBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), CauldronRenderer::new);
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view == null || tintIndex != 0) return 9551193;
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
                return cauldronBlockEntity.getPotionColour();
            }
            return 9551193;
        }, CauldronBlocks.BREWING_CAULDRON.get());
        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.TRANSLUCENT, CauldronBlocks.BREWING_CAULDRON.get());

    }
}
