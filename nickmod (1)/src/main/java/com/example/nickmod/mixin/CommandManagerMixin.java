package com.example.nickmod.mixin;

import com.example.nickmod.OpUtil;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    /** Every command from every source goes through here, so non-op players can't run any. */
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void nickmod$blockCommands(ParseResults<ServerCommandSource> parse, String command, CallbackInfo ci) {
        ServerCommandSource source = parse.getContext().getSource();
        if (source.getEntity() instanceof ServerPlayerEntity player && !OpUtil.isOp(player)) {
            player.sendMessage(Text.literal("You can't use commands.").formatted(Formatting.RED), false);
            ci.cancel();
        }
    }
}
