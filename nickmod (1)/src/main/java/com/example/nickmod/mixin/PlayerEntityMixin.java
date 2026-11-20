package com.example.nickmod.mixin;

import com.example.nickmod.NickManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    /** Used for chat messages, death messages, join/leave messages, etc. */
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void nickmod$nick(CallbackInfoReturnable<Text> cir) {
        if ((Object) this instanceof ServerPlayerEntity sp) {
            String nick = NickManager.get(sp.getUuid());
            if (nick != null) {
                cir.setReturnValue(Team.decorateName(sp.getScoreboardTeam(), Text.literal(nick)));
            }
        }
    }
}
