package cc.cassian.cauldrons.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class CauldronModHelpers {

    public static boolean canInteract(Level level, BlockPos pos, Entity entity) {
        return entity.mayInteract(level, pos) && !(entity instanceof ServerPlayer player && player.gameMode.getGameModeForPlayer().equals(GameType.ADVENTURE));
    }
}
