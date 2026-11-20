package com.example.nickmod.mixin;

import com.example.nickmod.NickManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Clients take the name shown above a player's head from the GameProfile in the
 * player-list packet. We swap in the nickname (same UUID + skin) when it's sent.
 */
@Mixin(PlayerListS2CPacket.Entry.class)
public abstract class PlayerListEntryMixin {
    @Shadow public abstract UUID profileId();

    @Inject(method = "profile", at = @At("RETURN"), cancellable = true)
    private void nickmod$nametag(CallbackInfoReturnable<GameProfile> cir) {
        GameProfile original = cir.getReturnValue();
        if (original == null) return;
        String nick = NickManager.get(this.profileId());
        if (nick == null) return;

        // Profile names are max 16 chars; colour codes can't fit, so strip them here.
        String plain = nick.replaceAll("\u00A7.", "");
        if (plain.length() > 16) plain = plain.substring(0, 16);
        if (plain.isEmpty()) return;

        cir.setReturnValue(new GameProfile(original.id(), plain, original.properties()));
    }
}
