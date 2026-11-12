package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CauldronModBlockEntityTypes {
    public static final BlockEntityType<CauldronBlockEntity> CAULDRON_BLOCK_ENTITY =
            CauldronMod.REGISTRAR.registerBlockEntity("cauldron_block_entity",
                    CauldronBlockEntity::new, CauldronModBlocks.BREWING_CAULDRON
            );

    public static void touch() {

    }
}
