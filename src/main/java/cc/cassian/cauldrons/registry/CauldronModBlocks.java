package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CauldronModBlocks {

    public static final Block BREWING_CAULDRON = CauldronMod.REGISTRAR.registerBlock(
            "cauldron", BrewingCauldronBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).lightLevel(properties-> properties.getValue(BrewingCauldronBlock.CONTENTS) == BrewingCauldronBlock.ContentsProperty.LAVA ? 15 : 0)
    );

    public static void touch() {

    }
}
