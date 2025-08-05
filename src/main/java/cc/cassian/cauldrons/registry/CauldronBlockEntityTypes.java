package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CauldronBlockEntityTypes {
    public static final Supplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_BLOCK_ENTITY =
            CommonRegistry.registerBlockEntity("cauldron_block_entity", ()->
                    BlockEntityType.Builder.of(CauldronBlockEntity::new, CauldronBlocks.BREWING_CAULDRON.get()).build(null));

    public static void touch() {

    }
}
