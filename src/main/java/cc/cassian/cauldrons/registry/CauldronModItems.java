package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.items.CauldronContentsItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

public class CauldronModItems {

    public static final Item CAULDRON_CONTENTS = register(
            "cauldron_contents", CauldronContentsItem::new, new Item.Properties()
    );

    public static final Item HONEY_CONTENTS = register(
            "honey", Item::new, new Item.Properties()
    );

    public static final Item SLIME_CONTENTS = register(
            "slime", Item::new, new Item.Properties()
    );

    public static final Item MILK_CONTENTS = register(
            "milk", Item::new, new Item.Properties()
    );

    private static Item register(String name, Function<Item.Properties, Item> function, Item.Properties properties) {
        return CauldronMod.REGISTRAR.registerItem(name, function, properties);
    }

    public static void touch() {

    }
}
