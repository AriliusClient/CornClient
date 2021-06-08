/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: FunnyItemsScreen
# Created by constantin at 12:35, Mär 19 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.gui.screen;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.ItemExploits;
import me.zeroX150.cornos.features.command.impl.Hologram;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.exploit.HologramAura;
import me.zeroX150.cornos.features.module.impl.external.Hud;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class FunnyItemsScreen extends Screen {
    TextFieldWidget armorStandName;
    TextFieldWidget pos;
    TextFieldWidget standAuraName;
    TextFieldWidget auraText;
    CheckboxWidget armorStandSpawnegg;

    public FunnyItemsScreen() {
        super(Text.of("Funny items"));
    }

    double roundToDecimalPoint(double inp, int point) {
        double divident = Math.pow(10, point);
        return Math.round(inp * divident) / divident;
    }

    @Override
    protected void init() {
        super.init();
        assert Cornos.minecraft.player != null;
        Vec3d ppos = Cornos.minecraft.player.getPos();
        armorStandName = new TextFieldWidget(textRenderer, 5, 20, 200, 20, Text.of("Armor stand name"));
        armorStandName.setMaxLength(65535);
        pos = new TextFieldWidget(textRenderer, 5, 45, 120, 20, Text.of("Position"));
        CustomButtonWidget currentPos = new CustomButtonWidget(130, 45, 75, 20, Text.of("Current pos"),
                () -> pos.setText(roundToDecimalPoint(ppos.x, 2) + " " + roundToDecimalPoint(ppos.y, 2) + " "
                        + roundToDecimalPoint(ppos.z, 2)));
        pos.setMaxLength(65535);
        pos.setText(roundToDecimalPoint(ppos.x, 2) + " " + roundToDecimalPoint(ppos.y, 2) + " "
                + roundToDecimalPoint(ppos.z, 2));
        armorStandSpawnegg = new CheckboxWidget(5, 70, 60, 20, Text.of("Create spawn egg"), false);
        CustomButtonWidget spawnStand = new CustomButtonWidget(5, 95, 200, 20, Text.of("Spawn hologram"), () -> {
            double x, y, z;
            String[] coords = this.pos.getText().split(" ");
            boolean isValid = coords.length == 3;
            try {
                for (String s : coords) {
                    Double.parseDouble(s);
                }
            } catch (Exception ignored) {
                isValid = false;
            }
            if (!isValid) {
                this.pos.setEditableColor(new Color(255, 50, 50).getRGB());
                return;
            } else
                this.pos.setEditableColor(new Color(255, 255, 255).getRGB());

            x = Double.parseDouble(coords[0]);
            y = Double.parseDouble(coords[1]);
            z = Double.parseDouble(coords[2]);
            Hologram.spawnHologram(new Vec3d(x, y, z), armorStandName.getText(), armorStandSpawnegg.isChecked(), false,
                    "armor_stand");
        });
        standAuraName = new TextFieldWidget(textRenderer, 5, 130, 200, 20, Text.of("Sus"));
        standAuraName.setMaxLength(65535);
        auraText = new TextFieldWidget(textRenderer, 5, 165, 200, 20,
                Text.of("Your Text that hologramAura will build"));
        auraText.setMaxLength(65535);
        CustomButtonWidget enableStandAura = new CustomButtonWidget(5, 190, 200, 20, Text.of("Enable stand aura"),
                () -> {
                    if (standAuraName.getText().isEmpty())
                        return;
                    HologramAura.message = standAuraName.getText();
                    HologramAura.argument = auraText.getText();
                    // System.out.println(HologramAura.message2build);
                    ModuleRegistry.search(HologramAura.class).setEnabled(true);
                });

        CustomButtonWidget crashFireball = new CustomButtonWidget(210, 20, 200, 20, Text.of("Server crash fireball"),
                () -> Cornos.minecraft.player.getInventory().addPickBlock(ItemExploits.SERVER_CRASH_FIREBALL.get()));
        CustomButtonWidget blockban = new CustomButtonWidget(210, 45, 200, 20, Text.of("Blockban crash"),
                () -> Cornos.minecraft.player.getInventory().addPickBlock(ItemExploits.BLOCKBAN.get()));
        CustomButtonWidget lagmorstand = new CustomButtonWidget(210, 70, 200, 20, Text.of("32k Lagmor stand"),
                () -> Cornos.minecraft.player.getInventory().addPickBlock(ItemExploits.LAGMOR_STAND.get()));
        CustomButtonWidget cKitKickStand = new CustomButtonWidget(415, 20, 200, 20, Text.of("Kick stand"),
                () -> Cornos.minecraft.player.getInventory().addPickBlock(ItemExploits.KICK_STAND.get()));
        CustomButtonWidget cTrollHelmet = new CustomButtonWidget(415, 45, 200, 20, Text.of("Troll helmet"),
                () -> Cornos.minecraft.player.getInventory().addPickBlock(ItemExploits.NO_MOVE_HELMET.get()));
        CustomButtonWidget cNoEntityKill = new CustomButtonWidget(415, 70, 200, 20, Text.of("No Entity Kill"),
                () -> Cornos.minecraft.player.getInventory().addPickBlock(ItemExploits.NO_ENTITY_KILL.get()));
        this.addDrawableChild(currentPos);
        this.addDrawableChild(spawnStand);
        this.addDrawableChild(crashFireball);
        this.addDrawableChild(enableStandAura);
        this.addDrawableChild(blockban);
        this.addDrawableChild(lagmorstand);
        this.addDrawableChild(cKitKickStand);
        this.addDrawableChild(cTrollHelmet);
        this.addDrawableChild(cNoEntityKill);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(0, 0, 0, 175).getRGB());
        // holo generator
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Hologram", 105, 5, 0xFFFFFF);
        armorStandName.render(matrices, mouseX, mouseY, delta);
        armorStandSpawnegg.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Hologram aura", 105, 120, 0xFFFFFF);
        standAuraName.render(matrices, mouseX, mouseY, delta);
        pos.render(matrices, mouseX, mouseY, delta);
        auraText.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Hologram arg (Text / URL)", 105, 155, 0xFFFFFF);
        // item presets
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Funny items²", 310, 5, 0xFFFFFF);
        DrawableHelper.drawCenteredText(matrices, textRenderer, "Cornos raid kit", 515, 5, 0xFFFFFF);

        textRenderer.draw(matrices, "Remember to open your inventory before using these", 1, height - 10,
                Hud.themeColor.getRGB());
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        armorStandName.charTyped(chr, keyCode);
        pos.charTyped(chr, keyCode);
        standAuraName.charTyped(chr, keyCode);
        auraText.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        armorStandName.keyReleased(keyCode, scanCode, modifiers);
        pos.keyReleased(keyCode, scanCode, modifiers);
        standAuraName.keyReleased(keyCode, scanCode, modifiers);
        auraText.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        armorStandName.keyPressed(keyCode, scanCode, modifiers);
        pos.keyPressed(keyCode, scanCode, modifiers);
        standAuraName.keyPressed(keyCode, scanCode, modifiers);
        auraText.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        armorStandSpawnegg.mouseClicked(mouseX, mouseY, button);
        armorStandName.mouseClicked(mouseX, mouseY, button);
        pos.mouseClicked(mouseX, mouseY, button);
        standAuraName.mouseClicked(mouseX, mouseY, button);
        auraText.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
