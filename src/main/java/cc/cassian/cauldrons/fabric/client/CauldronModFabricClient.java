package cc.cassian.cauldrons.fabric.client;

//? if fabric {

import cc.cassian.cauldrons.client.CauldronModClient;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import java.util.List;

public final class CauldronModFabricClient implements ClientModInitializer {

	@Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockEntityRenderers.register(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY, CauldronRenderer::new);

		BlockColorRegistry.register(List.of(CauldronModClient.getColor()), CauldronModBlocks.BREWING_CAULDRON.get());

    }
}

//?}