package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class BrewingRecipeCategory implements IRecipeCategory<RecipeHolder<BrewingRecipe>> {
	private final IDrawable icon;

	public BrewingRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(Blocks.CAULDRON);
	}

	public static final RecipeType<RecipeHolder<BrewingRecipe>> CATEGORY = RecipeType.createFromVanilla(CauldronModRecipes.BREWING);

	@Override
	public RecipeType<RecipeHolder<BrewingRecipe>> getRecipeType() {
		return CATEGORY;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("emi.category.toil_and_trouble.brewing");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BrewingRecipe> recipeHolder, IFocusGroup iFocusGroup) {
		var recipe = recipeHolder.value();
		// reagent
		builder.addSlot(RecipeIngredientRole.INPUT, 5, 4).addIngredients(recipe.getReagent()).setStandardSlotBackground();
		// potion item
		var input = CauldronModJeiPlugin.getResultForDisplay(recipe.getPotion());
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 41, 4).addItemStack(input.getB()).setStandardSlotBackground();
		builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(input.getA());
		// output
		var output = CauldronModJeiPlugin.getResultForDisplay(recipe.getResultPotion());
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 78, 4).addItemStack(output.getB()).setStandardSlotBackground();
		builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(output.getA());
	}

	@Override
	public int getWidth() {
		return 100;
	}

	@Override
	public int getHeight() {
		return 25;
	}

	@Override
	public void draw(RecipeHolder<BrewingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		guiGraphics.blit(CauldronMod.of("textures/gui/jei.png"), 0, 0, 0, 0, 100, 25, 100, 25);
	}

}
