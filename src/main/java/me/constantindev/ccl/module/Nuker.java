package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.Num;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class Nuker extends Module {
    int current = 0;

    public Nuker() {
        super("Nuker", "Stabs blocks like there is no tomorrow", MType.WORLD);
        this.mconf.add(new Num("range", 5, 10, 0));
    }

    @Override
    public void onExecute() {
        int range = (int) ((Num) this.mconf.getByName("range")).getValue();
        current++;
        if (current > 2) current = 0;
        else return;
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        BlockPos original = mc.player.getBlockPos();
        for (int x = -range; x < range + 1; x++) {
            for (int y = -range; y < range + 1; y++) {
                for (int z = -range; z < range + 1; z++) {
                    BlockPos bp2 = original.add(x, y, z);
                    if (bp2.equals(original.down())) continue;
                    BlockState bstate = mc.player.world.getBlockState(bp2);
                    if (bstate.getBlock().getName().asString().equalsIgnoreCase("air")) continue;
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, bp2, Direction.UP));
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, bp2, Direction.UP));

                }
            }
        }
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, original.down(), Direction.UP));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, original.down(), Direction.UP));
        super.onExecute();
    }
}
