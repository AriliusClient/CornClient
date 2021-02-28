package me.constantindev.ccl.module.ext;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import net.minecraft.client.MinecraftClient;

public class FullBright extends Module {

    private double oldgamma;

    public FullBright() {
        super("FullBright", "Light up your world.", MType.MISC);
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onEnable() {
        oldgamma = mc.options.gamma;
        mc.options.gamma = 10D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.options.gamma = oldgamma;
        super.onDisable();
    }
}