package me.zeroX150.cornos.features.command.impl;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import me.zeroX150.cornos.features.module.impl.movement.Flight;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Teleport extends Command {
    public Teleport() {
        super("Teleport", "Teleport anywhere using special bruddah technology", new String[]{"teleport", "tp", "goto"});
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 3) {
            STL.notifyUser("can i get x y and z coords");
            return;
        }
        String x = args[0];
        String y = args[1];
        String z = args[2];
        if (!STL.tryParseL(x)) {
            STL.notifyUser("\"" + x + "\" aint a number");
            return;
        }
        if (!STL.tryParseL(y)) {
            STL.notifyUser("\"" + y + "\" aint a number");
            return;
        }
        if (!STL.tryParseL(z)) {
            STL.notifyUser("\"" + z + "\" aint a number");
            return;
        }
        double xd = Double.parseDouble(x);
        double yd = Double.parseDouble(y);
        double zd = Double.parseDouble(z);
        Vec3d np = new Vec3d(xd, yd, zd);
        Vec3d ppos = Cornos.minecraft.player.getPos();
        PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 1850, ppos.z,
                Cornos.minecraft.player.isOnGround());
        PlayerMoveC2SPacket p1 = new PlayerMoveC2SPacket.PositionAndOnGround(np.x, np.y, np.z,
                Cornos.minecraft.player.isOnGround());
        Cornos.minecraft.getNetworkHandler().sendPacket(p1);
        Cornos.minecraft.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(++Flight.tpid));
        Cornos.minecraft.getNetworkHandler().sendPacket(p);
        Cornos.minecraft.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(++Flight.tpid));
        Cornos.minecraft.getNetworkHandler().sendPacket(p1);
        Cornos.minecraft.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(Flight.tpid));
        super.onExecute(args);
    }
}
