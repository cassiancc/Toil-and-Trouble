package cc.cassian.cauldrons.mixin;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronModEvents;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin  {

    @Inject(method = "entityInside", at = @At(value = "RETURN"))
    private void mixin(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!level.isClientSide() && state.is(Blocks.CAULDRON)) {
            if (entity instanceof ItemEntity itemEntity && itemEntity.tickCount>10) {
                var newState = CauldronModBlocks.BREWING_CAULDRON.get().defaultBlockState();
                level.setBlockAndUpdate(pos, newState);
                level.setBlockEntity(new CauldronBlockEntity(pos, newState));
                CauldronModEvents.insert(itemEntity.getItem(), newState, level, pos, null, null, null);
            }
        }
    }
}
