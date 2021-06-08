package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.Colors;
import me.zeroX150.cornos.features.module.impl.world.AutoSign;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class AutoSignEditor extends Screen {
    TextFieldWidget line1;
    TextFieldWidget line2;
    TextFieldWidget line3;
    TextFieldWidget line4;

    public AutoSignEditor() {
        super(Text.of("Autosign editor"));
    }

    @Override
    protected void init() {
        super.init();
        line1 = new TextFieldWidget(textRenderer, 6, 20, 100, 20, Text.of(""));
        line1.setMaxLength(15);
        line2 = new TextFieldWidget(textRenderer, 6, 45, 100, 20, Text.of(""));
        line2.setMaxLength(15);
        line3 = new TextFieldWidget(textRenderer, 6, 70, 100, 20, Text.of(""));
        line3.setMaxLength(15);
        line4 = new TextFieldWidget(textRenderer, 6, 95, 100, 20, Text.of(""));
        line4.setMaxLength(15);
        CustomButtonWidget save = new CustomButtonWidget(5, 120, 100, 20, Text.of("Save"), () -> {
            AutoSign.line1 = line1.getText();
            AutoSign.line2 = line2.getText();
            AutoSign.line3 = line3.getText();
            AutoSign.line4 = line4.getText();
        });
        this.addDrawableChild(save);
    }

    @Override
    public void tick() {
        line1.tick();
        line2.tick();
        line3.tick();
        line4.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Window w = Cornos.minecraft.getWindow();
        fill(matrices, 0, 0, w.getScaledWidth(), w.getScaledHeight(), Colors.GUIBACKGROUND.get().getRGB());
        textRenderer.draw(matrices, "Custom text editor for AutoSign", 5, 5, Color.WHITE.getRGB());
        if (line1.getText().isEmpty())
            line1.setSuggestion("Line 1");
        else
            line1.setSuggestion("");
        if (line2.getText().isEmpty())
            line2.setSuggestion("Line 2");
        else
            line2.setSuggestion("");
        if (line3.getText().isEmpty())
            line3.setSuggestion("Line 3");
        else
            line3.setSuggestion("");
        if (line4.getText().isEmpty())
            line4.setSuggestion("Line 4");
        else
            line4.setSuggestion("");
        line1.render(matrices, mouseX, mouseY, delta);
        line2.render(matrices, mouseX, mouseY, delta);
        line3.render(matrices, mouseX, mouseY, delta);
        line4.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        line1.mouseClicked(mouseX, mouseY, button);
        line2.mouseClicked(mouseX, mouseY, button);
        line3.mouseClicked(mouseX, mouseY, button);
        line4.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        line1.keyPressed(keyCode, scanCode, modifiers);
        line2.keyPressed(keyCode, scanCode, modifiers);
        line3.keyPressed(keyCode, scanCode, modifiers);
        line4.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        line1.keyReleased(keyCode, scanCode, modifiers);
        line2.keyReleased(keyCode, scanCode, modifiers);
        line3.keyReleased(keyCode, scanCode, modifiers);
        line4.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        line1.charTyped(chr, keyCode);
        line2.charTyped(chr, keyCode);
        line3.charTyped(chr, keyCode);
        line4.charTyped(chr, keyCode);
        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        line1.mouseReleased(mouseX, mouseY, button);
        line2.mouseReleased(mouseX, mouseY, button);
        line3.mouseReleased(mouseX, mouseY, button);
        line4.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
