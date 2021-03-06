package me.zeroX150.cornos;

import me.zeroX150.cornos.etc.ItemExploits;
import me.zeroX150.cornos.etc.NotificationManager;
import me.zeroX150.cornos.etc.event.EventHelper;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.etc.manager.ConfigManager;
import me.zeroX150.cornos.etc.manager.FriendsManager;
import me.zeroX150.cornos.etc.manager.KeybindManager;
import me.zeroX150.cornos.features.command.CommandRegistry;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.ClientConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Cornos implements ModInitializer {

    public static final String MOD_ID = "ccl";
    public static final String MOD_NAME = "Cornos";
    // itemgroups (pog)
    public static final ItemGroup GENERAL = FabricItemGroupBuilder.create(new Identifier("ccl", "exploits"))
            .icon(() -> new ItemStack(Items.BOOK)).appendItems(stacks -> {
                for (ItemExploits value : ItemExploits.values()) {
                    ItemStack exploit = value.get();
                    if (exploit != null)
                        stacks.add(exploit);
                }
            }).build();
    public static SoundEvent BONG_SOUND = new SoundEvent(new Identifier("ccl", "bong"));
    public static SoundEvent HITMARKER_SOUND = new SoundEvent(new Identifier("ccl", "hitmarker"));
    public static SoundEvent VINEBOOM_SOUND = new SoundEvent(new Identifier("ccl", "vineboom"));
    public static Logger LOGGER = LogManager.getLogger();
    public static MinecraftClient minecraft = MinecraftClient.getInstance();
    public static Thread fastUpdater;
    public static NotificationManager notifMan;
    public static FriendsManager friendsManager;
    public static Map<String, String> capes = new HashMap<>();
    public static ClientConfig config;

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static void onMinecraftCreate() {
        if (config.mconf.getByName("customProcessIcon").value.equals("on")) {
            InputStream inputStream = Cornos.class.getClassLoader().getResourceAsStream("assets/ccl/icon1.png");
            Cornos.minecraft.getWindow().setIcon(inputStream, inputStream);
        }
    }

    @Override
    public void onInitialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::sconf));
        log(Level.INFO, "Initializing main client");
        Registry.register(Registry.SOUND_EVENT, BONG_SOUND.getId(), BONG_SOUND);
        Registry.register(Registry.SOUND_EVENT, HITMARKER_SOUND.getId(), HITMARKER_SOUND);
        notifMan = new NotificationManager();
        friendsManager = new FriendsManager();
        log(Level.INFO, "Registering event bus");
        EventHelper.BUS.init();
        log(Level.INFO, "Initializing command registry");
        CommandRegistry.init();
        log(Level.INFO, "Initializing module registry");
        ModuleRegistry.init();
        config = (ClientConfig) ModuleRegistry.search(ClientConfig.class);
        log(Level.INFO, "Loading the configuration file");
        ConfigManager.lconf();
        log(Level.INFO, "Registering all keybinds");
        KeybindManager.init();
        try {
            log(Level.INFO, "Getting capes");
            capes = STL.downloadCapes();
            log(Level.INFO, "Contributor UUIDs:");
            File cornosCapes = new File(minecraft.runDirectory.getAbsolutePath() + "/cornosCapes/");
            if (!cornosCapes.exists()) cornosCapes.mkdir();
            for (String cape : capes.keySet()) {
                log(Level.INFO, "  " + cape + " has cape " + capes.get(cape));
                log(Level.INFO, "  - DOWNLOAD " + capes.get(cape));
                STL.downloadFile(capes.get(cape), cornosCapes.getAbsolutePath() + "/" + cape + ".png");
            }
        } catch (IOException e) {
            log(Level.INFO, "Failed to download capes.");
            e.printStackTrace();
        }
        log(Level.INFO, "All features registered. Ready to load game");

        fastUpdater = new Thread(() -> {
            while (true) {
                try {
                    if (Cornos.minecraft.player != null) {
                        for (Module m : ModuleRegistry.getAll()) {
                            if (m.isEnabled())
                                m.onFastUpdate();
                        }
                    }
                    Thread.sleep(10);
                } catch (Exception ignored) {
                }
            }
        });
        fastUpdater.start();
    }

}