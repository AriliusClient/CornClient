package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", "Automatically picks the fastest tool out of your hotbar", MType.WORLD);
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute() {
        if (mc.options.keyAttack.isPressed()) {
            if (!(mc.crosshairTarget instanceof BlockHitResult)) return;
            BlockHitResult r = ((BlockHitResult) mc.crosshairTarget);
            BlockPos b = r.getBlockPos();
            assert MinecraftClient.getInstance().player != null;
            BlockState bstate = mc.player.world.getBlockState(b);
            PlayerInventory pinv = mc.player.inventory;
            float best = 1f;
            int bs1 = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack is = pinv.getStack(i);
                float s = is.getMiningSpeedMultiplier(bstate);
                if (s > best) bs1 = i;
            }
            if (bs1 != -1) {
                pinv.addPickBlock(pinv.getStack(bs1));
            }
        }
        super.onExecute();
    }
}
