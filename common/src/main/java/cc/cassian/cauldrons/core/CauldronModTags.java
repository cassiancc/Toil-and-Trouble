package cc.cassian.cauldrons.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static cc.cassian.cauldrons.CauldronMod.MOD_ID;

public class CauldronModTags {
    public static final TagKey<Item> CANNOT_FILL_CAULDRON = itemTagKey("cannot_fill_cauldron");
    public static final TagKey<Item> CREATES_SPLASH_POTIONS = itemTagKey("creates_splash_potions");
    public static final TagKey<Item> CREATES_LINGERING_POTIONS = itemTagKey("creates_lingering_potions");
    public static final TagKey<Block> HEATS_CAULDRONS = blockTagKey("heats_cauldrons");

    public static TagKey<Block> blockTagKey(String id) {
        return blockTagKey(MOD_ID, id);
    }

    public static TagKey<Block> blockTagKey(String namespace, String id) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, id));
    }

    public static TagKey<Item> itemTagKey(String id) {
        return itemTagKey(MOD_ID, id);
    }

    public static TagKey<Item> itemTagKey(String namespace, String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, id));
    }
}
