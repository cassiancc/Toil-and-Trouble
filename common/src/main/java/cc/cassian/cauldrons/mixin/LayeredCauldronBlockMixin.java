package cc.cassian.cauldrons.mixin;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronModEvents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends AbstractCauldronBlock {
    public LayeredCauldronBlockMixin(Properties properties, CauldronInteraction.InteractionMap interactions) {
        super(properties, interactions);
    }

    @Inject(method = "entityInside", at = @At(value = "RETURN"))
    private void mixin(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, CallbackInfo ci) {
        if (!level.isClientSide() && CauldronMod.CONFIG.itemEntitiesConvertCauldrons.value() && state.is(Blocks.WATER_CAULDRON)) {
            if (entity instanceof ItemEntity itemEntity && itemEntity.tickCount>10) {
                var newState =  CauldronModBlocks.BREWING_CAULDRON.get().defaultBlockState().setValue(BrewingCauldronBlock.POTION_QUANTITY, state.getValue(LayeredCauldronBlock.LEVEL));
                level.setBlockAndUpdate(pos, newState);
                level.setBlockEntity(new CauldronBlockEntity(pos, newState, Potions.WATER));
                CauldronModEvents.insert(itemEntity.getItem(), newState, level, pos, null, null, null);
            }
        }
    }
}
