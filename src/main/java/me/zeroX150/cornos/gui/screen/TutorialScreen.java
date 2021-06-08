/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: TutorialScreen
# Created by constantin at 18:04, Mär 31 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.features.module.impl.external.ClientConfig;
import me.zeroX150.cornos.features.module.impl.misc.ClientProgression;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TutorialScreen extends Screen {
    CustomButtonWidget continueBTN;
    CheckboxWidget homescreen;
    CheckboxWidget mpscreen;
    CheckboxWidget customPIcon;
    CheckboxWidget doParticles;
    int page = 0;
    boolean finished = false;

    public TutorialScreen() {
        super(Text.of("Cornos tutorial"));
    }

    @Override
    protected void init() {
        super.init();
        homescreen = new CheckboxWidget(4, 65, 100, 20, Text.of("Client home screen"), true);
        mpscreen = new CheckboxWidget(4, 90, 100, 20, Text.of("Client multiplayer screen"), false);
        customPIcon = new CheckboxWidget(4, 115, 100, 20, Text.of("Custom process icon"), true);
        doParticles = new CheckboxWidget(4, 115 + 25, 100, 20, Text.of("Particles on home screen"), true);
        continueBTN = new CustomButtonWidget(width - 81, height - 21, 80, 20, Text.of("Next"), () -> {
            if (finished) {
                ClientConfig m = Cornos.config;
                m.mconf.getByName("homescreen").setValue(homescreen.isChecked() ? "client" : "vanilla");
                m.mconf.getByName("mpscreen").setValue(mpscreen.isChecked() ? "client" : "vanilla");
                m.mconf.getByName("customProcessIcon").setValue(customPIcon.isChecked() ? "on" : "off");
                m.particles.setValue(doParticles.isChecked() ? "on" : "off");
                ClientProgression.hasFinishedTut.setValue("on");
                assert this.client != null;
                this.client.openScreen(null);
            } else
                page++;
        });
        this.addDrawableChild(continueBTN);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Cornos.minecraft.options.guiScale = 2;
        renderBackground(matrices);
        matrices.push();
        matrices.scale(2, 2, 1);
        this.textRenderer.draw(matrices, "Welcome to Cornos!", 2, 2, 0xFFFFFFFF);
        matrices.scale(.5f, .5f, 1);
        this.textRenderer.draw(matrices, "Here's how to use the mod.", 5, 24, 0xFFFFFFFF);
        matrices.pop();
        List<String> messages = new ArrayList<>();
        switch (page) {
            case 0:
                messages.add("Firstly, this is a client aimed at creative and survival servers.");
                messages.add("Anarchy servers will be hard to dominate with this.");
                messages.add("If you are a big nerdy creative exploit fan, this client is for you.");
                messages.add(
                        "Of course, there are some good modules for survival, so you basically have a good choice for both");
                messages.add("That being said, click the §aNext§r button to continue");
                break;
            case 1:
                messages.add(
                        "Core features include funny items you can generate, a hologram generator and raiding utils.");
                messages.add("");
                messages.add("The hologram generator");
                messages.add(
                        "To generate a hologram, you can use the §ahologram§r command or use the §aFunny Items§r menu in the top left in the creative inventory");
                messages.add("");
                messages.add("The hologram aura");
                messages.add(
                        "The hologram aura module is basically like the hologram generator but on crack. Here's how to use it properly");
                messages.add("1. Go to the module's settings, configure it properly and turn off autoplace");
                messages.add("2. Go to the §afunny items§r menu");
                messages.add("3. Enter in the hologram's content you'd like to display");
                messages.add("4. Click the §aEnable§r button");
                messages.add("5. Go to somewhere you can place stuff on, and enable autoplace or place them manually");
                break;
            case 2:
                messages.add("Client GUI's and how you can disable them");
                messages.add("The client adds a few custom GUIs, like the multiplayer menu or the home screen.");
                messages.add(
                        "You can disable these and return to the default by going into §aClientConfig§r's module settings");
                messages.add(
                        "The module is in the §aMisc§r category. You can press the §aClick GUI§r button on the main menu to get to the settings");
                break;
            case 3:
                messages.add("Let's get to configuring, shall we?");
                homescreen.render(matrices, mouseX, mouseY, delta);
                mpscreen.render(matrices, mouseX, mouseY, delta);
                customPIcon.render(matrices, mouseX, mouseY, delta);
                doParticles.render(matrices, mouseX, mouseY, delta);
                break;
            default:
                messages.add("Congrats! You are ready to use the client now");
                messages.add("To revisit this tutorial and change settings, run §a"
                        + Cornos.config.mconf.getByName("prefix").value
                        + "config clientprogression finishedTutorial off§r in game.");
                continueBTN.setMessage(Text.of("Finish"));
                finished = true;
                break;
        }
        int offset = 50;
        for (String message : messages) {
            textRenderer.draw(matrices, message, 5, offset, 0xFFFFFFFF);
            offset += 10;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        homescreen.mouseClicked(mouseX, mouseY, button);
        mpscreen.mouseClicked(mouseX, mouseY, button);
        customPIcon.mouseClicked(mouseX, mouseY, button);
        doParticles.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        homescreen.mouseReleased(mouseX, mouseY, button);
        mpscreen.mouseReleased(mouseX, mouseY, button);
        customPIcon.mouseReleased(mouseX, mouseY, button);
        doParticles.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        homescreen.mouseMoved(mouseX, mouseY);
        mpscreen.mouseMoved(mouseX, mouseY);
        customPIcon.mouseMoved(mouseX, mouseY);
        doParticles.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }
}
