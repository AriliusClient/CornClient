package me.zeroX150.cornos.mixin.packet;

import me.zeroX150.cornos.features.module.impl.movement.Flight;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PlayerPositionLookS2CPacket.class)
public class PlayerPositionLookS2CPacketMixin {
    @Inject(method = "<init>(DDDFFLjava/util/Set;IZ)V", at = @At("TAIL"))
    public void init(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, int teleportId, boolean shouldDismount, CallbackInfo ci) {
        Flight.tpid = teleportId;
    }
}
