package me.constantindev.ccl.module.ext;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.ms.ModuleType;

public class XRAY extends Module {
    public XRAY() {
        super("XRAY", "When you just need some blocks", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        Cornos.minecraft.worldRenderer.reload();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Cornos.minecraft.worldRenderer.reload();
        super.onDisable();
    }
    // Logic: XrayHandler.java & AbstractBlockStateHook.java

}
