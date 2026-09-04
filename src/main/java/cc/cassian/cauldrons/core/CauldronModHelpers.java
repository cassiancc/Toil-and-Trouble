package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.CauldronMod;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CauldronModHelpers {
    /**
	 * A variation of the ingredient list codec that also accepts a single item - useful for backwards compatibility.
	 */
    public static Codec<List<Ingredient>> INGREDIENT_LIST_CODEC = Ingredient.CODEC.listOf(1, 9).withAlternative(Ingredient.CODEC, (ingredient -> List.of(ingredient)));
    public static StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_STREAM_CODEC = Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());

    public static boolean canInteract(Level level, BlockPos pos, Entity entity) {
        return entity.mayInteract((ServerLevel) level, pos) && !(entity instanceof ServerPlayer player && player.gameMode.getGameModeForPlayer().equals(GameType.ADVENTURE));
    }

    public static boolean hasShiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    public static @Nullable BlockState toBlock(ItemStack resultItem) {
        if (resultItem.getItem() instanceof BlockItem blockItem) {
            var blockstate = blockItem.getBlock().defaultBlockState();
            if (resultItem.has(DataComponents.BLOCK_STATE)) {
                BlockItemStateProperties blockItemStateProperties = resultItem.get(DataComponents.BLOCK_STATE);
                assert blockItemStateProperties != null;
                return blockItemStateProperties.apply(blockstate);
            }
            return blockstate;
        }
        return null;
    }

    public static @Nullable BlockState toBlock(Identifier id) {
        var potentialBlock = BuiltInRegistries.BLOCK.getOptional(id);
        return potentialBlock.map(Block::defaultBlockState).orElse(null);
    }

    public static void setBlockAndUpdate(Level level, BlockPos pos, BlockState blockState, String from) {
//        CauldronMod.LOGGER.info("%s, %s and %s".formatted(level instanceof ServerLevel ? "server" : "client", from, blockState));
        level.setBlockAndUpdate(pos, blockState);
    }
}
