package me.constantindev.ccl.module.ext;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import net.minecraft.client.MinecraftClient;

public class FullBright extends Module {

    private double oldgamma;

    public FullBright() {
        super("FullBright", "Light up your world.", MType.MISC);
    }

    @Override
    public void onEnable() {
        oldgamma = MinecraftClient.getInstance().options.gamma;
        MinecraftClient.getInstance().options.gamma = 10D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        MinecraftClient.getInstance().options.gamma = oldgamma;
        super.onDisable();
    }
}