/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: Confuse
# Created by constantin at 08:37, Mär 17 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.features.module.impl.combat;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.Raycast;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.Random;

public class Confuse extends Module {
    MConfMultiOption mode = new MConfMultiOption("mode", "randomtp", new String[]{"randomtp", "switch", "circle"}, "The mode of the module");
    MConfNum delay = new MConfNum("delay", 3, 20, 0, "How much delay in ticks to wait between TPs");
    MConfNum circleSpeed = new MConfNum("circleSpeed", 10, 180, 1, "How much to increment the circle position by");
    MConfToggleable moveThroughBlocks = new MConfToggleable("moveThroughBlocks", false, "Whether or not to move through solid blocks");
    int delayWaited = 0;
    double circleProgress = 0;
    double addition = 0.0;
    Entity target;

    public Confuse() {
        super("Confuse", "Makes your enemies shit themselves", ModuleType.COMBAT);
        this.mconf.add(mode);
        this.mconf.add(delay);
        this.mconf.add(moveThroughBlocks);
        this.mconf.add(circleSpeed);
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        if (target != null) {
            if (Cornos.friendsManager.getFriends().containsKey(target.getEntityName())) {
                return;
            }
            Vec3d last = null;
            addition += 1.0;
            if (addition > 360)
                addition = 0;
            for (int i = 0; i < 360; i += 1) {
                Color c1;
                double rot = (255.0 * 3) * (((((double) i) + addition) % 360) / 360.0);
                int seed = (int) Math.floor(rot / 255.0);
                double current = rot % 255;
                double red = seed == 0 ? current : (seed == 1 ? Math.abs(current - 255) : 0);
                double green = seed == 1 ? current : (seed == 2 ? Math.abs(current - 255) : 0);
                double blue = seed == 2 ? current : (seed == 0 ? Math.abs(current - 255) : 0);
                c1 = new Color((int) red, (int) green, (int) blue);
                Vec3d tp = target.getPos();
                double rad = Math.toRadians(i);
                double sin = Math.sin(rad) * 3;
                double cos = Math.cos(rad) * 3;
                Vec3d c = new Vec3d(tp.x + sin, tp.y + target.getHeight() / 2, tp.z + cos);
                if (last != null)
                    Renderer.renderLine(last, c, c1, 3);
                last = c;
            }
        }
        super.onRender(ms, td);
    }

    @Override
    public void onExecute() {
        delayWaited++;
        if (delayWaited < delay.getValue())
            return;
        delayWaited = 0;
        assert Cornos.minecraft.player != null;
        Vec3d sel1 = Cornos.minecraft.player.getPos().add(-4, -4, -4);
        Vec3d sel2 = sel1.add(8, 8, 8);
        Box selector = new Box(sel1, sel2);
        assert Cornos.minecraft.world != null;
        for (Entity e : Cornos.minecraft.world.getEntities()) {
            if (e.getUuid() == Cornos.minecraft.player.getUuid())
                continue;
            if (!e.isAlive() || !e.isAttackable())
                continue;
            Identifier id = Registry.ENTITY_TYPE.getId(e.getType());
            if (id.getPath().equals("wither_skull"))
                continue;

            if (e.getBoundingBox().intersects(selector)) {
                target = e;
                break;
            }
        }
        if (!target.isAlive()) {
            target = null;
        }
        if (target == null)
            return;
        Vec3d entityPos = target.getPos();
        Vec3d playerPos = Cornos.minecraft.player.getPos();
        if (playerPos.distanceTo(entityPos) > 6) {
            target = null;
            return;
        }
        Random r = new Random();
        switch (mode.value) {
            case "randomtp":
                double x = r.nextDouble() * 6 - 3;
                double y = 0;
                double z = r.nextDouble() * 6 - 3;
                Vec3d addend = new Vec3d(x, y, z);
                Vec3d goal = entityPos.add(addend);
                if (Cornos.minecraft.world.getBlockState(new BlockPos(goal.x, goal.y, goal.z)).getBlock()
                        != (Blocks.AIR)) {
                    goal = new Vec3d(x, playerPos.y, z);
                }
                if (Cornos.minecraft.world.getBlockState(new BlockPos(goal.x, goal.y, goal.z)).getBlock()
                        == (Blocks.AIR)) {
                    Raycast rc = new Raycast(Cornos.minecraft.player.getPos(), goal);
                    if (!moveThroughBlocks.isEnabled() && rc.passesThroughBlock(1)) {
                        delayWaited = (int) (delay.getValue() - 1);
                        break;
                    }
                    Cornos.minecraft.player.updatePosition(goal.x, goal.y, goal.z);
                } else {
                    delayWaited = (int) (delay.getValue() - 1);
                }
                break;
            case "switch":
                Vec3d diff = entityPos.subtract(playerPos);
                Vec3d diff1 = new Vec3d(MathHelper.clamp(diff.x, -3, 3), MathHelper.clamp(diff.y, -3, 3),
                        MathHelper.clamp(diff.z, -3, 3));
                Vec3d goal2 = entityPos.add(diff1);
                Raycast rc1 = new Raycast(playerPos, goal2);
                if (!moveThroughBlocks.isEnabled() && rc1.passesThroughBlock(1)) {
                    delayWaited = (int) (delay.getValue() - 1);
                    break;
                }
                Cornos.minecraft.player.updatePosition(goal2.x, goal2.y, goal2.z);
                break;
            case "circle":
                delay.setValue("0");
                circleProgress += circleSpeed.getValue();
                if (circleProgress > 360)
                    circleProgress -= 360;
                double rad = Math.toRadians(circleProgress);
                double sin = Math.sin(rad) * 3;
                double cos = Math.cos(rad) * 3;
                Vec3d current = new Vec3d(entityPos.x + sin, playerPos.y, entityPos.z + cos);
                if (!moveThroughBlocks.isEnabled() && new Raycast(playerPos, current).passesThroughBlock(1))
                    break;
                Cornos.minecraft.player.updatePosition(current.x, current.y, current.z);
                break;
        }
        super.onExecute();
    }
}
