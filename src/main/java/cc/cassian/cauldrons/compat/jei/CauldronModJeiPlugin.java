package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.registry.CauldronModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
//? if fabric && >1.21.9
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
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
		registration.addRecipeCategories(new DippingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		JeiRecipeHelpers modRecipes = new JeiRecipeHelpers();
		registration.addRecipes(BrewingRecipeCategory.CATEGORY, modRecipes.getBrewingRecipes());
		registration.addRecipes(DippingRecipeCategory.CATEGORY, modRecipes.getDippingRecipes());
	}

	@Override
	@SuppressWarnings("all")
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(Blocks.CAULDRON, BrewingRecipeCategory.CATEGORY);
		registration.addRecipeCatalyst(Blocks.CAULDRON, DippingRecipeCategory.CATEGORY);
	}

	public static final Map<Identifier, ItemStack> OVERRIDES = Map.of(
			Identifier.withDefaultNamespace("lava_cauldron"), new ItemStack(Blocks.LAVA),
			Identifier.withDefaultNamespace("water_cauldron"), new ItemStack(Blocks.WATER),
			Identifier.withDefaultNamespace("powder_snow_cauldron"), new ItemStack(Blocks.POWDER_SNOW),
			CauldronMod.of("lava"), new ItemStack(Blocks.LAVA),
			CauldronMod.of("empty"), new ItemStack(Items.AIR),
			CauldronMod.of("honey"), new ItemStack(CauldronModItems.HONEY_CONTENTS.get())
	);

	static Pair<ItemStack, ItemStack> getResultForDisplay(CauldronContents resultPotion) {
		if (resultPotion.potion().isPresent()) {
			var potion = resultPotion.potion().get();
			return new Pair<>((PotionContents.createItemStack(Items.POTION, potion)), PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), potion));
		} else if (OVERRIDES.containsKey(resultPotion.id())) {
			var stack = OVERRIDES.get(resultPotion.id());
			return new Pair<>(stack, stack);
		} else {
			//? if >1.21.2 {
			var stack = new ItemStack(BuiltInRegistries.BLOCK.getValue(resultPotion.id()));
			//?} else {
			/*var stack = new ItemStack(BuiltInRegistries.BLOCK.get(resultPotion.id()));
			*///?}
			return new Pair<>(stack, stack);
		}
	}

	//? if >1.21.9 {
	public static void syncRecipes() {
		RecipeSynchronization.synchronizeRecipeSerializer(CauldronModRecipes.BREWING_SERIALIZER.get());
		RecipeSynchronization.synchronizeRecipeSerializer(CauldronModRecipes.DIPPING_SERIALIZER.get());
	}
	//?}
}
