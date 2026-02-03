package cc.cassian.cauldrons.compat.rrv;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.registry.CauldronModItems;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.util.Map;

public class Constants {
	public static final Map<Identifier, SlotContent> OVERRIDES = Map.of(
			Identifier.withDefaultNamespace("lava_cauldron"), SlotContent.of(new FluidStack(Fluids.LAVA)),
			Identifier.withDefaultNamespace("water_cauldron"), SlotContent.of(new FluidStack(Fluids.WATER)),
			Identifier.withDefaultNamespace("powder_snow_cauldron"), SlotContent.of(Ingredient.of(Blocks.POWDER_SNOW)),
			CauldronMod.of("lava"), SlotContent.of(new FluidStack(Fluids.LAVA)),
			CauldronMod.of("empty"), SlotContent.of(Items.AIR)
	);

	public static Pair<SlotContent, SlotContent> getResultForDisplay(CauldronContents resultPotion) {
		if (resultPotion.potion().isPresent()) {
			var potion = resultPotion.potion().get();
			return new Pair<>(SlotContent.of(PotionContents.createItemStack(Items.POTION, potion)), SlotContent.of(PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), potion)));
		} else if (OVERRIDES.containsKey(resultPotion.id())) {
			var stack = OVERRIDES.get(resultPotion.id());
			return new Pair<>(stack, stack);
		} else {
			var stack = SlotContent.of(BuiltInRegistries.ITEM.getOptional(resultPotion.id()).orElse(Items.AIR));
			return new Pair<>(stack, stack);
		}
	}
}
