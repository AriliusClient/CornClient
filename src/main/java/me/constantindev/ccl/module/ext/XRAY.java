package me.constantindev.ccl.module.ext;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import net.minecraft.client.MinecraftClient;

public class XRAY extends Module {
    public XRAY() {
        super("xray", "When you just need some blocks", MType.WORLD);
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
        super.onDisable();
    }
    // Logic: XrayHandler.java & LuminanceHook.java

}
