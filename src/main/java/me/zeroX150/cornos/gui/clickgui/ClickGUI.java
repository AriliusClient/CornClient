package me.zeroX150.cornos.gui.clickgui;

import me.zeroX150.cornos.etc.config.MConf;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.ModuleType;
import me.zeroX150.cornos.gui.widget.CustomButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    List<CustomButtonWidget> customButtons = new ArrayList<>();
    ModuleType selected = null;

    public ClickGUI() {
        super(Text.of("amogus"));
    }

    @Override
    protected void init() {
        super.init();
        int offsetY = 1;
        for (ModuleType value : ModuleType.values()) {
            CustomButtonWidget cbt = new CustomButtonWidget(1, offsetY, 80, 20, Text.of(value.getN()), () -> {
                selected = value;
                handleModuleChange(value);
            });
            offsetY += 21;

            addDrawableChild(cbt);
        }
    }

    void handleModuleChange(ModuleType newType) {
        customButtons.clear();
        int xOffset = 82;
        int yOffset = 1;
        for (Module module : ModuleRegistry.getAll()) {
            if (module.type == newType) {
                if (yOffset + 20 > height) {
                    xOffset += 82;
                    yOffset = 1;
                }
                CustomButtonWidget w = new CustomButtonWidget(xOffset, yOffset, 100, 20, Text.of(module.name), () -> {
                    // module.setEnabled(!module.isEnabled());
                    handleModuleChange(module);
                }) {
                    @Override
                    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (!module.isEnabled()) {
                            this.unselectedColor = new Color(25, 44, 49, 50);
                        } else
                            this.unselectedColor = new Color(86, 113, 113, 50);
                        super.renderButton(matrices, mouseX, mouseY, delta);
                    }
                };
                customButtons.add(w);
                yOffset += 21;
            }
        }
    }

    void handleModuleChange(Module m) {
        handleModuleChange(selected);
        int xOffset = 184;
        int yOffset = 1;
        CustomButtonWidget enabled = new CustomButtonWidget(xOffset, yOffset, 80, 20, Text.of("Enabled"),
                () -> m.setEnabled(!m.isEnabled())) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                if (m.isEnabled())
                    this.unselectedColor = new Color(86, 113, 113, 50);
                else
                    this.unselectedColor = new Color(25, 44, 49, 50);
                super.renderButton(matrices, mouseX, mouseY, delta);
            }
        };
        customButtons.add(enabled);
        yOffset += 21;
        for (MConf.ConfigKey configKey : m.mconf.config) {
            if (configKey instanceof MConfToggleable) {
                if (yOffset + 20 > height) {
                    xOffset += 82;
                    yOffset = 1;
                }
                CustomButtonWidget btn = new CustomButtonWidget(xOffset, yOffset, 80, 20, Text.of(configKey.key),
                        ((MConfToggleable) configKey)::toggle) {
                    @Override
                    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (((MConfToggleable) configKey).isEnabled())
                            this.unselectedColor = new Color(86, 113, 113, 50);
                        else
                            this.unselectedColor = new Color(25, 44, 49, 50);
                        super.renderButton(matrices, mouseX, mouseY, delta);
                    }
                };
                customButtons.add(btn);
                yOffset += 21;
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        for (CustomButtonWidget customButtonWidget : customButtons.toArray(new CustomButtonWidget[0])) {
            customButtonWidget.render(matrices, mouseX, mouseY, delta);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (CustomButtonWidget customButtonWidget : customButtons.toArray(new CustomButtonWidget[0])) {
            customButtonWidget.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CustomButtonWidget customButtonWidget : customButtons.toArray(new CustomButtonWidget[0])) {
            customButtonWidget.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (CustomButtonWidget customButtonWidget : customButtons.toArray(new CustomButtonWidget[0])) {
            customButtonWidget.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
