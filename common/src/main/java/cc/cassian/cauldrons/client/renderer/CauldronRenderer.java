package cc.cassian.cauldrons.client.renderer;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {
    private static final float SIZE = 0.375F;
    private final ItemModelResolver itemRenderer;

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.itemModelResolver();
    }

    @Override
    public void submit(CauldronBlockEntity blockEntity, float partialTick, PoseStack poseStack, int packedLight, int packedOverlay, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, SubmitNodeCollector submitNodeCollector) {
        ItemStack itemStack = blockEntity.getItem();
        int k = (int)blockEntity.getBlockPos().asLong();

        if (itemStack != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.44921875F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0, 0, 0.0F);
            poseStack.scale(0.375F, 0.375F, 0.375F);
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            this.itemRenderer.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, k);
            itemStackRenderState.submit(poseStack, submitNodeCollector, packedLight, packedOverlay, 0);
            poseStack.popPose();
        }
    }
}
