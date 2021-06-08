package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.etc.config.Colors;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XrayConfigScreen extends Screen {
    TextFieldWidget newBlock;
    List<Block> newBlocks = new ArrayList<>();

    public XrayConfigScreen() {
        super(Text.of("XrayConfigScreen"));
    }

    @Override
    protected void init() {
        super.init();
        newBlocks.clear();
        newBlocks.addAll(Arrays.asList(CConf.xrayBlocks));
        newBlock = new TextFieldWidget(textRenderer, 5, 5, 100, 20, Text.of(""));
        CustomButtonWidget add = new CustomButtonWidget(110, 5, 45, 20, Text.of("Add"), () -> {
            Block b = Registry.BLOCK.get(new Identifier(newBlock.getText()));
            if (b == Blocks.AIR) {
                newBlock.setEditableColor(Color.RED.getRGB());
            } else {
                newBlock.setEditableColor(14737632);
                newBlocks.add(b);
                CConf.xrayBlocks = newBlocks.toArray(new Block[0]);
                assert this.client != null;
                this.client.openScreen(new XrayConfigScreen());
            }
        });
        addDrawableChild(add);
        int offset = 40;
        int offsetX = 5;
        for (Block block : newBlocks.toArray(new Block[0])) {
            CustomButtonWidget delete = new CustomButtonWidget(offsetX, offset, 150, 20, block.getName(), () -> {
                newBlocks.remove(block);
                CConf.xrayBlocks = newBlocks.toArray(new Block[0]);
                assert this.client != null;
                this.client.openScreen(new XrayConfigScreen());
            });
            addDrawableChild(delete);
            offset += 25;
            if (offset + 20 > height) {
                offset = 40;
                offsetX += 155;
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Window w = Cornos.minecraft.getWindow();
        fill(matrices, 0, 0, w.getScaledWidth(), w.getScaledHeight(), Colors.GUIBACKGROUND.get().getRGB());
        newBlock.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        newBlock.tick();
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        newBlock.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        newBlock.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        newBlock.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        newBlock.charTyped(chr, keyCode);
        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        newBlock.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
