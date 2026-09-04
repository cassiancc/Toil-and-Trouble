package cc.cassian.cauldrons.client.renderer;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//? if >1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
*///?} else {
import net.minecraft.client.renderer.entity.ItemRenderer;
//?}
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {
    private static final float SIZE = 0.375F;
    private final ItemRenderer itemRenderer;

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

	@Override
	public void render(CauldronBlockEntity cauldronBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		int k = (int)cauldronBlockEntity.getBlockPos().asLong();

		List<ItemStack> items = cauldronBlockEntity.getItems();
		submit(cauldronBlockEntity, poseStack, items, packedLight, packedOverlay, bufferSource, k);
	}

    public void submit(CauldronBlockEntity blockEntity, PoseStack poseStack, List<ItemStack> items, int packedLight, int packedOverlay, MultiBufferSource bufferSource, int k) {
        AtomicReference<Float> yPos = new AtomicReference<>(0.44921875F);
		for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
			poseStack.pushPose();
			if (i==0) {
				poseStack.translate(0.5F, yPos.get(), 0.5F);
				yPos.updateAndGet(v -> (float) (v + .2));
				rotate(poseStack, Axis.XP, 90.0F);
				poseStack.translate(0.0, 0, 0.0F);
			}
			else if (i<5) {
				poseStack.translate(0.5F, 0.44921875F, 0.5F);
                Direction direction = Direction.from2DDataValue((i + Direction.UP.get2DDataValue()) % 4);
                float angle = -direction.toYRot();
				rotate(poseStack, Axis.YP, angle);
				rotate(poseStack, Axis.XP, 90.0F);
                poseStack.translate(-0.2125F, -0.2125F, 0.0F);
			} else {
				poseStack.translate(0.5F, 0.64921875F, 0.5F);
				Direction direction = Direction.from2DDataValue((i + Direction.UP.get2DDataValue()) % 4);
				float angle = -direction.toYRot();
				rotate(poseStack, Axis.YP, angle);
				rotate(poseStack, Axis.XP, 90.0F);
				poseStack.translate(-0.1125F, -0.1125F, 0.0F);
			}
			poseStack.scale(SIZE, SIZE, SIZE);
			this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), k);
			poseStack.popPose();
		}
	}

	private static void rotate(PoseStack poseStack, Axis axis, float angle) {
		//? if <26.3 {
		poseStack.mulPose(axis.rotationDegrees(angle));
		//?} else {
		/*poseStack.rotateDegrees(axis, angle);
		*///?}
	}


}
