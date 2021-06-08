package me.zeroX150.cornos.mixin.gui;

import com.google.common.collect.Lists;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.BetterHotbar;
import me.zeroX150.cornos.features.module.impl.external.Hud;
import me.zeroX150.cornos.features.module.impl.external.NoRender;
import me.zeroX150.cornos.features.module.impl.external.TabGUI;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    private int scaledWidth;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Hud hud = (Hud) ModuleRegistry.search(Hud.class);
        double rgbSpeed = ((MConfNum) ModuleRegistry.search(Hud.class).mconf.getByName("rgbSpeed")).getValue();
        double majorBruhMoment = Math.abs(rgbSpeed - 21) * 250;
        CConf.latestRGBVal = Hud.themeColor.isRainbow() ? Color.HSBtoRGB((float) ((System.currentTimeMillis() % majorBruhMoment) / majorBruhMoment), 0.6f, 1) : Hud.themeColor.getRGB();

        if (ModuleRegistry.search(Hud.class).isEnabled()) {
            if (((MConfToggleable) hud.mconf.getByName("modules")).isEnabled()) {
                boolean doRgb = Hud.themeColor.isRainbow();
                int offset = -11;
                List<Module> ml = ModuleRegistry.getAll();
                List<Module> mlR = new ArrayList<>();
                for (Module module : ml) {
                    if (module.isEnabled() && (((MConfToggleable) module.mconf.getByName("visible")).isEnabled()))
                        mlR.add(module);
                }
                mlR.sort(Comparator.comparingInt(o -> Cornos.minecraft.textRenderer.getWidth(o.name + (o.getContext().isEmpty() ? "" : " " + o.getContext()))));
                List<Module> mlR1 = Lists.reverse(mlR);
                double hsvVal = (System.currentTimeMillis() % majorBruhMoment) / majorBruhMoment;
                double hsvInc = Hud.modulesRgbScale.getValue();
                for (Module module : mlR1) {
                    int colorToUse = doRgb ? Color.HSBtoRGB((float) hsvVal, 0.6f, 1) : Hud.themeColor.getRGB();
                    hsvVal += hsvInc;
                    hsvVal %= 1.0;
                    offset += 11;
                    String s = module.name;
                    String ctx = module.getContext();
                    if (!ctx.isEmpty()) s += " §7" + ctx;
                    DrawableHelper.fill(matrices,
                            scaledWidth - Cornos.minecraft.textRenderer.getWidth(s) - 2 - 3, offset,
                            scaledWidth - 2, offset + 11, new Color(47, 47, 47, 90).getRGB());
                    DrawableHelper.fill(matrices, scaledWidth - 2, offset, scaledWidth, offset + 11,
                            doRgb ? colorToUse : Hud.themeColor.getRGB());
                    Cornos.minecraft.textRenderer.draw(matrices, module.name,
                            scaledWidth - Cornos.minecraft.textRenderer.getWidth(s) - 3, 2 + offset,
                            doRgb ? colorToUse : Hud.themeColor.getRGB());
                    if (!ctx.isEmpty()) {
                        Color use = new Color(doRgb ? colorToUse : Hud.themeColor.getRGB());
                        int rInv = Math.abs(255 - use.getRed());
                        int gInv = Math.abs(255 - use.getGreen());
                        int bInv = Math.abs(255 - use.getBlue());
                        Cornos.minecraft.textRenderer.draw(matrices, ctx, scaledWidth - Cornos.minecraft.textRenderer.getWidth(ctx) - 3, 2 + offset, new Color(rInv, gInv, bInv).getRGB());
                    }
                }
            }
            try {
                CConf.hudElements.render(matrices, tickDelta);
            } catch (Exception ignored) {
            }
        }
        for (Module m : ModuleRegistry.getAll()) {
            if (m.isEnabled())
                m.onHudRender(matrices, tickDelta);
        }
        if (ModuleRegistry.search(TabGUI.class).isEnabled() && CConf.tabGUI != null) {
            CConf.tabGUI.render(matrices, tickDelta);
        }
    }

    @Inject(at = {@At("HEAD")}, method = "renderStatusEffectOverlay", cancellable = true)
    private void renderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
        if (ModuleRegistry.search(Hud.class).isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    public void renderPumpkinOverlayReplacement(Identifier texture, float opacity, CallbackInfo ci) {
        if (NoRender.pumpkin.isEnabled() && ModuleRegistry.search(NoRender.class).isEnabled() && texture.getPath().contains("pumpkin")) {
            ci.cancel();
            MatrixStack defaultM = new MatrixStack();
            int w2d = Cornos.minecraft.getWindow().getScaledWidth() / 2;
            Cornos.minecraft.textRenderer.draw(defaultM, "You are wearing a pumpkin",
                    w2d - ((float) Cornos.minecraft.textRenderer.getWidth("You are wearing a pumpkin") / 2), 50,
                    0xFFFFFFFF);
        }
    }

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    public void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (ModuleRegistry.search(BetterHotbar.class).isEnabled())
            BetterHotbar.renderHotbar();
    }
}
