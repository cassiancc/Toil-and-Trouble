package cc.cassian.cauldrons.client.renderer;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {
    private static final float SIZE = 0.375F;
    private final ItemRenderer itemRenderer;

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CauldronBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        ItemStack itemStack = blockEntity.getItem();
        int k = (int)blockEntity.getBlockPos().asLong();

        if (itemStack != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.44921875F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0, 0, 0.0F);
            poseStack.scale(0.375F, 0.375F, 0.375F);
            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), k);
            poseStack.popPose();
        }
    }
}
