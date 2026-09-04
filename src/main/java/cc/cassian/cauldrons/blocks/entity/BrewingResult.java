package cc.cassian.cauldrons.blocks.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record BrewingResult(BlockState state, boolean success) {
}
