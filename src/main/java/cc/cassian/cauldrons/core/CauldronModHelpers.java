package cc.cassian.cauldrons.core;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

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
}
