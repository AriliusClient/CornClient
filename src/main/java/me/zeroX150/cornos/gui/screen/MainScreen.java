package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.etc.render.particles.LargeParticles;
import me.zeroX150.cornos.features.module.impl.misc.ClientProgression;
import me.zeroX150.cornos.gui.clickgui.ClickGUI;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MainScreen extends Screen {
    Identifier bg = new Identifier("ccl", "bg.jpg");
    LargeParticles p;
    boolean showSecrets = false;

    public MainScreen() {
        super(Text.of("bruh"));
    }

    @Override
    protected void init() {
        super.init();
        p = new LargeParticles(10);
        if (!ClientProgression.hasFinishedTut.isEnabled()) {
            assert this.client != null;
            this.client.openScreen(new TutorialScreen());
            return;
        }
        this.addDrawableChild(new CustomButtonWidget(width - 125, height - 150, 120, 20, Text.of("Alts"), () -> {
            assert this.client != null;
            this.client.openScreen(new AltManagerScreen());
        }));
        this.addDrawableChild(new CustomButtonWidget(width - 125, height - 125, 120, 20, Text.of("Settings"),
                () -> Cornos.minecraft.openScreen(new OptionsScreen(this, Cornos.minecraft.options))));
        this.addDrawableChild(new CustomButtonWidget(width - 125, height - 100, 120, 20, Text.of("Singleplayer"),
                () -> Cornos.minecraft.openScreen(new SelectWorldScreen(this))));
        this.addDrawableChild(new CustomButtonWidget(width - 125, height - 75, 120, 20, Text.of("Multiplayer"),
                () -> Cornos.minecraft.openScreen(new MultiplayerScreen(this))));
        CustomButtonWidget btnw = new CustomButtonWidget(width - 125, height - 50, 120, 20, Text.of("Realms"),
                () -> Cornos.minecraft.openScreen(new RealmsMainScreen(this)));
        this.addDrawableChild(btnw);
        this.addDrawableChild(new CustomButtonWidget(width - 125, height - 25, 120, 20, Text.of("Vanilla homescreen"), () -> {
            Cornos.config.mconf.getByName("homescreen").setValue("vanilla");
            Cornos.minecraft.openScreen(new TitleScreen());
        }));
        this.addDrawableChild(new TexturedButtonWidget(width - 20, 0, 20, 20, 0, 0, 0,
                new Identifier("ccl", "transparent.png"), (b) -> {
            /*
             * btnw.setMessage(Text.of("Roleplay")); showSecrets = true;
             */
            Cornos.minecraft.openScreen(new ClickGUI());
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Color c = CConf.getRGB();
        Cornos.minecraft.getTextureManager().bindTexture(bg);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, 0, width, height, height, width);
        p.render();
        Renderer.drawImage(matrices, new Identifier("ccl", "hscreenlogo.png"), -11, 1, 960 / 4, 233 / 4,
                (float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255);
        int diff = 32;
        GL11.glScaled(2, 2, 1);
        GL11.glScaled(.5, .5, 1);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        p.tick();
        super.tick();
    }
}
