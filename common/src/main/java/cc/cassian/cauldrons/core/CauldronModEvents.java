package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import cc.cassian.cauldrons.recipe.DippingRecipe;
import cc.cassian.cauldrons.recipe.InsertingRecipe;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.Optional;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.POTION_QUANTITY;
import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.setFillLevel;
import static net.minecraft.world.level.block.Block.popResourceFromFace;

public class CauldronModEvents {

    public static ItemInteractionResult useBlock(Player player, Level level, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        BlockState blockState = level.getBlockState(pos);
        ItemStack stack = player.getItemInHand(interactionHand);
        if (blockState.is(Blocks.CAULDRON) && !stack.is(Items.WATER_BUCKET)) {
            level.setBlockAndUpdate(pos, CauldronModBlocks.BREWING_CAULDRON.get().defaultBlockState());
            return insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, direction);
        }
        else if (blockState.is(Blocks.WATER_CAULDRON) && !stack.is(Items.BUCKET)) {
            var state =  CauldronModBlocks.BREWING_CAULDRON.get().defaultBlockState().setValue(BrewingCauldronBlock.CONTENTS, BrewingCauldronBlock.Contents.WATER).setValue(POTION_QUANTITY, blockState.getValue(LayeredCauldronBlock.LEVEL));
            level.setBlockAndUpdate(pos, state);
            level.setBlockEntity(new CauldronBlockEntity(pos, state, new CauldronContents(Potions.WATER)));
            return insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, direction);

        }
        else if (blockState.is(Blocks.LAVA_CAULDRON) && !stack.is(Items.BUCKET)) {
            var state =  CauldronModBlocks.BREWING_CAULDRON.get().defaultBlockState().setValue(BrewingCauldronBlock.CONTENTS, BrewingCauldronBlock.Contents.LAVA).setValue(POTION_QUANTITY, 3);
            level.setBlockAndUpdate(pos, state);
            level.setBlockEntity(new CauldronBlockEntity(pos, state, new CauldronContents("lava")));
            return insert(player.getItemInHand(interactionHand), blockState, level, pos, player, interactionHand, direction);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static ItemInteractionResult insert(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, @Nullable Player player, @Nullable InteractionHand interactionHand, @Nullable Direction direction
    ) {
        if (direction == null) {
            direction = Direction.UP;
        }
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (!itemStack.isEmpty()) {
                if (level instanceof ServerLevel) {
                    Optional<RecipeHolder<InsertingRecipe>> insertingRecipeRecipeHolder = level.getRecipeManager().getRecipeFor(CauldronModRecipes.INSERTING.get(), new BrewingRecipeInput(itemStack, cauldronBlockEntity.getContents(), false), level);
                    if (insertingRecipeRecipeHolder.isPresent()) {
                        var recipe = insertingRecipeRecipeHolder.get().value();
                        int newFillLevel = blockState.getValue(POTION_QUANTITY) + recipe.getAmount();
                        if (newFillLevel > -1 && newFillLevel < 4) {
                            cauldronBlockEntity.setContents(recipe.getResultPotion());
                            if (player == null || !player.isCreative())
                                itemStack.setCount(itemStack.getCount()-1);
                            addItem(player, interactionHand, level, pos, direction, recipe.getResultItem(level.registryAccess()));
                            setFillLevel(blockState, level, pos, newFillLevel);
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                    return tryHardcodedRecipe(itemStack, blockState, cauldronBlockEntity, level, pos, player, interactionHand, direction);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static ItemInteractionResult tryHardcodedRecipe(
            ItemStack itemStack, BlockState blockState, CauldronBlockEntity cauldronBlockEntity, Level level, BlockPos pos, @Nullable Player player, @Nullable InteractionHand interactionHand, @Nullable Direction direction
    ) {
        if (itemStack.is(Items.ARROW) && itemStack.getCount()>=16 && cauldronBlockEntity.getFillLevel()>=1) {
            var tippedCount = 16;
            var fillLevel = 1;
            if (itemStack.getCount()>=32 && cauldronBlockEntity.getFillLevel()>=2) {
                tippedCount = 32;
                fillLevel = 2;
            }
            if (itemStack.getCount()==64 && cauldronBlockEntity.getFillLevel()==3) {
                tippedCount = 64;
                fillLevel = 3;
            }
            itemStack.setCount(itemStack.getCount()-tippedCount);
            var stack = CauldronContents.createItemStack(Items.TIPPED_ARROW, cauldronBlockEntity.getContents());
            stack.setCount(tippedCount);
            setFillLevel(blockState, level, pos, cauldronBlockEntity.getFillLevel()-fillLevel);
            addItem(player, interactionHand, level, pos, direction, stack);
            return ItemInteractionResult.CONSUME;
        } else if (itemStack.is(Items.GLASS_BOTTLE) && cauldronBlockEntity.getContents().isPotion() && cauldronBlockEntity.getFillLevel()>=1) {
            var fillLevel = 1;
            if (itemStack.getCount()==2 && cauldronBlockEntity.getFillLevel()==2) {
                fillLevel = 2;
            }
            if (itemStack.getCount()>=3 && cauldronBlockEntity.getFillLevel()==3) {
                fillLevel = 3;
            }
            itemStack.setCount(itemStack.getCount()-fillLevel);
            var stack = ItemStack.EMPTY;
            var potionItem = Items.POTION;
            if (cauldronBlockEntity.isPotionSplash()) potionItem = Items.SPLASH_POTION;
            else if (cauldronBlockEntity.isPotionLingering()) potionItem = Items.LINGERING_POTION;
            stack = CauldronContents.createItemStack(potionItem, cauldronBlockEntity.getContents());
            stack.setCount(fillLevel);
            setFillLevel(blockState, level, pos, cauldronBlockEntity.getFillLevel()-fillLevel);
            addItem(player, interactionHand, level, pos, direction, stack);
            return ItemInteractionResult.SUCCESS;
        } else {
            Pair<ItemInteractionResult, ItemStack> insert = cauldronBlockEntity.insert(itemStack.copyWithCount(1));
            if (!(insert.getA() == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)) {
                if (player == null || !player.isCreative())
                    itemStack.setCount(itemStack.getCount()-1);
                addItem(player, interactionHand, level, pos, direction, insert.getB());
            }
            return insert.getA();
        }
    }

    public static void addItem(Player player, InteractionHand interactionHand, Level level, BlockPos pos, Direction direction, ItemStack stack) {
        if (player != null) {
            if (interactionHand != null && player.getItemInHand(interactionHand).isEmpty()) {
                player.setItemInHand(interactionHand, stack);
            } else {
                player.addItem(stack);
            }
        } else {
            popResourceFromFace(level, pos, direction, stack);
        }
    }
}
