package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.registry.CauldronModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
//? if fabric && >1.21.9
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Blocks;
import oshi.util.tuples.Pair;

import java.util.Map;

public class CauldronModJeiPlugin implements IModPlugin {

	@Override
	public Identifier getPluginUid() {
		return CauldronMod.of("plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new BrewingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new AlchemyRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		JeiRecipeHelpers modRecipes = new JeiRecipeHelpers();
		registration.addRecipes(BrewingRecipeCategory.CATEGORY, modRecipes.getBrewingRecipes());
		registration.addRecipes(AlchemyRecipeCategory.CATEGORY, modRecipes.getDippingRecipes());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(BrewingRecipeCategory.CATEGORY, Blocks.CAULDRON);
		registration.addCraftingStation(AlchemyRecipeCategory.CATEGORY, Blocks.CAULDRON);
	}

	public static final Map<Identifier, ItemStack> OVERRIDES = Map.of(
			Identifier.withDefaultNamespace("lava_cauldron"), new ItemStack(Blocks.LAVA),
			Identifier.withDefaultNamespace("water_cauldron"), new ItemStack(Blocks.WATER),
			Identifier.withDefaultNamespace("powder_snow_cauldron"), new ItemStack(Blocks.POWDER_SNOW),
			CauldronMod.of("lava"), new ItemStack(Blocks.LAVA),
			CauldronMod.of("empty"), new ItemStack(Items.AIR),
			CauldronMod.of("honey"), new ItemStack(CauldronModItems.HONEY_CONTENTS)
	);

	static Pair<ItemStack, ItemStack> getResultForDisplay(CauldronContents resultPotion) {
		if (resultPotion.potion().isPresent()) {
			var potion = resultPotion.potion().get();
			return new Pair<>((PotionContents.createItemStack(Items.POTION, potion)), PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS, potion));
		} else if (OVERRIDES.containsKey(resultPotion.id())) {
			var stack = OVERRIDES.get(resultPotion.id());
			return new Pair<>(stack, stack);
		} else {
			var stack = new ItemStack(BuiltInRegistries.BLOCK.getValue(resultPotion.id()));
			return new Pair<>(stack, stack);
		}
	}
}
