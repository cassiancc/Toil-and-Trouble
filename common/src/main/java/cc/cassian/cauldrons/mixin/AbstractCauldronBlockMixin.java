package cc.cassian.cauldrons.mixin;

import cc.cassian.cauldrons.core.CauldronModEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin {
    @Inject(method = "useItemOn", at = @At(value = "RETURN"), cancellable = true)
    private void mixin(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue().equals(InteractionResult.TRY_WITH_EMPTY_HAND) || cir.getReturnValue().equals(InteractionResult.PASS)) {
            cir.setReturnValue(CauldronModEvents.useBlock(player, level, interactionHand, blockPos, blockHitResult.getDirection()));
        }
    }
}
