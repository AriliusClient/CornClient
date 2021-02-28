package me.constantindev.ccl.etc.reg;

import me.constantindev.ccl.command.*;
import me.constantindev.ccl.etc.base.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CommandRegistry {
    private static final List<Command> cl = new ArrayList<>();

    public static void init() {
        cl.add(new Toggle());
        cl.add(new Config());
        cl.add(new Test());
        cl.add(new Help());
        cl.add(new Dupe());
        cl.add(new Crash());
        cl.add(new Keybind());
        cl.add(new Vclip());
    }

    public static List<Command> getAll() {
        return cl;
    }

    public static Command getByName(String name) {
        AtomicReference<Command> mr = new AtomicReference<>(null);
        CommandRegistry.getAll().forEach(module -> {
            boolean isGood = false;
            for (String s : module.triggers) {
                if (s.equalsIgnoreCase(name)) {
                    isGood = true;
                    break;
                }
            }
            if (isGood) mr.set(module);
        });
        return mr.get();
    }
}
