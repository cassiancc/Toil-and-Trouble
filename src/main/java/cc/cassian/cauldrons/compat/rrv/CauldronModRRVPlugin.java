package cc.cassian.cauldrons.compat.rrv;

//? if >1.21.10 {

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.brewing.CauldronBrewingViewRecipe;
import cc.cassian.cauldrons.compat.rrv.dipping.CauldronDippingServerRecipe;
import cc.cassian.cauldrons.compat.rrv.dipping.CauldronDippingViewRecipe;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.registry.CauldronModItems;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.util.Collections;
import java.util.Map;

public class CauldronModRRVPlugin implements ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        // register the server recipes
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.BREWING.get()).forEach(recipe -> {
                recipeList.add(new CauldronBrewingServerRecipe(recipe.getReagent(), recipe.getPotion(), recipe.getResultPotion()));
            });
            ServerRecipeManager.INSTANCE.getRecipesForType(CauldronModRecipes.DIPPING.get()).forEach(recipe -> {
                recipeList.add(new CauldronDippingServerRecipe(recipe.getReagent(), recipe.getPotion(), recipe.getResultItem()));
            });
        });

        // and all the client recipes
        ItemView.registerClientRecipeWrapper(CauldronBrewingServerRecipe.TYPE, modRecipe -> {
            return Collections.singletonList(new CauldronBrewingViewRecipe(modRecipe));
        });
        ItemView.registerClientRecipeWrapper(CauldronDippingServerRecipe.TYPE, modRecipe -> {
            return Collections.singletonList(new CauldronDippingViewRecipe(modRecipe));
        });

        // hide cauldron contents
        // TODO move this into an event if/when neoforge 1.21.8
        ItemView.excludeItem(CauldronModItems.CAULDRON_CONTENTS.get());
    }

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
//?}