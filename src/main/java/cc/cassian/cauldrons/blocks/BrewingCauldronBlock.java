package cc.cassian.cauldrons.blocks;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

public class BrewingCauldronBlock extends CauldronBlock implements EntityBlock {
    public static final IntegerProperty POTION_QUANTITY = IntegerProperty.create("potion_quantity", 0, 3);
    public static final BooleanProperty MAGIC = BooleanProperty.create("magic");


    public BrewingCauldronBlock(Properties properties) {
        super(properties);
    }

    protected ItemInteractionResult useItemOn(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
    ) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (itemStack.is(Items.STICK)) {
                cauldronBlockEntity.brew();
                return ItemInteractionResult.SUCCESS;
            } else if (!itemStack.isEmpty()) {
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

    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            popResourceFromFace(level, pos, blockHitResult.getDirection(), cauldronBlockEntity.retrieve());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && this.isEntityInsideContent(state, pos, entity) && level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity && cauldronBlockEntity.getPotion() != null) {
            if (cauldronBlockEntity.isPotionWater()) {
                if (entity.isOnFire()) {
                    entity.clearFire();
                    if (entity.mayInteract(level, pos)) {
                        lowerFillLevel(state, level, pos);
                    }
                }
            } else if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.isAffectedByPotions()) {
                    for (MobEffectInstance effect : cauldronBlockEntity.getPotion().value().getEffects()) {
                        livingEntity.addEffect(effect);
                    }
                }
                if (entity.mayInteract(level, pos)) {
                    setFillLevel(state, level, pos, 0);
                }
            }
        }
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (6.0 + state.getValue(POTION_QUANTITY) * 3.0) / 16.0;
    }

    public static void lowerFillLevel(BlockState state, Level level, BlockPos pos) {
        int i = state.getValue(POTION_QUANTITY) - 1;
        setFillLevel(state, level, pos, i);
    }

    public static void raiseFillLevel(BlockState state, Level level, BlockPos pos) {
        int i = state.getValue(POTION_QUANTITY) - 1;
        setFillLevel(state, level, pos, i);
    }

    public static void setFillLevel(BlockState state, Level level, BlockPos pos, int i) {
        BlockState blockState = state.setValue(POTION_QUANTITY, i);
        level.setBlockAndUpdate(pos, blockState);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (CauldronBlock.shouldHandlePrecipitation(level, precipitation) && state.getValue(POTION_QUANTITY) != 3) {
            if (state.getValue(POTION_QUANTITY) == 0) {
                if (precipitation == Biome.Precipitation.RAIN) {
                    BlockState blockState = Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1);
                    level.setBlockAndUpdate(pos, blockState);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                } else if (precipitation == Biome.Precipitation.SNOW) {
                    BlockState blockState = Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1);
                    level.setBlockAndUpdate(pos, blockState);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                }
            } else if (precipitation == Biome.Precipitation.RAIN && level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity && cauldronBlockEntity.isPotionWater()) {
                raiseFillLevel(state, level, pos);
            }
        }
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(POTION_QUANTITY) == 3;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POTION_QUANTITY);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POTION_QUANTITY, MAGIC);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CauldronBlockEntity(blockPos, blockState);
    }
}
