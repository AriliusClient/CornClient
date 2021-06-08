/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: CombatUpdateHook
# Created by constantin at 01:24, Mär 31 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.mixin.packet;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.MemeSFX;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCombatS2CPacket.class)
public class CombatEventS2CPacketMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    public void init(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        if (ModuleRegistry.search(MemeSFX.class).isEnabled()) {
            assert Cornos.minecraft.player != null;
            Cornos.minecraft.player.playSound(Cornos.BONG_SOUND, 1f, 1f);
        }
    }
}
