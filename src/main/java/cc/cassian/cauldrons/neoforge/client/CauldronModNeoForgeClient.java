package cc.cassian.cauldrons.neoforge.client;

//? if neoforge {

/*import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.client.renderer.CauldronRenderer;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.items.CauldronContentsItem;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
//? if <1.21.5 {
/^import net.minecraft.util.FastColor;
^///?}
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = CauldronMod.MOD_ID, value = Dist.CLIENT)
public final class CauldronModNeoForgeClient {

    public static RecipeMap map;

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(((state, view, pos, tintIndex) -> {
            if (view == null || tintIndex != 0) return 9551193;
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldronBlockEntity) {
                return cauldronBlockEntity.getPotionColour();
            }
            return 9551193;
        }), CauldronModBlocks.BREWING_CAULDRON.get());

    }

    //? if <1.21.5 {
    /^@SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(((stack, tintIndex) -> FastColor.ARGB32.opaque(stack.get(DataComponents.POTION_CONTENTS).getColor())), CauldronModItems.CAULDRON_CONTENTS.get());
    }
    ^///?}

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), CauldronRenderer::new);
    }

    @SubscribeEvent
    public static void register(RecipesReceivedEvent event) {
        CauldronModNeoForgeClient.map = event.getRecipeMap();
    }
}
*///?}