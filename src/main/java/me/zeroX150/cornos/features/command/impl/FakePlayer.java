package me.zeroX150.cornos.features.command.impl;

import com.mojang.authlib.GameProfile;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.event.EventHelper;
import me.zeroX150.cornos.etc.event.EventType;
import me.zeroX150.cornos.etc.helper.Rnd;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePlayer extends Command {
    final List<OtherClientPlayerEntity> fakePlayers = new ArrayList<>();
    final List<Vec3d> trackedPositions = new ArrayList<>();

    public FakePlayer() {
        super("FakePlayer", "Creates a totally real functional player", new String[]{"cplayer"});
        EventHelper.BUS.registerEvent(EventType.ONTICK, event -> {
            assert Cornos.minecraft.player != null;
            if (!trackedPositions.contains(Cornos.minecraft.player.getPos()))
                trackedPositions.add(Cornos.minecraft.player.getPos());
            if (trackedPositions.size() > 30) {
                trackedPositions.subList(0, 1).clear();
            }
            Vec3d gotoPos = trackedPositions.get(0);
            List<OtherClientPlayerEntity> newPl = new ArrayList<>();
            for (OtherClientPlayerEntity p : fakePlayers) {
                if (!p.isRemoved())
                    newPl.add(p);
            }
            for (OtherClientPlayerEntity p : newPl) {
                p.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, Cornos.minecraft.player.getPos());
                p.updatePosition(gotoPos.x, gotoPos.y, gotoPos.z);
                // p.teleport(gotoPos.x,gotoPos.y,gotoPos.z);
                p.travel(new Vec3d(1, 0, 0).rotateY(MathHelper.wrapDegrees(p.getYaw())));
            }
            fakePlayers.clear();
            fakePlayers.addAll(newPl);
        });
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 2) {
            STL.notifyUser("ima need an username and uuid");
            return;
        }
        String u = args[0];
        UUID uuid;
        try {
            uuid = UUID.fromString(args[1]);
        } catch (Exception ignored) {
            STL.notifyUser("not sure if thats a valid uuid bruh");
            return;
        }
        assert Cornos.minecraft.world != null;
        OtherClientPlayerEntity o = new OtherClientPlayerEntity(Cornos.minecraft.world, new GameProfile(uuid, u));
        o.copyPositionAndRotation(Cornos.minecraft.player);
        Cornos.minecraft.world.addPlayer((int) Rnd.rndD(65535), o);
        fakePlayers.add(o);
        super.onExecute(args);
    }
}
