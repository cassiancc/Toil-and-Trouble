package cc.cassian.cauldrons.client;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CauldronModClient {
    public static List<Component> getForWaila(BlockState state, CauldronBlockEntity entity) {
        List<Component> iTooltip = new ArrayList<>();
        if (entity.getContents() != CauldronContents.EMPTY) {
            iTooltip.add(Component.translatable("gui.toil_and_trouble.doses", state.getValue(BrewingCauldronBlock.POTION_QUANTITY)).withStyle(ChatFormatting.DARK_PURPLE));
            if (entity.getContents().isPotion()) {
                var item = Items.POTION;
                if (entity.isPotionSplash())
                    item = Items.SPLASH_POTION;
                else if (entity.isPotionLingering())
                    item = Items.LINGERING_POTION;
                iTooltip.add(CauldronContents.createItemStack(item, entity.getContents()).getHoverName());
                if (CauldronModHelpers.hasShiftDown())
                    PotionContents.addPotionTooltip(entity.getContents().getAllEffects(), iTooltip::add, 0, 0);
            } else {
                iTooltip.add(Component.translatableWithFallback(entity.getContents().id().toLanguageKey("cauldron"), WordUtils.capitalize(entity.getContents().id().getPath().replace("_", " "))));
            }
            if (!entity.getItems().isEmpty()) {
                iTooltip.add(Component.empty());
            }
        }
        return iTooltip;
    }

	public static BlockTintSource getColor() {
		return new BlockTintSource() {
			public int color(final BlockState state) {
				return 9551193;
			}

			public int colorInWorld(final BlockState state, final BlockAndTintGetter view, final BlockPos pos) {
				if (view.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
					return cauldronBlockEntity.getPotionColour();
				}
				return 9551193;
			}

			public int colorAsTerrainParticle(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
				return -1;
			}
		};
	}

	public static int getColor(ItemStack stack, int i) {
		if (stack.has(DataComponents.POTION_CONTENTS))
			return stack.get(DataComponents.POTION_CONTENTS).getColor();
		return 9551193;
	}
}
