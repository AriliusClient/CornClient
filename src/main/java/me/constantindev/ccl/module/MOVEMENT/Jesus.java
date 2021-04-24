package me.constantindev.ccl.module.MOVEMENT;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.MultiOption;
import me.constantindev.ccl.etc.ms.MType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Jesus extends Module {
    public static MultiOption mode = new MultiOption("mode", "jump", new String[]{"jump", "velocity", "vanilla", "dontfall", "solid", "herobrine", "herobrine2", "herobrine3"});

    public Jesus() {
        super("Jesus", "Yisus.", MType.MOVEMENT);
        this.mconf.add(mode);
    }

    @Override
    public void onExecute() {
        assert Cornos.minecraft.player != null;
        switch (mode.value) {
            case "herobrine2":
            case "jump":
                if (Cornos.minecraft.player.isWet()) {
                    Cornos.minecraft.player.jump();
                }
                break;
            case "herobrine":
            case "velocity":
                if (Cornos.minecraft.player.isWet()) {
                    Vec3d vel = Cornos.minecraft.player.getVelocity();
                    Cornos.minecraft.player.setVelocity(vel.x, 0.1, vel.z);
                }
                break;
            case "vanilla":
                if (Cornos.minecraft.player.isWet()) {
                    Cornos.minecraft.player.addVelocity(0, 0.05, 0);
                }

                break;
            case "dontfall":
                BlockPos bpos = Cornos.minecraft.player.getBlockPos();

                if (Cornos.minecraft.player.world.getBlockState(bpos.down()).getFluidState().getLevel() != 0) {
                    Vec3d v = Cornos.minecraft.player.getVelocity();
                    Cornos.minecraft.player.setVelocity(v.x, 0, v.z);
                }
                break;
        }
        super.onExecute();
    }
}
