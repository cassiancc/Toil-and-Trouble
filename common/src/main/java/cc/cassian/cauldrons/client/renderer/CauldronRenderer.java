package cc.cassian.cauldrons.client.renderer;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.CampfireRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity, CauldronBlockEntityRenderState> {
    private static final float SIZE = 0.375F;
    private final ItemModelResolver itemRenderer;

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.itemModelResolver();
    }

    @Override
    public CauldronBlockEntityRenderState createRenderState() {
        return new CauldronBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(
            CauldronBlockEntity cauldronBlockEntity,
            CauldronBlockEntityRenderState cauldronBlockEntityRenderState,
            float f,
            Vec3 vec3,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(cauldronBlockEntity, cauldronBlockEntityRenderState, f, vec3, crumblingOverlay);

        ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
        int k = (int)cauldronBlockEntity.getBlockPos().asLong();

        this.itemRenderer
                .updateForTopItem(itemStackRenderState, cauldronBlockEntity.getItem(), ItemDisplayContext.FIXED, cauldronBlockEntity.getLevel(), null, k);
        cauldronBlockEntityRenderState.item = itemStackRenderState;
    }

    @Override
    public void submit(CauldronBlockEntityRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        ItemStackRenderState itemStack = blockEntityRenderState.item;
        int k = (int)blockEntityRenderState.blockPos.asLong();

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.44921875F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.translate(0.0, 0, 0.0F);
        poseStack.scale(0.375F, 0.375F, 0.375F);
        itemStack.submit(poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
