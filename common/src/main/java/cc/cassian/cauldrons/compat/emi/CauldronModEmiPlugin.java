package cc.cassian.cauldrons.compat.emi;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.DippingRecipe;
import cc.cassian.cauldrons.registry.CauldronModItems;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.util.Map;

@EmiEntrypoint
public class CauldronModEmiPlugin implements EmiPlugin {

    public static final EmiStack CAULDRON_WORKSTATION = EmiStack.of(Items.CAULDRON);

    public static final EmiRecipeCategory BREWING_CATEGORY
            = new EmiRecipeCategory(CauldronMod.of("brewing"), CAULDRON_WORKSTATION);

    public static final EmiRecipeCategory DIPPING_CATEGORY
            = new EmiRecipeCategory(CauldronMod.of("dipping"), CAULDRON_WORKSTATION);

    public static final Map<ResourceLocation, EmiStack> OVERRIDES = Map.of(
            ResourceLocation.withDefaultNamespace("lava_cauldron"), EmiStack.of(Fluids.LAVA),
            ResourceLocation.withDefaultNamespace("water_cauldron"), EmiStack.of(Fluids.WATER),
            ResourceLocation.withDefaultNamespace("powder_snow_cauldron"), EmiStack.of(Blocks.POWDER_SNOW)
    );

    static Pair<EmiStack, EmiStack> getResultForDisplay(CauldronContents resultPotion) {
        if (resultPotion.potion().isPresent()) {
            var potion = resultPotion.potion().get();
            return new Pair<>(EmiStack.of(PotionContents.createItemStack(Items.POTION, potion)), EmiStack.of(PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS.get(), potion)));
        } else if (OVERRIDES.containsKey(resultPotion.id())) {
            var stack = OVERRIDES.get(resultPotion.id());
            return new Pair<>(stack, stack);
        } else {
            var stack = EmiStack.of(BuiltInRegistries.BLOCK.get(resultPotion.id()));
            return new Pair<>(stack, stack);
        }
    }


    @Override
    public void register(EmiRegistry registry) {
        RecipeManager manager = registry.getRecipeManager();
        var access = Minecraft.getInstance().level.registryAccess();

        // brewing
        registry.addCategory(BREWING_CATEGORY);
        registry.addWorkstation(BREWING_CATEGORY, CAULDRON_WORKSTATION);
        for (RecipeHolder<BrewingRecipe> recipe : manager.getAllRecipesFor(CauldronModRecipes.BREWING.get())) {
            registry.addRecipe(new BrewingEmiRecipe(recipe, access));
        }
        // dipping
        registry.addCategory(DIPPING_CATEGORY);
        registry.addWorkstation(DIPPING_CATEGORY, CAULDRON_WORKSTATION);
        for (RecipeHolder<DippingRecipe> recipe : manager.getAllRecipesFor(CauldronModRecipes.DIPPING.get())) {
            registry.addRecipe(new DippingEmiRecipe(recipe, access));
        }

    }
}
