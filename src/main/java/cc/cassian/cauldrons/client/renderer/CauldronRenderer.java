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
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
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

import java.util.ArrayList;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity
        //? if >1.21.9
        , CauldronBlockEntityRenderState
        > {
    private static final float SIZE = 0.375F;
    //? if >1.21.9 {
    private final ItemModelResolver itemRenderer;
    //?} else {
    /*private final ItemRenderer itemRenderer;
    *///?}

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        //? if >1.21.9 {
        this.itemRenderer = context.itemModelResolver();
         //?} else {
        /*this.itemRenderer = context.getItemRenderer();
        *///?}
    }

    //? if >1.21.9 {
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

        ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
        int k = (int)cauldronBlockEntity.getBlockPos().asLong();

        this.itemRenderer
                .updateForTopItem(itemStackRenderState, cauldronBlockEntity.getItem(), ItemDisplayContext.FIXED, cauldronBlockEntity.getLevel(), null, k);
        cauldronBlockEntityRenderState.item = itemStackRenderState;
    }


    @Override
    public void submit(CauldronBlockEntityRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ItemStackRenderState itemStack = blockEntityRenderState.item;
        int k = (int)blockEntityRenderState.blockPos.asLong();

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.44921875F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.translate(0.0, 0, 0.0F);
        poseStack.scale(SIZE, SIZE, SIZE);
        itemStack.submit(poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
        //?} else {
    /*@Override
    public void render(CauldronBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
                       //? if >1.21.4
                       ,Vec3 cameraPos
    ) {
        ItemStack itemStack = blockEntity.getItem();
        int k = (int)blockEntity.getBlockPos().asLong();

        if (itemStack != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.44921875F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0, 0, 0.0F);
            poseStack.scale(SIZE, SIZE, SIZE);
            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), k);
            poseStack.popPose();
        }
    }
    *///?}


}
