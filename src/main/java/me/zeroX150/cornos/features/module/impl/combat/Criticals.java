package me.zeroX150.cornos.features.module.impl.combat;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.event.EventHelper;
import me.zeroX150.cornos.etc.event.EventType;
import me.zeroX150.cornos.etc.event.arg.PacketEvent;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Criticals extends Module {
    MConfMultiOption mode = new MConfMultiOption("mode", "packet", new String[]{"packet", "tphop"}, "The mode");

    public Criticals() {
        super("Criticals", "more damage", ModuleType.COMBAT);
        this.mconf.add(mode);
        Module parent = this;
        EventHelper.BUS.registerEvent(EventType.ONPACKETSEND, event -> {
            PacketEvent pe = (PacketEvent) event;
            if (parent.isEnabled() && (pe.packet instanceof PlayerInteractEntityC2SPacket)) {
                PlayerInteractEntityC2SPacket p = (PlayerInteractEntityC2SPacket) pe.packet;
                assert Cornos.minecraft.player != null;
                Vec3d pos = Cornos.minecraft.player.getPos();
                switch (mode.value) {
                    case "packet":
                        PlayerMoveC2SPacket.PositionAndOnGround mp = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y + 0.2,
                                pos.z, true);
                        PlayerMoveC2SPacket.PositionAndOnGround mp1 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z,
                                false);
                        PlayerMoveC2SPacket.PositionAndOnGround mp2 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x,
                                pos.y + 0.000011, pos.z, false);
                        PlayerMoveC2SPacket.PositionAndOnGround mp3 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z,
                                false);
                        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(mp);
                        Cornos.minecraft.getNetworkHandler().sendPacket(mp1);
                        Cornos.minecraft.getNetworkHandler().sendPacket(mp2);
                        Cornos.minecraft.getNetworkHandler().sendPacket(mp3);
                        break;
                    case "tphop":
                        PlayerMoveC2SPacket.PositionAndOnGround mp4 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y + 0.02,
                                pos.z, false);
                        PlayerMoveC2SPacket.PositionAndOnGround mp5 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y + 0.01,
                                pos.z, false);
                        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(mp4);
                        Cornos.minecraft.getNetworkHandler().sendPacket(mp5);
                        break;
                }
            }
        });
    }
}
