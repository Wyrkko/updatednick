package com.example.nickmod;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class OpUtil {
    private OpUtil() {}

    /**
     * The ONE place that decides "is this player an operator".
     * If Mojang's permission API shifts again, only edit this method.
     */
    public static boolean isOp(ServerPlayerEntity player) {
        return player.getEntityWorld().getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry());
    }

    /** Console / command blocks / RCON (non-players) count as allowed. */
    public static boolean isOp(ServerCommandSource source) {
        if (source.getEntity() instanceof ServerPlayerEntity p) {
            return isOp(p);
        }
        return true;
    }
}
