package cc.cassian.cauldrons.blocks;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModEvents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import cc.cassian.mru.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class BrewingCauldronBlock extends CauldronBlock implements EntityBlock {
    public static final IntegerProperty POTION_QUANTITY = IntegerProperty.create("potion_quantity", 0, 3);
    public static final BooleanProperty BREWING = BooleanProperty.create("brewing");
    public static final BooleanProperty HEATED = BooleanProperty.create("heated");
    public static final EnumProperty<ContentsProperty> CONTENTS = EnumProperty.create("contents", ContentsProperty.class, ContentsProperty.values());

    public enum ContentsProperty implements StringRepresentable {
        EMPTY("empty"), WATER("water"), LAVA("lava"), POTION("potion"), HONEY("honey"), SLIME("slime"), MILK("milk"), CHORUS_HONEY("chorus_honey");
        private final String name;

        public static ContentsProperty toBlockStateProperty(CauldronContents contents1) {
            if (contents1.is(Potions.WATER)) return ContentsProperty.WATER;
            else if (contents1.potion().isPresent()) return ContentsProperty.POTION;
            else if (contents1.is("honey")) return ContentsProperty.HONEY;
            else if (contents1.is("slime")) return ContentsProperty.SLIME;
            else if (contents1.is("milk")) return ContentsProperty.MILK;
            else if (contents1.is(Identifier.fromNamespaceAndPath("chorus_honey", "chorus_honey"))) return ContentsProperty.CHORUS_HONEY;
            else if (contents1.is("lava")) return ContentsProperty.LAVA;
            else if (contents1.is(Identifier.withDefaultNamespace("air"))) return ContentsProperty.EMPTY;
            return ContentsProperty.POTION;
        }

        ContentsProperty(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public BrewingCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POTION_QUANTITY, 0).setValue(BREWING, false).setValue(HEATED, false).setValue(CONTENTS, ContentsProperty.EMPTY));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return CauldronBlockEntity::tick;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
    ) {
		return CauldronModEvents.insert(itemStack, blockState, level, pos, player, interactionHand, blockHitResult.getDirection());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
		if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
//            if (!cauldronBlockEntity.getContents().equals(CauldronContents.EMPTY) && blockState.getOptionalValue(POTION_QUANTITY).orElse(0).equals(0)) {
//                cauldronBlockEntity.setContents(CauldronContents.EMPTY);
//            }
			if (!cauldronBlockEntity.isEmpty()) {
				CauldronModEvents.giveToPlayer(player, null, level, pos, blockHitResult.getDirection(), cauldronBlockEntity.retrieve());
				return InteractionResult.SUCCESS;
			} else if (CauldronMod.CONFIG.client.showContentsWhenInteracting.value()) {
                player.displayClientMessage(cauldronBlockEntity.getContentsName(), true);
            }
            //reset to vanilla
			if (cauldronBlockEntity.getItems().isEmpty() && cauldronBlockEntity.getContents().equals(CauldronContents.EMPTY) && blockState.getOptionalValue(POTION_QUANTITY).orElse(0).equals(0)) {
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
			}
		}
        return InteractionResult.PASS;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (entity instanceof ItemEntity itemEntity && itemEntity.tickCount>10) {
                CauldronModEvents.insert(itemEntity.getItem(), state, level, pos, null, null, null);
            }
            else if (cauldronBlockEntity.getContents() != CauldronContents.EMPTY) {
//                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
                if (cauldronBlockEntity.isPotionWater()) {
                    if (entity.isOnFire()) {
                        entity.clearFire();
                        if (CauldronModHelpers.canInteract(level, pos, entity)) {
                            lowerFillLevel(state, level, pos);
                        }
                    }
                } else if (cauldronBlockEntity.getContents().is("lava")) {
                    entity.lavaHurt();
                } else if (entity instanceof LivingEntity livingEntity && CauldronMod.CONFIG.cauldronsApplyEffects.value()) {
                    if (livingEntity.isAffectedByPotions()) {
                        if (cauldronBlockEntity.getContents().is("milk")) {
                            livingEntity.removeAllEffects();
                        }
                        else if (cauldronBlockEntity.getContents().is("slime")) {
                            Holder<MobEffect> effect = Platform.INSTANCE.isLoaded("slime_time") ? BuiltInRegistries.MOB_EFFECT.getOrThrow(ResourceKey.create(Registries.MOB_EFFECT, Identifier.fromNamespaceAndPath("slime_time", "slime_time"))) : MobEffects.OOZING;
                            livingEntity.addEffect(new MobEffectInstance(effect, 1, 1, true, true));
                        }

                        else {
                            for (MobEffectInstance effect : cauldronBlockEntity.getContents().getAllEffects()) {
                                livingEntity.addEffect(new MobEffectInstance(effect.getEffect(), 1, effect.getAmplifier(), true, true));
                            }
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
        setFillLevel(state, level, pos, i, null);
    }

    public static void raiseFillLevel(BlockState state, Level level, BlockPos pos) {
        int i = state.getValue(POTION_QUANTITY) - 1;
        setFillLevel(state, level, pos, i, null);
    }

    public static void setFillLevel(BlockState state, @Nullable Level level, BlockPos pos, int i, @Nullable ContentsProperty contentsProperty) {
        if (i > 3 || i < -1 || level == null) return;
        BlockState blockState = state.setValue(POTION_QUANTITY, i);
        if (contentsProperty != null) {
            blockState = blockState.setValue(CONTENTS, contentsProperty);
        }
        if (i == 0) {
            blockState = Blocks.CAULDRON.defaultBlockState();
        }
        CauldronModHelpers.setBlockAndUpdate(level, pos, blockState, "update fill level");
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (CauldronBlock.shouldHandlePrecipitation(level, precipitation) && state.getValue(POTION_QUANTITY) != 3) {
            if (state.getValue(POTION_QUANTITY) == 0) {
                if (precipitation == Biome.Precipitation.RAIN) {
                    BlockState blockState = Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1);
                    CauldronModHelpers.setBlockAndUpdate(level, pos, blockState, "rain");
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                } else if (precipitation == Biome.Precipitation.SNOW) {
                    BlockState blockState = Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1);
                    CauldronModHelpers.setBlockAndUpdate(level, pos, blockState, "snow");
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                }
            } else if (precipitation == Biome.Precipitation.RAIN && level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity && cauldronBlockEntity.isPotionWater()) {
                raiseFillLevel(state, level, pos);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
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
        builder.add(POTION_QUANTITY, BREWING, HEATED, CONTENTS);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CauldronBlockEntity(blockPos, blockState);
    }
}
