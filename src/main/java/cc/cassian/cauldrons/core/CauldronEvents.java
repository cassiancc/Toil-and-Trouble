package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import oshi.util.tuples.Pair;

import static net.minecraft.world.level.block.Block.popResourceFromFace;

public class CauldronEvents {

    public static InteractionResult useBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState blockState = level.getBlockState(pos);
        if (blockState.is(Blocks.CAULDRON)) {
            level.setBlockAndUpdate(pos, CauldronBlocks.BREWING_CAULDRON.get().defaultBlockState());
            var insert = insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, blockHitResult);
            if (insert.equals(ItemInteractionResult.SUCCESS))
                return InteractionResult.SUCCESS;
        }
        else if (blockState.is(Blocks.WATER_CAULDRON)) {
            var state =  CauldronBlocks.BREWING_CAULDRON.get().defaultBlockState().setValue(BrewingCauldronBlock.POTION_QUANTITY, 3);
            level.setBlockAndUpdate(pos, state);
            level.setBlockEntity(new CauldronBlockEntity(pos, state, Potions.WATER));
            var insert = insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, blockHitResult);
            if (insert.equals(ItemInteractionResult.SUCCESS))
                return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    protected static ItemInteractionResult insert(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
    ) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (!itemStack.isEmpty()) {
                Pair<ItemInteractionResult, ItemStack> insert = cauldronBlockEntity.insert(itemStack.copyWithCount(1));
                if (!(insert.getA() == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)) {
                    if (player.getItemInHand(interactionHand).getCount() == 1)
                        player.setItemInHand(interactionHand, insert.getB());
                    else {
                        player.setItemInHand(interactionHand, itemStack.copyWithCount(itemStack.getCount()-1));
                        popResourceFromFace(level, pos, blockHitResult.getDirection(), insert.getB());
                    }
                }
                return insert.getA();
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
