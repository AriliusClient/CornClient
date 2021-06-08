/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: ArrowAvoid
# Created by constantin at 07:04, Mär 01 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

credits to bleachhack for providing most of the code here
*/
package me.zeroX150.cornos.features.module.impl.movement;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.render.RenderableLine;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class ArrowAvoid extends Module {
    RenderableLine rlL = null;

    public ArrowAvoid() {
        super("ArrowAvoid", "skeletons are fucking horrible", ModuleType.MOVEMENT);
        this.mconf.add(new MConfMultiOption("Type", "Packet", new String[]{"Client", "Packet"}, "Movement type"));
        this.mconf.add(new MConfNum("Speed", 1, 2, 0.1, "Speed to move with"));
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        if (rlL != null)
            this.rlq.add(rlL);
        super.onRender(ms, td);
    }

    @Override
    public void onExecute() {
        assert Cornos.minecraft.world != null;
        for (Entity e : Cornos.minecraft.world.getEntities()) {
            if (!(e instanceof ArrowEntity) || e.age > 50)
                continue;
            assert Cornos.minecraft.player != null;
            Box playerHB = Cornos.minecraft.player.getBoundingBox().expand(0.555);
            List<Box> bl = new ArrayList<>();
            Vec3d prevPos = null;
            for (int i = 0; i < 100; i++) {
                Vec3d nextPos = e.getPos().add(e.getVelocity().multiply(i / 5d));
                Box current = new Box(
                        nextPos.subtract(e.getBoundingBox().getXLength() / 2, 0, e.getBoundingBox().getZLength() / 2),
                        nextPos.add(e.getBoundingBox().getXLength() / 2, e.getBoundingBox().getYLength(),
                                e.getBoundingBox().getZLength() / 2));
                bl.add(current);
                boolean intc = playerHB.intersects(current);
                if (prevPos != null) {
                    rlL = new RenderableLine(prevPos, nextPos, intc ? 255 : 100, intc ? 50 : 255, 50, 255, 2);
                }
                prevPos = nextPos;

            }
            String mode = this.mconf.getByName("Type").value;
            double speed = ((MConfNum) this.mconf.getByName("Speed")).getValue();
            for (int i = 0; i < 75; i++) {
                Vec3d nextPos = e.getPos().add(e.getVelocity().multiply(i / 5d));
                Box nextBox = new Box(
                        nextPos.subtract(e.getBoundingBox().getXLength() / 2, 0, e.getBoundingBox().getZLength() / 2),
                        nextPos.add(e.getBoundingBox().getXLength() / 2, e.getBoundingBox().getYLength(),
                                e.getBoundingBox().getZLength() / 2));

                if (playerHB.intersects(nextBox)) {
                    for (Vec3d vel : new Vec3d[]{new Vec3d(1, 0, 0), new Vec3d(-1, 0, 0), new Vec3d(0, 0, 1),
                            new Vec3d(0, 0, -1)}) {
                        boolean contains = false;
                        for (Box b : bl)
                            if (b.intersects(playerHB.offset(vel.x, vel.y, vel.z)))
                                contains = true;
                        if (!contains) {
                            if (mode.equalsIgnoreCase("client")) {
                                Vec3d vel2 = vel.multiply(speed);
                                Cornos.minecraft.player.setVelocity(vel2.x, vel2.y, vel2.z);
                            } else if (mode.equalsIgnoreCase("packet")) {
                                Vec3d vel2 = Cornos.minecraft.player.getPos().add(vel.multiply(speed));
                                Cornos.minecraft.player.networkHandler.sendPacket(
                                        new PlayerMoveC2SPacket.PositionAndOnGround(vel2.x, vel2.y, vel2.z, false));
                                Cornos.minecraft.player.networkHandler.sendPacket(
                                        new PlayerMoveC2SPacket.PositionAndOnGround(vel2.x, vel2.y - 0.01, vel2.z, true));
                            }
                            return;
                        }
                    }

                    if (mode.equalsIgnoreCase("client")) {
                        Cornos.minecraft.player.setVelocity(0, 0, -speed);
                    } else if (mode.equalsIgnoreCase("packet")) {
                        Vec3d vel2 = Cornos.minecraft.player.getPos().add(new Vec3d(0, 0, -speed));
                        Cornos.minecraft.player.networkHandler
                                .sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(vel2.x, vel2.y, vel2.z, false));
                        Cornos.minecraft.player.networkHandler
                                .sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(vel2.x, vel2.y - 0.01, vel2.z, true));
                    }
                }
            }
        }
        super.onExecute();
    }
}
