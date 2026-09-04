package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.mru.util.ItemLikeEntry;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CauldronModBlocks {

    public static final ItemLikeEntry<Block> BREWING_CAULDRON = CauldronMod.REGISTRAR.registerBlockEntry(
            "cauldron", BrewingCauldronBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).lightLevel(properties-> properties.getValue(BrewingCauldronBlock.CONTENTS) == BrewingCauldronBlock.ContentsProperty.LAVA ? 15 : 0)
    );

    public static void touch() {

    }
}
