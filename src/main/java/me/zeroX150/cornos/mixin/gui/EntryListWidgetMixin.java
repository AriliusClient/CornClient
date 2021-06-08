/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: EntryListWidgetMixin
# Created by constantin at 10:06, Mär 12 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.mixin.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class EntryListWidgetMixin {
    @Shadow
    private boolean renderBackground;

    @Shadow
    private boolean renderHeader;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight,
                     CallbackInfo ci) {
        this.renderBackground = false;
        this.renderHeader = false;
    }

    @Inject(method = "setRenderHeader", at = @At("HEAD"), cancellable = true)
    public void override(boolean renderHeader, int headerHeight, CallbackInfo ci) {
        this.renderBackground = false;
        this.renderHeader = false;
        ci.cancel();
    }

    @Inject(method = "setRenderBackground", at = @At("HEAD"), cancellable = true)
    public void override1(boolean bl, CallbackInfo ci) {
        this.renderBackground = false;
        this.renderHeader = false;
        ci.cancel();
    }
}
