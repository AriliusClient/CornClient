package me.constantindev.ccl.etc.helper;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.ms.KeyBind;
import me.constantindev.ccl.etc.reg.ModuleRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeybindMan {
    public static Map<String, KeyBind> binds = new HashMap<>();
    public static boolean freezeTabGui;
    static Module tab = ModuleRegistry.search("TabGUI");

    public static void init() {
        for (Module module : ModuleRegistry.getAll()) {
            if (!module.mconf.getByName("keybind").value.equals("-1.0")) {
                binds.put(module.name, new KeyBind((int) Double.parseDouble(module.mconf.getByName("keybind").value)));
            }
            binds.put("TAB_UP", new KeyBind(265));
            binds.put("TAB_DOWN", new KeyBind(264));
            binds.put("TAB_LEFT", new KeyBind(263));
            binds.put("TAB_RIGHT", new KeyBind(262));
            binds.put("TAB_ENTER", new KeyBind(GLFW.GLFW_KEY_ENTER));
        }
    }

    public static void tick() {
        binds.forEach((s, keyBinding) -> {
            if (keyBinding.isPressed() && Cornos.minecraft.currentScreen == null)
                if (s.contains("TAB_")) {
                    if (tab.isEnabled()) {
                        if (!freezeTabGui) {
                            ModuleRegistry.getTabManager().keyPressed(keyBinding.keycode);
                        } else {
                            freezeTabGui = false;
                        }
                    }
                } else {
                    Module m = ModuleRegistry.search(s);
                    m.setEnabled(!m.isEnabled());
                }
        });
    }

    public static void reload() {
        binds.clear();
        init();
    }
}
