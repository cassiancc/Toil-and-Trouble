package cc.cassian.cauldrons.client.renderer;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//? if >1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
*///?}
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(cauldronBlockEntity, cauldronBlockEntityRenderState, f, vec3, crumblingOverlay);


        int k = (int)cauldronBlockEntity.getBlockPos().asLong();

        List<ItemStack> items = cauldronBlockEntity.getItems();
        cauldronBlockEntityRenderState.items.clear();
        if (!items.isEmpty()) {
            items.forEach(itemStack -> {
                ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
                this.itemRenderer.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.FIXED, cauldronBlockEntity.getLevel(), null, k);
                cauldronBlockEntityRenderState.items.add(itemStackRenderState);
            });
		}

    }


    @Override
    public void submit(CauldronBlockEntityRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        AtomicReference<Float> yo = new AtomicReference<>(0.44921875F);
        blockEntityRenderState.items.forEach(itemStack -> {
            int k = (int)blockEntityRenderState.blockPos.asLong();
            poseStack.pushPose();
            poseStack.translate(0.5F, yo.get(), 0.5F);
            yo.updateAndGet(v -> (float) (v + .33));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0, 0, 0.0F);
            poseStack.scale(SIZE, SIZE, SIZE);
            itemStack.submit(poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        });

    }


}
