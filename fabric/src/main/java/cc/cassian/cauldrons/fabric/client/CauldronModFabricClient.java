package cc.cassian.cauldrons.fabric.client;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;

public final class CauldronModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockEntityRenderers.register(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), CauldronRenderer::new);
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view == null || tintIndex != 0) return 9551193;
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
                return cauldronBlockEntity.getPotionColour();
            }
            return 9551193;
        }, CauldronModBlocks.BREWING_CAULDRON.get());
        ColorProviderRegistry.ITEM.register(((itemStack, i) -> {
            var contents = itemStack.getComponents().get(DataComponents.POTION_CONTENTS);
            if (contents != null)
                return contents.getColor();
            return -1;
        }), CauldronModItems.CAULDRON_CONTENTS.get());
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), CauldronModBlocks.BREWING_CAULDRON.get());

    }
}
