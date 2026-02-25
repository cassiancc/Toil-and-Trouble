package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.registry.CauldronModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class Constants {
	public static final Map<Identifier, ItemStack> OVERRIDES = Map.of(
			Identifier.withDefaultNamespace("lava_cauldron"), new ItemStack(Blocks.LAVA),
			Identifier.withDefaultNamespace("water_cauldron"), new ItemStack(Blocks.WATER),
			Identifier.withDefaultNamespace("powder_snow_cauldron"), new ItemStack(Blocks.POWDER_SNOW),
			CauldronMod.of("lava"), new ItemStack(Blocks.LAVA),
			CauldronMod.of("empty"), new ItemStack(Items.AIR),
			CauldronMod.of("honey"), new ItemStack(CauldronModItems.HONEY_CONTENTS)
	);
}
