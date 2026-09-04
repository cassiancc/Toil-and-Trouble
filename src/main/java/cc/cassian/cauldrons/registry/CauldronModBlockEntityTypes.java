package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
//? fabric {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//?}
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class CauldronModBlockEntityTypes {
    public static final BlockEntityType<CauldronBlockEntity> CAULDRON_BLOCK_ENTITY =
            CauldronMod.REGISTRAR.registerBlockEntity("cauldron_block_entity",
                    CauldronBlockEntity::new, CauldronModBlocks.BREWING_CAULDRON.get()
            );

    public static void touch() {

    }
}
