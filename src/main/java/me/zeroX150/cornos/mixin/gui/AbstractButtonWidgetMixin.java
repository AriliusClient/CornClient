package me.zeroX150.cornos.mixin.gui;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.etc.helper.Renderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ClickableWidget.class)
public abstract class AbstractButtonWidgetMixin {
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    public boolean active;
    @Shadow
    protected int width;
    @Shadow
    protected int height;
    protected int r;
    Color unselectedColor = new Color(25, 44, 49, 50);
    Color disabledColor = new Color(0, 0, 0, 70);
    long lastCache = 0;
    double timer = 0;

    @Shadow
    public abstract boolean isHovered();

    @Shadow
    public abstract Text getMessage();

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int dxStart, dyStart, dWidth, dHeight;

        if (((Object) this) instanceof ButtonWidget) {
            dxStart = x;
            dyStart = y;
            dWidth = width;
            dHeight = height;
        } else if (((Object) this) instanceof SliderWidget) {
            SliderWidget inst = (SliderWidget) ((Object) this);
            ISliderWidgetAccessor accessor = (ISliderWidgetAccessor) inst;
            double prog = accessor.getValue();
            dHeight = 20;
            dWidth = 4;
            dxStart = (int) (x + (prog * (width - 4))); // wtf why
            dyStart = y;
        } else return;
        ci.cancel();
        Color c = CConf.getRGB();
        long current = System.currentTimeMillis();
        if (current - lastCache > 3) {
            double cvalA = (double) (current - lastCache) / 400.0;
            double cval = this.isHovered() ? cvalA : -cvalA;
            timer += cval;
            lastCache = current;
        }
        timer = MathHelper.clamp(timer, 0, 1);
        timer = this.active ? timer : 0;
        if (timer != 0) {
            double a = easeInOutQuart(timer);
            Renderer.renderLineScreen(new Vec3d(dxStart, dyStart, 0), new Vec3d(dxStart, y + (a * dHeight), 0), c, 2);
            Renderer.renderLineScreen(new Vec3d(dxStart, dyStart + dHeight, 0), new Vec3d(dxStart + (a * dWidth), dyStart + dHeight, 0), c, 2);
            Renderer.renderLineScreen(new Vec3d(dxStart + dWidth, dyStart + dHeight, 0),
                    new Vec3d(dxStart + dWidth, dyStart + dHeight - (a * dHeight), 0), c, 2);
            Renderer.renderLineScreen(new Vec3d(dxStart + dWidth, dyStart, 0), new Vec3d(dxStart + dWidth - (a * dWidth), dyStart, 0), c, 2);
        }
        DrawableHelper.fill(matrices, x, y, x + width, y + height, this.active ? unselectedColor.getRGB() : disabledColor.getRGB());
        DrawableHelper.drawCenteredText(matrices, Cornos.minecraft.textRenderer, this.getMessage(), this.x + this.width / 2,
                this.y + (this.height - 8) / 2, Color.white.getRGB());
    }

    double easeInOutQuart(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

}
