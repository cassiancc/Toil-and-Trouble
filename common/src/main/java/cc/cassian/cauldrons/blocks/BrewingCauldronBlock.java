package cc.cassian.cauldrons.blocks;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronModEvents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BrewingCauldronBlock extends CauldronBlock implements EntityBlock {
    public static final IntegerProperty POTION_QUANTITY = IntegerProperty.create("potion_quantity", 0, 3);
    public static final BooleanProperty BREWING = BooleanProperty.create("brewing");
    public static final BooleanProperty HEATED = BooleanProperty.create("heated");
    public static final BooleanProperty HAS_POTION = BooleanProperty.create("has_potion");


    public BrewingCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POTION_QUANTITY, 0).setValue(BREWING, false).setValue(HEATED, false).setValue(HAS_POTION, false));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return CauldronBlockEntity::tick;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
    ) {
        return CauldronModEvents.insert(itemStack, blockState, level, pos, player, interactionHand, blockHitResult.getDirection());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity && !cauldronBlockEntity.isEmpty()) {
            CauldronModEvents.addItem(player, null, level, pos, blockHitResult.getDirection(), cauldronBlockEntity.retrieve());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (entity instanceof ItemEntity itemEntity && itemEntity.tickCount>10) {
                CauldronModEvents.insert(itemEntity.getItem(), state, level, pos, null, null, null);
            }
            else if (cauldronBlockEntity.getPotion() != PotionContents.EMPTY) {
//                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
                if (cauldronBlockEntity.isPotionWater()) {
                    if (entity.isOnFire()) {
                        entity.clearFire();
                        if (CauldronModHelpers.canInteract(level, pos, entity)) {
                            lowerFillLevel(state, level, pos);
                        }
                    }
                } else if (entity instanceof LivingEntity livingEntity && CauldronMod.CONFIG.cauldronsApplyEffects.value()) {
                    if (livingEntity.isAffectedByPotions()) {
                        for (MobEffectInstance effect : cauldronBlockEntity.getPotion().getAllEffects()) {
                            livingEntity.addEffect(new MobEffectInstance(effect.getEffect(), 1, effect.getAmplifier(), true, true));
                        }
                    }
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
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
        return new ItemStack(Blocks.CAULDRON);
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
        builder.add(POTION_QUANTITY, BREWING, HEATED, HAS_POTION);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (level instanceof ServerLevel) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), cauldronBlockEntity.getItem());
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CauldronBlockEntity(blockPos, blockState);
    }
}
