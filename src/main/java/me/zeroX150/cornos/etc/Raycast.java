/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: Raycast
# Created by constantin at 09:02, Mär 17 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.etc;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.render.RenderableLine;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Raycast {
    Vec3d s;
    Vec3d e;
    Vec3d diff;
    double dist;

    public Raycast(Vec3d start, Vec3d end) {
        s = start;
        e = end;
        diff = e.subtract(s);
        dist = s.distanceTo(e);
    }

    public boolean passesThroughBlock(int laziness) {
        Vec3d current = s;
        double lastDist = Double.MAX_VALUE;
        boolean passes = false;
        double perX = diff.x / 100 * laziness;
        double perY = diff.y / 100 * laziness;
        double perZ = diff.z / 100 * laziness;
        while (lastDist > current.distanceTo(e)) {
            lastDist = current.distanceTo(e);
            current = current.add(perX, perY, perZ);
            BlockPos bp = new BlockPos(current.x, current.y, current.z);
            assert Cornos.minecraft.world != null;
            if (Cornos.minecraft.world.getBlockState(bp).getBlock() != (Blocks.AIR)) {
                passes = true;
                break;
            }
        }
        return passes;
    }
}