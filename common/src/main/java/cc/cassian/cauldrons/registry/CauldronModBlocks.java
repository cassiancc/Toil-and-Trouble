package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class CauldronModBlocks {

    public static final Supplier<Block> BREWING_CAULDRON = register(
            "cauldron", BrewingCauldronBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
    );

    private static Supplier<Block> register(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        // register block
        // return
        return CommonRegistry.registerBlock(resourceKey.location().getPath(), ()-> function.apply(properties));
    }

    private static ResourceKey<Block> registryKey(String string) {
        return ResourceKey.create(Registries.BLOCK, CauldronMod.of(string));
    }

    private static Supplier<Block> register(String string, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return register(registryKey(string), function, properties);
    }

    private static Supplier<Block> register(String string, BlockBehaviour.Properties properties) {
        return register(string, Block::new, properties);
    }

    public static void touch() {

    }
}
