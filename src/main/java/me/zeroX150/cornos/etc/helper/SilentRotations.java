package me.zeroX150.cornos.etc.helper;

import me.zeroX150.cornos.Cornos;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class SilentRotations {

    public static float camPitch;
    public static float camYaw;
    public static float prevCamPitch;
    public static float prevCamYaw;

    public static boolean nextRotationSilent;

    public static void doSilentRotation(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        assert Cornos.minecraft.player != null;
        float pitch = Cornos.minecraft.player.getPitch();
        float yaw = Cornos.minecraft.player.getYaw();
        Cornos.minecraft.player.lookAt(anchorPoint, target);
        Entity cameraEntity = Cornos.minecraft.cameraEntity;
        assert cameraEntity != null;
        camPitch = cameraEntity.getPitch();
        camYaw = cameraEntity.getYaw();
        prevCamPitch = cameraEntity.prevPitch;
        prevCamYaw = cameraEntity.prevYaw;
        cameraEntity.setPitch(pitch);
        cameraEntity.setYaw(yaw);
        nextRotationSilent = true;
    }

}
