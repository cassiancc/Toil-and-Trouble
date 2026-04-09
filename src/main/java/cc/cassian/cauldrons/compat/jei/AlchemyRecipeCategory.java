package cc.cassian.cauldrons.compat.jei;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemyRecipeCategory implements IRecipeCategory<RecipeHolder<AlchemyRecipe>> {

	private final IDrawable icon;

	public AlchemyRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(Blocks.CAULDRON);
	}

	public static final IRecipeType<RecipeHolder<AlchemyRecipe>> CATEGORY = IRecipeType.create(CauldronModRecipes.ALCHEMY);
	@Override
	public IRecipeType<RecipeHolder<AlchemyRecipe>> getRecipeType() {
		return CATEGORY;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("emi.category.toil_and_trouble.alchemy");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlchemyRecipe> recipeHolder, IFocusGroup iFocusGroup) {
		var recipe = recipeHolder.value();
		// reagent
		int i = 0;
		List<Ingredient> reagents = recipe.getReagents();
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18).setStandardSlotBackground();
				if (i < reagents.size()) {
					slot.add(reagents.get(i++));
				}
			}
		}
		// potion item
		var input = CauldronModJeiPlugin.getResultForDisplay(recipe.getContents());
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 61, 37).add(input.getB()).setStandardSlotBackground();
		builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(input.getA());
		// output
		var output = recipe.getResultItem();
		builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19).add(output).setStandardSlotBackground();
	}

	@Override
	public int getWidth() {
		return 116;
	}

	@Override
	public int getHeight() {
		return 54;
	}

	@Override
	public void draw(RecipeHolder<AlchemyRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CauldronMod.of("textures/gui/jei.png"), 0, 0, 0, 0, 100, 25, 100, 25);
	}
}
