package me.constantindev.ccl.module.MOVEMENT;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.MConfNum;
import me.constantindev.ccl.etc.ms.ModuleType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class ShiftTp extends Module {
    public ShiftTp() {
        super("ShiftTp", "you shift and you get teleported", ModuleType.MOVEMENT);
        this.mconf.add(new MConfNum("multiplier", 4.0, 15, 1));
    }

    @Override
    public void onExecute() {
        int mtp = (int) ((MConfNum) this.mconf.getByName("multiplier")).getValue();
        if (Cornos.minecraft.options.keySneak.wasPressed()) {
            assert Cornos.minecraft.player != null;
            Vec3d pos = Cornos.minecraft.player.getPos();
            Vec3d rot = Cornos.minecraft.player.getRotationVector();
            rot = rot.multiply(1, 0, 1);
            pos = pos.add(rot.multiply(mtp));
            Cornos.minecraft.player.world.sendPacket(new PlayerMoveC2SPacket.PositionOnly(pos.x, pos.y, pos.z, false));
            Cornos.minecraft.player.updatePosition(pos.x, pos.y, pos.z);

        }
        super.onExecute();
    }
}
