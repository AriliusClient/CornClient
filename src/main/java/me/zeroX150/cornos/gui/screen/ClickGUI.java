package me.zeroX150.cornos.gui.screen;

import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.mc16.MinecraftGUI;
import com.lukflug.panelstudio.settings.*;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.Theme;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.*;
import me.zeroX150.cornos.etc.manager.KeybindManager;
import me.zeroX150.cornos.etc.render.particles.ConnectingParticles;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.ModuleType;
import me.zeroX150.cornos.features.module.impl.external.Hud;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickGUI extends MinecraftGUI {
    private final GUIInterface guiInterface;
    private final com.lukflug.panelstudio.ClickGUI gui;
    ConnectingParticles p;

    public ClickGUI() {
        p = new ConnectingParticles(100);
        guiInterface = new GUIInterface(true) {
            @Override
            protected String getResourcePrefix() {
                return "ccl:gui/";
            }

            @Override
            public void drawString(Point pos, String s, Color c) {
                end();
                Cornos.minecraft.textRenderer.draw(new MatrixStack(), " " + s, pos.x, pos.y, c.getRGB());
                begin();
            }

            @Override
            public int getFontWidth(String s) {
                return Cornos.minecraft.textRenderer.getWidth(s);
            }

            @Override
            public int getFontHeight() {
                return Cornos.minecraft.textRenderer.fontHeight;
            }

        };
        Theme theme = new com.lukflug.panelstudio.theme.GameSenseTheme(new ColorScheme() {
            @Override
            public Color getActiveColor() {
                return me.zeroX150.cornos.features.module.impl.external.ClickGUI.activeColor.getColor();
            }

            @Override
            public Color getInactiveColor() {
                return me.zeroX150.cornos.features.module.impl.external.ClickGUI.inactiveColor.getColor();
            }

            @Override
            public Color getBackgroundColor() {
                return me.zeroX150.cornos.features.module.impl.external.ClickGUI.backgroundColor.getColor();
            }

            @Override
            public Color getOutlineColor() {
                return Hud.themeColor.getColor();
            }

            @Override
            public Color getFontColor() {
                return Colors.TEXT.get();
            }

            @Override
            public int getOpacity() {
                return 255;
            }
        }, 8, 4, 0);
        gui = new com.lukflug.panelstudio.ClickGUI(guiInterface, context -> {
            int h = Cornos.minecraft.getWindow().getScaledHeight();
            int x = Cornos.minecraft.getWindow().getScaledWidth() / 2;
            DrawableHelper.drawCenteredText(new MatrixStack(), Cornos.minecraft.textRenderer, context.getDescription(), x, h - 15, 0xFFFFFFFF);
        });
        int offset = 10 - 114;
        int offsetY = 10;
        for (ModuleType type : ModuleType.values()) {
            if (type == ModuleType.HIDDEN)
                continue;
            int maxW = 96;
            for (Module m : ModuleRegistry.getAll()) {
                if (m.type != type)
                    continue;
                maxW = Math.max(maxW, Cornos.minecraft.textRenderer.getWidth(m.type.getN()));
            }
            offset += 114;
            if (offset > Cornos.minecraft.getWindow().getScaledWidth()) {
                offset = 10;
                offsetY += 114;
            }
            com.lukflug.panelstudio.DraggableContainer container = new DraggableContainer(type.getN(), null,
                    theme.getContainerRenderer(), new SimpleToggleable(false), new SettingsAnimation(me.zeroX150.cornos.features.module.impl.external.ClickGUI.getNumSet()),
                    null, new Point(offset, offsetY), maxW + 8) {
                @Override
                protected int getScrollHeight(int childHeight) {
                    int h = Cornos.minecraft.getWindow().getScaledHeight();
                    return Math.min(Math.min(h - 20, 400), childHeight);
                }
            };
            gui.addComponent(container);
            for (Module m : ModuleRegistry.getAll()) {
                if (m.type != type)
                    continue;
                maxW = Math.max(maxW, Cornos.minecraft.textRenderer.getWidth(m.name));
                CollapsibleContainer mc = new CollapsibleContainer(m.name, m.description, theme.getContainerRenderer(),
                        new SimpleToggleable(false), new SettingsAnimation(me.zeroX150.cornos.features.module.impl.external.ClickGUI.getNumSet()),
                        new SimpleToggleable(m.isEnabled()) {
                            @Override
                            public boolean isOn() {
                                return m.isEnabled();
                            }

                            @Override
                            public void toggle() {
                                m.setEnabled(!m.isEnabled());
                            }
                        });
                container.addComponent(mc);
                for (MConf.ConfigKey kc : m.mconf.config) {
                    maxW = Math.max(maxW, Cornos.minecraft.textRenderer.getWidth(kc.key + ": " + kc.value));
                    // it works.
                    // don't question it.
                    if (kc instanceof MConfToggleable) {
                        BooleanComponent bc = new BooleanComponent(kc.key, kc.description, theme.getComponentRenderer(),
                                new com.lukflug.panelstudio.settings.Toggleable() {
                                    @Override
                                    public void toggle() {
                                        ((MConfToggleable) kc).toggle();
                                    }

                                    @Override
                                    public boolean isOn() {
                                        return ((MConfToggleable) kc).isEnabled();
                                    }
                                });
                        mc.addComponent(bc);
                    } else if (kc instanceof MConfMultiOption) {
                        List<String> l = new ArrayList<>(Arrays.asList(((MConfMultiOption) kc).possibleValues));

                        EnumSetting es = new EnumSetting() {
                            int current = l.indexOf(kc.value);

                            @Override
                            public void increment() {
                                current++;
                                if (current > (((MConfMultiOption) kc).possibleValues.length - 1))
                                    current = 0;
                                kc.setValue(((MConfMultiOption) kc).possibleValues[current]);
                            }

                            @Override
                            public String getValueName() {
                                return ((MConfMultiOption) kc).possibleValues[current];
                            }
                        };
                        EnumComponent ec = new EnumComponent(kc.key, kc.description, theme.getComponentRenderer(), es);
                        mc.addComponent(ec);
                    } else if (kc instanceof MConfKeyBind) {
                        KeybindSetting ks = new KeybindSetting() {
                            @Override
                            public int getKey() {
                                return (int) ((MConfKeyBind) kc).getValue();
                            }

                            @Override
                            public void setKey(int key) {
                                if (key == 47 || key == 0)
                                    kc.setValue(-1 + "");
                                else
                                    kc.setValue(key + "");
                                KeybindManager.reload();
                            }

                            @Override
                            public String getKeyName() {
                                String ret;
                                if (this.getKey() < 0) {
                                    ret = "None";
                                } else {
                                    ret = GLFW.glfwGetKeyName(this.getKey(), this.getKey());
                                }
                                if (ret == null)
                                    ret = this.getKey() + "";
                                return ret;
                            }
                        };
                        KeybindComponent kc1 = new KeybindComponent(theme.getComponentRenderer(), ks);
                        mc.addComponent(kc1);
                    } else if (kc instanceof MConfNum) {
                        NumberSetting ns = new NumberSetting() {
                            @Override
                            public double getNumber() {

                                return ((MConfNum) kc).getValue();
                            }

                            @Override
                            public void setNumber(double value) {
                                kc.setValue(value + "");
                            }

                            @Override
                            public double getMaximumValue() {
                                return ((MConfNum) kc).max;
                            }

                            @Override
                            public double getMinimumValue() {
                                return ((MConfNum) kc).min;
                            }

                            @Override
                            public int getPrecision() {
                                return 1;
                            }
                        };
                        NumberComponent nc = new NumberComponent(kc.key, kc.description, theme.getComponentRenderer(), ns,
                                ((MConfNum) kc).min, ((MConfNum) kc).max);
                        mc.addComponent(nc);
                    } else if (kc instanceof MConfColor) {
                        MConfColor rgbaColor = (MConfColor) kc;
                        ColorSetting c = new ColorSetting() {
                            @Override
                            public Color getValue() {
                                return rgbaColor.getColor();
                            }

                            @Override
                            public void setValue(Color value) {
                                rgbaColor.setColor(value);
                            }

                            @Override
                            public Color getColor() {
                                return rgbaColor.getColor();
                            }

                            @Override
                            public boolean getRainbow() {
                                return rgbaColor.isRainbow();
                            }

                            @Override
                            public void setRainbow(boolean rainbow) {
                                rgbaColor.setRainbow(rainbow);
                            }
                        };

                        ColorComponent cc = new ColorComponent(kc.key, kc.description, theme.getComponentRenderer(),
                                new SettingsAnimation(me.zeroX150.cornos.features.module.impl.external.ClickGUI.getNumSet()), theme.getComponentRenderer(), c, false, true,
                                new com.lukflug.panelstudio.settings.Toggleable() {
                                    boolean state = false;

                                    @Override
                                    public void toggle() {
                                        state = !state;
                                    }

                                    @Override
                                    public boolean isOn() {
                                        return state;
                                    }
                                });
                        mc.addComponent(cc);
                    }
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        int w = Cornos.minecraft.getWindow().getScaledWidth();
        int h = Cornos.minecraft.getWindow().getScaledHeight();
        ButtonWidget expand = new ButtonWidget(w - 21, h - 21, 20, 20, Text.of("▼"), button -> {
            for (FixedComponent component : gui.getComponents()) {
                if (component instanceof CollapsibleContainer) {
                    CollapsibleContainer collapsibleContainer = (CollapsibleContainer) component;
                    if (!collapsibleContainer.isOn()) collapsibleContainer.toggle();
                }
            }
        });
        ButtonWidget no = new ButtonWidget(w - 21, h - 42, 20, 20, Text.of("▲"), button -> {
            for (FixedComponent component : gui.getComponents()) {
                if (component instanceof CollapsibleContainer) {
                    CollapsibleContainer collapsibleContainer = (CollapsibleContainer) component;
                    if (collapsibleContainer.isOn()) collapsibleContainer.toggle();
                }
            }
        });
        addDrawableChild(expand);
        addDrawableChild(no);
    }

    @Override
    protected com.lukflug.panelstudio.ClickGUI getGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return 10;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        int h = Cornos.minecraft.getWindow().getScaledHeight();
        int w = Cornos.minecraft.getWindow().getScaledWidth();
        DrawableHelper.fill(matrices, 0, 0, w, h, Colors.GUIBACKGROUND.get().getRGB());
        p.render();
        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        p.tick();
        super.tick();
    }
}
