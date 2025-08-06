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

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {
    private static final float SIZE = 0.375F;
    private final ItemRenderer itemRenderer;

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CauldronBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        ItemStack itemStack = blockEntity.getItem();
        int k = (int)blockEntity.getBlockPos().asLong();

        if (itemStack != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.44921875F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0, 0, 0.0F);
            poseStack.scale(0.375F, 0.375F, 0.375F);
            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, i, j, poseStack, multiBufferSource, blockEntity.getLevel(), k);
            poseStack.popPose();
        }
    }
}
