package me.zeroX150.cornos.features.command;

import me.zeroX150.cornos.features.command.impl.*;

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
        cl.add(new Keybind());
        cl.add(new Clip());
        cl.add(new CopyIP());
        cl.add(new Leave());
        cl.add(new Scan());
        cl.add(new Drop());
        cl.add(new Give());
        cl.add(new Hologram());
        cl.add(new Panic());
        cl.add(new Enchant());
        cl.add(new Rename());
        cl.add(new Reload());
        cl.add(new BlockEgg());
        cl.add(new FakePlayer());
        cl.add(new RemovePlayer());
        cl.add(new SetSeed());
        cl.add(new AutoSign());
        cl.add(new KickBook());
        cl.add(new XrayConfig());
        cl.add(new Friend());
        cl.add(new Teleport());
        cl.add(new Fireball());
        cl.add(new CrashPlayer());

        cl.add(new RandomBook());
        cl.add(new DataDump());
        cl.add(new Filler());
        cl.add(new Poof2());
        cl.add(new ServerInfo());
    }

    public static List<Command> getAll() {
        return cl;
    }

    public static void reload() {
        cl.clear();
        init();
    }

    public static Command search(String name) {
        AtomicReference<Command> mr = new AtomicReference<>(null);
        CommandRegistry.getAll().forEach(module -> {
            boolean isGood = false;
            for (String s : module.triggers) {
                if (s.equalsIgnoreCase(name)) {
                    isGood = true;
                    break;
                }
            }
            if (isGood)
                mr.set(module);
        });
        return mr.get();
    }
}