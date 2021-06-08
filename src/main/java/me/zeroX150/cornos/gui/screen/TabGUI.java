package me.zeroX150.cornos.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.etc.manager.TabManager;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TabGUI extends DrawableHelper {

    private final Identifier rainbowLogo = new Identifier("ccl", "white_logo.png");

    public static void drawBorderedRect(int x, int y, int x1, int y1, float size, int borderC, int insideC,
                                        Matrix4f matrix4f) {
        drawGuiRect(x + size, y + size, x1 - size, y1 - size, insideC, matrix4f);
        drawGuiRect(x + size, y + size, x1, y, borderC, matrix4f);
        drawGuiRect(x, y + size, x + size, y1, borderC, matrix4f);
        drawGuiRect(x1, y1, x1 - size, y + size, borderC, matrix4f);
        drawGuiRect(x, y1 - size, x1, y1, borderC, matrix4f);
    }

    public static void drawGuiRect(float left, float top, float right, float bottom, int color, Matrix4f matrix4f) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager._enableBlend();
        GlStateManager._disableTexture();
        GlStateManager._blendFunc(770, 771);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix4f, left, bottom, 0.0F).color(f, f1, f2, f3).next();
        buffer.vertex(matrix4f, right, bottom, 0.0F).color(f, f1, f2, f3).next();
        buffer.vertex(matrix4f, right, top, 0.0F).color(f, f1, f2, f3).next();
        buffer.vertex(matrix4f, left, top, 0.0F).color(f, f1, f2, f3).next();
        buffer.end();
        BufferRenderer.draw(buffer);
        GlStateManager._enableTexture();
        GlStateManager._disableBlend();
    }

    public void render(MatrixStack matrices, float delta) {
        drawTabs(matrices, delta);
        GL11.glPushMatrix();
        Color color = new Color(CConf.latestRGBVal);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        Renderer.drawImage(matrices, rainbowLogo, 1, 1, 80, 18, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    private void drawTabs(MatrixStack matrices, float delta) {
        if (ModuleRegistry.getTabManager() != null) {
            TabManager tabManager = ModuleRegistry.getTabManager();
            int i = 0;
            int k = 0;
            for (ModuleType mType : tabManager.getTabType()) {
                drawGuiRect(5, 20 + i, 79, 30 + i, 0x90000000, matrices.peek().getModel());
                if (k == tabManager.getCurrentTab()) {
                    drawGuiRect(5, 20 + i, 79, 30 + i, CConf.latestRGBVal, matrices.peek().getModel());
                }
                Cornos.minecraft.textRenderer.drawWithShadow(matrices, mType.getN(), 7, 21 + i, 0xffffff, true);
                if (tabManager.getTabs().get(mType).isExpanded()) {
                    int j = 0;
                    int l = 0;
                    for (Module m : tabManager.getMods().get(mType)) {
                        int off = m.isEnabled() ? 2 : 0;
                        drawGuiRect(172 + off, 20 + j, 81 + off, 30 + j, new Color(35, 35, 35).getRGB(),
                                matrices.peek().getModel());
                        if (m.isEnabled()) {
                            drawGuiRect(174, 20 + j, 83, 30 + j, new Color(47, 47, 47, 134).getRGB(),
                                    matrices.peek().getModel());
                        }
                        if (l == tabManager.getCurrentMods().get(mType)) {
                            drawGuiRect(172 + off, 20 + j, 81 + off, 30 + j, CConf.latestRGBVal,
                                    matrices.peek().getModel());
                        }
                        Cornos.minecraft.textRenderer.drawWithShadow(matrices, m.name, 84 + off, 21 + j, 0xffffff,
                                true);
                        j += 10;
                        l++;
                    }
                }
                i += 10;
                k++;
            }
        }
    }
}
