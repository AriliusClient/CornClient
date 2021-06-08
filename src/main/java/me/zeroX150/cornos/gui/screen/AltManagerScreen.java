/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: AltManagerScreen
# Created by constantin at 08:03, Mär 19 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.module.impl.misc.Alts;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import me.zeroX150.cornos.gui.widget.PasswordField;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AltManagerScreen extends Screen {
    TextFieldWidget email;
    PasswordField passwd;
    TextFieldWidget emailpasswd;
    CustomButtonWidget showpass;
    String errormsg = "";
    boolean hasSavedAlts = false;

    public AltManagerScreen() {
        super(Text.of("Cornos alt manager"));
    }

    @Override
    protected void init() {
        // do not question it, fabric api is fucking retarded
        super.init();

        email = new TextFieldWidget(textRenderer, width / 2 - (200 / 2), height / 2 - (20 / 2) - 35, 200, 20,
                Text.of("Email"));
        email.setMaxLength(1000);
        passwd = new PasswordField(textRenderer, width / 2 - (200 / 2), height / 2 - (20 / 2), 200, 20,
                Text.of("Password"));
        passwd.setMaxLength(1000);
        showpass = new CustomButtonWidget(width / 2 - (200 / 2) + 210, height / 2 - (20 / 2), 30, 20, Text.of("Show"),
                () -> {
                    passwd.setShowPassword(!passwd.isShowPassword());
                    if (passwd.isShowPassword()) {
                        showpass.setMessage(Text.of("Hide"));
                    } else {
                        showpass.setMessage(Text.of("Show"));
                    }
                });
        emailpasswd = new TextFieldWidget(textRenderer, width / 2 - (200 / 2), height / 2 - (20 / 2) + 35, 200, 20,
                Text.of("Username:password"));
        emailpasswd.setMaxLength(2000);
        CustomButtonWidget login = new CustomButtonWidget(width / 2 - (120 / 2), height / 2 - (20 / 2) + 60, 120, 20,
                Text.of("Login"), () -> {
            boolean success = STL.auth(this.email.getText(), this.passwd.getText());
            this.errormsg = success ? "§aLogged in!" : "§cFailed to log in. Check the password and email.";
        });
        CustomButtonWidget saveAlt = new CustomButtonWidget(width / 2 - (120 / 2), height / 2 - (20 / 2) + 85, 120, 20,
                Text.of("Save alt"), () -> {
            String email = this.email.getText();
            String passwd = this.passwd.getText();
            if (email.isEmpty()) {
                this.errormsg = "§cEmail cannot be empty";
                return;
            }
            if (passwd.isEmpty())
                passwd = "ThisDoesNotMatterSinceTheAccountIsCracked";
            Alts.k.setValue(
                    Alts.k.value + ((char) 999) + encStr(email, 6001) + ((char) 998) + encStr(passwd, 6000));
            assert this.client != null;
            this.client.openScreen(new AltManagerScreen());
        });
        this.addDrawableChild(login);
        this.addDrawableChild(saveAlt);
        this.addDrawableChild(showpass);

        List<List<String>> goodalts = new ArrayList<>();
        for (String s : Alts.k.value.substring(1).split(((char) 999) + "")) {
            String[] keypair = s.split(((char) 998) + "");
            if (keypair.length > 1) {
                System.out.println(s);
                List<String> bruh = new ArrayList<>();
                bruh.add(encStr(keypair[0], -6001));
                bruh.add(encStr(keypair[1], -6000));
                goodalts.add(bruh);
            }
        }
        List<String> fal = new ArrayList<>();
        int offset = 11;
        for (List<String> good : goodalts) {
            hasSavedAlts = true;
            fal.add(encStr(good.get(0), 6001) + ((char) 998) + encStr(good.get(1), 6000));
            CustomButtonWidget alt = new CustomButtonWidget(width - 141, offset, 140, 20, Text.of(good.get(0)), () -> {
                this.email.setText(good.get(0));
                if (!good.get(1).equalsIgnoreCase("ThisDoesNotMatterSinceTheAccountIsCracked"))
                    this.passwd.setText(good.get(1));
            });
            CustomButtonWidget delete = new CustomButtonWidget(width - 141 - 21, offset, 20, 20, Text.of("X"), () -> {
                fal.remove(encStr(good.get(0), 6001) + ((char) 998) + encStr(good.get(1), 6000));
                Alts.k.setValue("0" + String.join(((char) 999) + "", fal));
                assert this.client != null;
                this.client.openScreen(new AltManagerScreen());
            });
            offset += 25;
            this.addDrawableChild(alt);
            this.addDrawableChild(delete);
        }
        Alts.k.setValue("0" + String.join(((char) 999) + "", fal));
    }

    @Override
    public void tick() {
        String s = emailpasswd.getText();
        String[] keypair = s.split(":");
        if (keypair.length < 2)
            return;
        String email = keypair[0];
        String passwd = keypair[1];
        this.email.setText(email);
        this.passwd.setText(passwd);
        super.tick();
    }

    String encStr(String s2c, int off) {
        StringBuilder ret = new StringBuilder();
        for (char c : s2c.toCharArray()) {
            ret.append((char) ((int) c + off));
        }
        return ret.toString();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Email", width / 2, height / 2 - (20 / 2) - 45,
                0xFFFFFF);
        email.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Password", width / 2, height / 2 - (20 / 2) - 10,
                0xFFFFFF);
        passwd.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Or email:password", width / 2,
                height / 2 - (20 / 2) + 25, 0xFFFFFF);
        emailpasswd.render(matrices, mouseX, mouseY, delta);
        if (hasSavedAlts)
            DrawableHelper.drawCenteredText(matrices, textRenderer, "Saved alts", width - 81, 2, 0xFFFFFF);
        if (!errormsg.isEmpty()) {
            textRenderer.draw(matrices, errormsg, 1, 1, 0xFFFFFF);
            // DrawableHelper.drawCenteredText(matrices,textRenderer,errormsg,width/2,height/2+30,0xFFFFFF);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        email.mouseClicked(mouseX, mouseY, button);
        passwd.mouseClicked(mouseX, mouseY, button);
        emailpasswd.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        email.keyPressed(keyCode, scanCode, modifiers);
        passwd.keyPressed(keyCode, scanCode, modifiers);
        emailpasswd.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        email.keyReleased(keyCode, scanCode, modifiers);
        passwd.keyReleased(keyCode, scanCode, modifiers);
        emailpasswd.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        email.charTyped(chr, keyCode);
        passwd.charTyped(chr, keyCode);
        emailpasswd.charTyped(chr, keyCode);
        return false;
    }
}
