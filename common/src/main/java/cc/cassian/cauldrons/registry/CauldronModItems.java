package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class CauldronModItems {

    public static final Supplier<Item> CAULDRON_CONTENTS = register(
            "cauldron_contents", PotionItem::new, new Item.Properties()
    );

    private static Supplier<Item> register(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties) {
        return CommonRegistry.registerItem(resourceKey.location().getPath(), ()-> function.apply(properties.setId(resourceKey)));
    }

    private static ResourceKey<Item> registryKey(String string) {
        return ResourceKey.create(Registries.ITEM, CauldronMod.of(string));
    }

    private static Supplier<Item> register(String string, Function<Item.Properties, Item> function, Item.Properties properties) {
        return register(registryKey(string), function, properties);
    }

    private static Supplier<Item> register(String string, Item.Properties properties) {
        return register(string, Item::new, properties);
    }

    public static void touch() {

    }
}
