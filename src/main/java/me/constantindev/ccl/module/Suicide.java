package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import net.minecraft.client.MinecraftClient;

public class Suicide extends Module {
    public Suicide() {
        super("Suicide", "You know what this does", MType.EXPLOIT);
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute() {
        new Thread(() -> {
            try {
                assert mc.player != null;
                mc.player.setVelocity(0, 5, 0);
                Thread.sleep(500);
                mc.player.setVelocity(0, -10, 0);
            } catch (Exception ignored) {
            }
        }).start();
        this.isOn.setState(false);
        super.onExecute();
    }
}
