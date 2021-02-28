package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.RenderableBlock;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.helper.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Objects;

public class MidAirPlace extends Module {
    int timeout = 20;

    public MidAirPlace() {
        super("MidAirPlace", "Allows you to place blocks midair", MType.WORLD);
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute() {
        HitResult hr = mc.crosshairTarget;
        if (!(hr instanceof BlockHitResult)) return;
        RenderHelper.addToQueue(new RenderableBlock(((BlockHitResult) hr).getBlockPos(), 255, 50, 50, 255));
        if (mc.options.keyUse.isPressed()) {
            timeout--;
            if (timeout != 19 && timeout > 0) return;
            PlayerInteractBlockC2SPacket p = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) hr);
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(p);
        } else timeout = 20;
        super.onExecute();
    }
}
