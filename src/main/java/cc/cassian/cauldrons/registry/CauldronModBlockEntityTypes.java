package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class CauldronModBlockEntityTypes {
    public static final Supplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_BLOCK_ENTITY =
            CommonRegistry.registerBlockEntity("cauldron_block_entity", ()->
                    //? if >1.21.2 {
                    new BlockEntityType<>
                    //?} else {
                    /*BlockEntityType.Builder.of
                    *///?}
                    (CauldronBlockEntity::new,
                            //? if >1.21.2
                            Set.of(
                            CauldronModBlocks.BREWING_CAULDRON.get())
                            //? if <1.21.2 {
                            /*.build(null)
                            *///?} else {
                            )
                            //?}
            );

    public static void touch() {

    }
}
