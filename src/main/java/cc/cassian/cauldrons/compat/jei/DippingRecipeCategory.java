package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
//? if >1.21.2 {
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.renderer.RenderPipelines;
//?} else {
/*import mezz.jei.api.recipe.RecipeType;
*///?}
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class DippingRecipeCategory implements IRecipeCategory<RecipeHolder<AlchemyRecipe>> {

	private final IDrawable icon;

	public DippingRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(Blocks.CAULDRON);
	}

	//? if >1.21.9 {
	public static final IRecipeType<RecipeHolder<AlchemyRecipe>> CATEGORY = IRecipeType.create(CauldronModRecipes.ALCHEMY);
	@Override
	public IRecipeType<RecipeHolder<AlchemyRecipe>> getRecipeType() {
		return CATEGORY;
	}
	//?} else {
	/*public static final RecipeType<RecipeHolder<DippingRecipe>> CATEGORY = RecipeType.createFromDeferredVanilla(CauldronModRecipes.DIPPING).get();
	@Override
	public RecipeType<RecipeHolder<DippingRecipe>> getRecipeType() {
		return CATEGORY;
	}
	*///?}

	@Override
	public Component getTitle() {
		return Component.translatable("emi.category.toil_and_trouble.dipping");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlchemyRecipe> recipeHolder, IFocusGroup iFocusGroup) {
		var recipe = recipeHolder.value();
		// reagent
		recipe.getReagents().forEach(reagent -> {
			builder.addSlot(RecipeIngredientRole.INPUT, 5, 4).add(reagent).setStandardSlotBackground();
		});
		// potion item
		var input = CauldronModJeiPlugin.getResultForDisplay(recipe.getPotion());
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 41, 4).add(input.getB()).setStandardSlotBackground();
		builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(input.getA());
		// output
		var output = recipe.getResultItem();
		builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 4).add(output).setStandardSlotBackground();
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
	public void draw(RecipeHolder<AlchemyRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		guiGraphics.blit(
				//? if >1.21.2
				RenderPipelines.GUI_TEXTURED,
				CauldronMod.of("textures/gui/jei.png"), 0, 0, 0, 0, 100, 25, 100, 25);
	}
}
