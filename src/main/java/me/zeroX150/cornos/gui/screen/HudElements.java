package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.Hud;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HudElements extends DrawableHelper {
    public static List<Double> tpsHistory = new ArrayList<>();
    public static List<Double> tpsAvgHistory = new ArrayList<>();
    public static long lastRecv = 0;
    public static String tps = "Calculating...";
    public static List<Double> minAvg = new ArrayList<>();
    private final DateFormat dateFormat = new SimpleDateFormat("h:mm aa");
    int tacoCounter = 0;
    int dababyPos = 0;
    double tick = 0;
    long last = System.currentTimeMillis();
    long dababyLast = System.currentTimeMillis();
    boolean dababyDirection = false;

    public void render(MatrixStack matrices, float delta) {
        Cornos.notifMan.render(matrices);
        int w = Cornos.minecraft.getWindow().getScaledWidth();
        int h = Cornos.minecraft.getWindow().getScaledHeight();
        tick += .2;
        if (tick > 360)
            tick = 0;
        double sin = Math.sin(Math.toRadians(tick));
        Hud hud = (Hud) ModuleRegistry.search(Hud.class);

        TextRenderer textRenderer = Cornos.minecraft.textRenderer;
        int offset = 0;
        if (((MConfToggleable) hud.mconf.getByName("miniplayer")).isEnabled()) {
            double yaw = sin * 100;
            assert Cornos.minecraft.player != null;
            InventoryScreen.drawEntity((int) (Cornos.minecraft.getWindow().getScaledWidth() / 1.5),
                    Cornos.minecraft.getWindow().getScaledHeight() - 1, 25, (float) (yaw),
                    Cornos.minecraft.player.getPitch(), Cornos.minecraft.player);
        }
        if (((MConfToggleable) hud.mconf.getByName("speedBPS")).isEnabled()) {
            double lrx = Cornos.minecraft.player.lastRenderX;
            double lry = Cornos.minecraft.player.lastRenderY;
            double lrz = Cornos.minecraft.player.lastRenderZ;
            double dist = Math.round(Cornos.minecraft.player.getPos().distanceTo(new Vec3d(lrx, lry, lrz)) * 200) / 10d;
            offset += 10;
            drawTextWithShadow(matrices, textRenderer, Text.of("Speed:"), 2,
                    Cornos.minecraft.getWindow().getScaledHeight() - offset, Hud.themeColor.getRGB());
            drawTextWithShadow(matrices, textRenderer, Text.of(" " + dist),
                    2 + textRenderer.getWidth(Text.of("Speed:")),
                    Cornos.minecraft.getWindow().getScaledHeight() - offset, Color.LIGHT_GRAY.getRGB());
        }
        if (((MConfToggleable) hud.mconf.getByName("ping")).isEnabled()) {
            if (Cornos.minecraft.getNetworkHandler() == null || Cornos.minecraft.player == null)
                return;
            PlayerListEntry ple = Cornos.minecraft.getNetworkHandler()
                    .getPlayerListEntry(Cornos.minecraft.player.getUuid());
            offset += 10;
            drawTextWithShadow(matrices, textRenderer, new LiteralText("Ping:"), 2,
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Hud.themeColor.getRGB());
            drawTextWithShadow(matrices, textRenderer,
                    new LiteralText(" " + (ple == null ? "?" : ple.getLatency()) + " ms"),
                    2 + textRenderer.getWidth(new LiteralText("Ping:")),
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Color.LIGHT_GRAY.getRGB());
        }
        if (((MConfToggleable) hud.mconf.getByName("coords")).isEnabled()) {
            offset += 9;
            drawTextWithShadow(matrices, textRenderer, new LiteralText("XYZ:"), 2,
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Hud.themeColor.getRGB());
            assert Cornos.minecraft.player != null;
            drawTextWithShadow(matrices, textRenderer,
                    new LiteralText(" " + Cornos.minecraft.player.getBlockPos().getX() + " "
                            + Cornos.minecraft.player.getBlockPos().getY() + " "
                            + Cornos.minecraft.player.getBlockPos().getZ()),
                    2 + textRenderer.getWidth(new LiteralText("XYZ:")),
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Color.LIGHT_GRAY.getRGB());
        }
        if (((MConfToggleable) hud.mconf.getByName("tps")).isEnabled()) {
            offset += 10;
            drawTextWithShadow(matrices, textRenderer, new LiteralText("TPS:"), 2,
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Hud.themeColor.getRGB());
            drawTextWithShadow(matrices, textRenderer, new LiteralText(" " + tps),
                    2 + textRenderer.getWidth(new LiteralText("TPS:")),
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Color.LIGHT_GRAY.getRGB());
        }
        if (((MConfToggleable) hud.mconf.getByName("fps")).isEnabled()) {
            offset += 10;
            drawTextWithShadow(matrices, textRenderer, new LiteralText("FPS:"), 2,
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Hud.themeColor.getRGB());
            drawTextWithShadow(matrices, textRenderer,
                    new LiteralText(" " + Cornos.minecraft.fpsDebugString.split(" ")[0]),
                    2 + textRenderer.getWidth(new LiteralText("FPS:")),
                    Cornos.minecraft.getWindow().getScaledHeight() - (offset), Color.LIGHT_GRAY.getRGB());
        }
        if (((MConfToggleable) hud.mconf.getByName("taco")).isEnabled()) {
            long current = System.currentTimeMillis();
            if (last + 300 < current) {
                last = current;
                tacoCounter++;
            }
            if (tacoCounter > 4)
                tacoCounter = 1;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(770, 771);
            Cornos.minecraft.getTextureManager().bindTexture(new Identifier("ccl", "taco/t" + tacoCounter + ".png"));
            DrawableHelper.drawTexture(matrices, (int) (w / 1.5 + 30), h - 64, 0, 0, 0, 92, 64, 64, 92);
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (((MConfToggleable) hud.mconf.getByName("dababycar")).isEnabled()) {
            long current = System.currentTimeMillis();
            if (dababyLast + 50 < current) {
                dababyLast = current;
                if (!dababyDirection) {
                    dababyPos++;
                } else {
                    dababyPos--;
                }
            }
            if (dababyPos >= 92 - 32) {
                dababyDirection = true;
            }
            if (dababyPos <= -92 + 32) {
                dababyDirection = false;
            }
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(770, 771);
            Cornos.minecraft.getTextureManager().bindTexture(new Identifier("ccl", "dababycar/dababycar.png"));
            assert Cornos.minecraft.player != null;
            DrawableHelper.drawTexture(matrices, (int) (w / 2.0 - 32 + dababyPos),
                    h - (Cornos.minecraft.player.isCreative() ? 70 : 90), 0, 0, 0, 64, 64, 64,
                    dababyDirection ? -64 : 64);
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (tpsHistory.size() > 92)
            tpsHistory.remove(0);
        if (tpsAvgHistory.size() > 92)
            tpsAvgHistory.remove(0);
        if (((MConfToggleable) hud.mconf.getByName("graph")).isEnabled()) {
            int yBase = h - 41;
            int xBase = (w / 2) - 91 - 2;
            double last = -1;
            drawTextWithShadow(matrices, textRenderer, Text.of("TPS"), xBase, yBase - 20 - 11,
                    new Color(0, 255, 217).getRGB());
            drawTextWithShadow(matrices, textRenderer, Text.of("Average TPS"),
                    (w / 2) + 91 - textRenderer.getWidth("Average TPS"), yBase - 20 - 11,
                    new Color(35, 255, 39).getRGB());
            for (Double d1 : tpsHistory.toArray(new Double[0])) {
                double d = d1 == null ? 20.0 : d1;
                int current = (int) Math.floor(d);
                if (last != -1) {
                    Renderer.renderLineScreen(new Vec3d(xBase, yBase - ((int) Math.floor(last)), 0),
                            new Vec3d(xBase + 2, yBase - current, 0), new Color(0, 255, 217), 2);
                }
                xBase += 2;
                last = d;
            }
            xBase = (w / 2) - 91 - 2;
            last = -1;
            for (Double d1 : tpsAvgHistory.toArray(new Double[0])) {
                double d = d1 == null ? 20.0 : d1;
                int current = (int) Math.floor(d);
                if (last != -1) {
                    Renderer.renderLineScreen(new Vec3d(xBase, yBase - ((int) Math.floor(last)), 0),
                            new Vec3d(xBase + 2, yBase - current, 0), new Color(35, 255, 39), 2);
                }
                xBase += 2;
                last = d;
            }
        }

        if (((MConfToggleable) hud.mconf.getByName("effects")).isEnabled()) {
            assert Cornos.minecraft.player != null;
            int i = 0;
            for (StatusEffectInstance statusEffectInstance : Cornos.minecraft.player.getStatusEffects()) {
                StatusEffectType statusEffectType = statusEffectInstance.getEffectType().getType();
                Formatting formatting;
                switch (statusEffectType) {
                    case HARMFUL:
                        formatting = Formatting.RED;
                        break;
                    case NEUTRAL:
                        formatting = Formatting.AQUA;
                        break;
                    case BENEFICIAL:
                        formatting = Formatting.GREEN;
                        break;
                    default:
                        formatting = Formatting.GRAY;
                        break;
                }
                drawTextWithShadow(matrices, textRenderer,
                        new LiteralText(StatusEffectUtil.durationToString(statusEffectInstance, 1)),
                        Cornos.minecraft.getWindow().getScaledWidth() - (5
                                + textRenderer.getWidth(StatusEffectUtil.durationToString(statusEffectInstance, 1))),
                        Cornos.minecraft.getWindow().getScaledHeight() - (9 + i), Color.LIGHT_GRAY.getRGB());
                drawTextWithShadow(matrices, textRenderer,
                        new LiteralText(statusEffectInstance.getEffectType().getName().getString() + " "
                                + (statusEffectInstance.getAmplifier() + 1) + " "),
                        Cornos.minecraft.getWindow().getScaledWidth()
                                - (5 + textRenderer.getWidth(statusEffectInstance.getEffectType().getName().getString()
                                + " " + (statusEffectInstance.getAmplifier() + 1) + " "
                                + StatusEffectUtil.durationToString(statusEffectInstance, 1))),
                        Cornos.minecraft.getWindow().getScaledHeight() - (10 + i),
                        Objects.requireNonNull(formatting.getColorValue()));
                i += 10;
            }
        }
        if (((MConfToggleable) hud.mconf.getByName("time")).isEnabled()) {
            assert Cornos.minecraft.player != null;
            drawTextWithShadow(matrices, textRenderer, new LiteralText(dateFormat.format(new Date())),
                    2 + textRenderer.getWidth("lollmao" + dateFormat.format(new Date())),
                    Cornos.minecraft.getWindow().getScaledHeight() - (9), Color.LIGHT_GRAY.getRGB());
        }

        if (lastRecv + 2000 < System.currentTimeMillis()) {
            int formatted;
            formatted = (int) Math.floor((System.currentTimeMillis() - lastRecv) / 1000f);

            DrawableHelper.drawCenteredText(matrices, textRenderer, "Server doesn't respond! (" + formatted + " s)",
                    w / 2, 40, new Color(255, 50, 50).getRGB());
        }
    }
}
