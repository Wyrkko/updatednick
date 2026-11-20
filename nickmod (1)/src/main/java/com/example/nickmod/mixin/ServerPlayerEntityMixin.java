package com.example.nickmod.mixin;

import com.example.nickmod.NickManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    /** Tab list name. */
    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void nickmod$tab(CallbackInfoReturnable<Text> cir) {
        String nick = NickManager.get(((ServerPlayerEntity) (Object) this).getUuid());
        if (nick != null) {
            cir.setReturnValue(Text.literal(nick));
        }
    }
}
