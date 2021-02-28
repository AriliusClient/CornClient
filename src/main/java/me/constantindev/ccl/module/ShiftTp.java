package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.Num;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class ShiftTp extends Module {
    public ShiftTp() {
        super("ShiftTp", "Teleports you when shifting. Useful for phasing through walls", MType.MOVEMENT);
        this.mconf.add(new Num("multiplier", 4.0, 15, 1));
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute() {
        int mtp = (int) ((Num) this.mconf.getByName("multiplier")).getValue();
        if (mc.options.keySneak.wasPressed()) {
            assert mc.player != null;
            Vec3d pos = mc.player.getPos();
            Vec3d rot = mc.player.getRotationVector();
            rot = rot.multiply(1, 0, 1);
            pos = pos.add(rot.multiply(mtp));
            mc.player.world.sendPacket(new PlayerMoveC2SPacket.PositionOnly(pos.x, pos.y, pos.z, false));
            mc.player.updatePosition(pos.x, pos.y, pos.z);

        }
        super.onExecute();
    }
}
