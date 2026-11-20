package com.example.nickmod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NickMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nickmod");

    @Override
    public void onInitialize() {
        NickManager.load();

        // ---- Block chat for non-ops ----
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (OpUtil.isOp(sender)) return true;
            sender.sendMessage(Text.literal("Chat is disabled for non-operators.")
                    .formatted(net.minecraft.util.Formatting.RED), false);
            return false;
        });
        // (Commands are blocked in CommandManagerMixin.)

        // ---- /nick ----
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(literal("nick")
                .requires(OpUtil::isOp)
                .then(argument("target", EntityArgumentType.player())
                    .then(literal("reset").executes(ctx -> {
                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                        NickManager.remove(target.getUuid());
                        refresh(target);
                        ctx.getSource().sendFeedback(() ->
                            Text.literal("Reset nickname of " + target.getGameProfile().name()), true);
                        return 1;
                    }))
                    .then(argument("nickname", StringArgumentType.greedyString()).executes(ctx -> {
                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                        String nick = StringArgumentType.getString(ctx, "nickname").replace('&', '\u00A7');
                        NickManager.set(target.getUuid(), nick);
                        refresh(target);
                        ctx.getSource().sendFeedback(() ->
                            Text.literal(target.getGameProfile().name() + " is now nicknamed " + nick), true);
                        return 1;
                    }))
                )
            )
        );
    }

    /**
     * Re-send the player to everyone's tab list (tab name + profile name).
     * The profile name is what vanilla clients show over the head.
     */
    private static void refresh(ServerPlayerEntity player) {
        var pm = player.getEntityWorld().getServer().getPlayerManager();
        pm.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
        pm.sendToAll(new net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket(
            java.util.List.of(player.getUuid())));
        pm.sendToAll(PlayerListS2CPacket.entryFromPlayer(java.util.List.of(player)));
    }
}
