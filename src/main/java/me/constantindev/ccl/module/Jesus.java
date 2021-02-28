package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.MultiOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", "Yisus.", MType.MOVEMENT);
        this.mconf.add(new MultiOption("mode", "jump", new String[]{"jump", "velocity", "vanilla", "dontfall"}));
    }

    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute() {
        assert mc.player != null;
        switch (this.mconf.getByName("mode").value) {
            case "jump":
                if (mc.player.isWet()) {
                    mc.player.jump();
                }
                break;
            case "velocity":
                if (mc.player.isWet()) {
                    Vec3d vel = mc.player.getVelocity();
                    mc.player.setVelocity(vel.x, 0.1, vel.z);
                }
                break;
            case "vanilla":
                if (mc.player.isWet()) {
                    mc.player.addVelocity(0, 0.05, 0);
                }

                break;
            case "dontfall":
                BlockPos bpos = mc.player.getBlockPos();

                if (mc.player.world.getBlockState(bpos.down()).getFluidState().getLevel() != 0) {
                    Vec3d v = mc.player.getVelocity();
                    mc.player.setVelocity(v.x, 0, v.z);
                }
                break;
        }
        super.onExecute();
    }
}
