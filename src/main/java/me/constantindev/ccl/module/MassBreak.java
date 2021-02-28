package me.constantindev.ccl.module;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.Num;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.Level;

import java.util.Objects;

public class MassBreak extends Module {
    public MassBreak() {
        super("MassBreak", "Breaks a lot if you break a block", MType.WORLD);
        this.mconf.add(new Num("radius", 3.0, 10, 0));
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute() {
        int rad = (int) ((Num) this.mconf.getByName("radius")).getValue();
        BlockPos latest;
        try {
            assert mc.crosshairTarget != null;
            latest = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        } catch (Exception ignored) {
            return;
        }
        if (mc.options.keyAttack.isPressed()) {
            Cornos.log(Level.INFO, "bruh");
            for (int x = 0; x < rad; x++) {
                for (int y = 0; y < rad; y++) {
                    for (int z = 0; z < rad; z++) {
                        BlockPos c = latest.add(x - (rad / 2), y - (rad / 2), z - (rad / 2));
                        if (c.equals(latest)) continue;
                        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, c, Direction.UP));
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, c, Direction.UP));
                    }
                }
            }
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, latest, Direction.UP));
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, latest, Direction.UP));
        }
        super.onExecute();
    }
}
