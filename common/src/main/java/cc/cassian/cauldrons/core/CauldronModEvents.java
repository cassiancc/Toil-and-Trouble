package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.registry.CauldronBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Pair;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.setFillLevel;
import static cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity.createItemStack;
import static net.minecraft.world.level.block.Block.popResourceFromFace;

public class CauldronModEvents {

    public static ItemInteractionResult useBlock(Player player, Level level, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        BlockState blockState = level.getBlockState(pos);
        ItemStack stack = player.getItemInHand(interactionHand);
        if (blockState.is(Blocks.CAULDRON) && !stack.is(Items.WATER_BUCKET)) {
            level.setBlockAndUpdate(pos, CauldronBlocks.BREWING_CAULDRON.get().defaultBlockState());
            return insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, direction);
        }
        else if (blockState.is(Blocks.WATER_CAULDRON) && !stack.is(Items.BUCKET)) {
            var state =  CauldronBlocks.BREWING_CAULDRON.get().defaultBlockState().setValue(BrewingCauldronBlock.POTION_QUANTITY, blockState.getValue(LayeredCauldronBlock.LEVEL));
            level.setBlockAndUpdate(pos, state);
            level.setBlockEntity(new CauldronBlockEntity(pos, state, Potions.WATER));
            return insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, direction);

        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static ItemInteractionResult insert(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, Direction direction
    ) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (!itemStack.isEmpty()) {
                if (itemStack.is(Items.ARROW) && itemStack.getCount()>8 && cauldronBlockEntity.getFillLevel()>=1) {
                    itemStack.setCount(itemStack.getCount()-8);
                    var stack = PotionContents.createItemStack(Items.TIPPED_ARROW, cauldronBlockEntity.getPotion());
                    stack.setCount(8);
                    setFillLevel(blockState, level, pos, cauldronBlockEntity.getFillLevel()-1);
                    player.addItem(stack);
                    return ItemInteractionResult.CONSUME;
                } else {
                    Pair<ItemInteractionResult, ItemStack> insert = cauldronBlockEntity.insert(itemStack.copyWithCount(1));
                    if (!(insert.getA() == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)) {
                        if (player.getItemInHand(interactionHand).getCount() == 1)
                            player.setItemInHand(interactionHand, insert.getB());
                        else {
                            player.setItemInHand(interactionHand, itemStack.copyWithCount(itemStack.getCount()-1));
                            popResourceFromFace(level, pos, direction, insert.getB());
                        }
                    }
                    return insert.getA();
                }

            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
