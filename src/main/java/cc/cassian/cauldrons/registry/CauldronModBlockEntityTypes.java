package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
//? fabric {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//?}
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class CauldronModBlockEntityTypes {
    public static final BlockEntityType<CauldronBlockEntity> CAULDRON_BLOCK_ENTITY =
            CommonRegistry.registerBlockEntity("cauldron_block_entity",
                    //? if fabric {
                    FabricBlockEntityTypeBuilder.create(CauldronBlockEntity::new,
                            CauldronModBlocks.BREWING_CAULDRON).build()
                    //?} else {
                    /*new BlockEntityType<>(CauldronBlockEntity::new,
                            CauldronModBlocks.BREWING_CAULDRON)
                    *///?}
            );

    public static void touch() {

    }
}
