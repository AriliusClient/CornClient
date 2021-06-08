package me.zeroX150.cornos.features.module.impl.movement;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class ShiftTp extends Module {
    public ShiftTp() {
        super("ShiftTp", "you shift and you get teleported", ModuleType.MOVEMENT);
        this.mconf.add(new MConfNum("multiplier", 4.0, 15, 1, "Amount of blocks to teleport"));
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
            Cornos.minecraft.player.world.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, true));
            Cornos.minecraft.player.updatePosition(pos.x, pos.y, pos.z);

        }
        super.onExecute();
    }
}
