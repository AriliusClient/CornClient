/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: ScanningEntryMixin
# Created by constantin at 14:49, Mär 12 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.mixin.gui;

import me.zeroX150.cornos.Cornos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ScanningEntry.class)
public class ScanningEntryMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
                       int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (Cornos.config.mconf.getByName("mpscreen").value.equalsIgnoreCase("vanilla"))
            return;
        ci.cancel();
        int var10000 = y + entryHeight / 2;
        int i = var10000 - 9 / 2;
        DrawableHelper.drawCenteredText(matrices, this.client.textRenderer, new TranslatableText("lanServer.scanning"),
                320 / 2, i, 0xFFFFFF);
        // this.client.textRenderer.draw(matrices, new
        // TranslatableText("lanServer.scanning"),
        // (float)(this.client.currentScreen.width / 2 -
        // this.client.textRenderer.getWidth(new TranslatableText("lanServer.scanning"))
        // / 2), (float)i, 16777215);
        String loading;
        switch ((int) (Util.getMeasuringTimeMs() / 300L % 4L)) {
            case 0:
            default:
                loading = "O o o";
                break;
            case 1:
            case 3:
                loading = "o O o";
                break;
            case 2:
                loading = "o o O";
        }
        DrawableHelper.drawCenteredText(matrices, this.client.textRenderer, loading, 320 / 2, i + 9, 8421504);
    }
}
