package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class CauldronModBlockEntityTypes {
    public static final Supplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_BLOCK_ENTITY =
            CommonRegistry.registerBlockEntity("cauldron_block_entity", ()->
                    new BlockEntityType<>(CauldronBlockEntity::new, Set.of(CauldronModBlocks.BREWING_CAULDRON.get())));

    public static void touch() {

    }
}
