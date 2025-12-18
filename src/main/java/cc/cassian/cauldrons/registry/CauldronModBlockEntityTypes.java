package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
//? fabric {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//?}
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class CauldronModBlockEntityTypes {
    public static final Supplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_BLOCK_ENTITY =
            CommonRegistry.registerBlockEntity("cauldron_block_entity", ()->
                    //? if fabric {
                    FabricBlockEntityTypeBuilder.create(CauldronBlockEntity::new,
                            CauldronModBlocks.BREWING_CAULDRON.get()).build()
                    //?} else if >1.21.2 {
                    /*new BlockEntityType<>(CauldronBlockEntity::new,Set.of(
                            CauldronModBlocks.BREWING_CAULDRON.get()))
                    *///?} else {
                    /*BlockEntityType.Builder.of(CauldronBlockEntity::new,
                            CauldronModBlocks.BREWING_CAULDRON.get()).build(null)
                    *///?}
            );

    public static void touch() {

    }
}
