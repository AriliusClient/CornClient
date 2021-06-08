package me.zeroX150.cornos.etc.helper;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.mixin.IMinecraftClientAccessor;
import me.zeroX150.cornos.mixin.IRenderTickCounterAccessor;
import me.zeroX150.cornos.mixin.ISessionAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.Session;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class STL {
    public static void notifyUser(String msg) {
        if (Cornos.minecraft.player == null)
            return;
        Cornos.minecraft.player.sendMessage(Text.of(Formatting.DARK_AQUA + "[" + Formatting.AQUA + "Cornos"
                + Formatting.DARK_AQUA + "] " + Formatting.RESET + msg), false);
    }

    public static boolean tryParseI(String arg) {
        boolean isValid;
        try {
            Integer.parseInt(arg);
            isValid = true;
        } catch (Exception exc) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean tryParseL(String arg) {
        boolean isValid;
        try {
            Long.parseLong(arg);
            isValid = true;
        } catch (Exception exc) {
            isValid = false;
        }
        return isValid;
    }

    public static void placeBlock(BlockPos pos) {
        BlockState bs = Cornos.minecraft.world.getBlockState(pos);
        if (bs.getMaterial().isReplaceable()) {
            BlockHitResult bhr = new BlockHitResult(new Vec3d(.5, .5, .5), Direction.DOWN, pos, false);
            Cornos.minecraft.interactionManager.interactBlock(Cornos.minecraft.player, Cornos.minecraft.world, Hand.MAIN_HAND, bhr);
        }
    }

    public static boolean auth(String username, String password) {
        if (password.isEmpty()) {
            Session crackedSession = new Session(username, UUID.randomUUID().toString(), "CornosOnTOP", "mojang");
            ((ISessionAccessor) Cornos.minecraft).setSession(crackedSession);
            return true;
        }
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
                Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setPassword(password);
        auth.setUsername(username);
        try {
            auth.logIn();
            Session ns = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
                    auth.getAuthenticatedToken(), "mojang");
            ((ISessionAccessor) Cornos.minecraft).setSession(ns);
            return true;
        } catch (Exception ec) {
            Cornos.log(Level.ERROR, "Failed to log in: ");
            ec.printStackTrace();
            return false;
        }
    }

    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("unused")
    public static void update() {
        new Thread(() -> {
            try {
                boolean trash;
                File f = new File(STL.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                if (f.isDirectory()) {
                    STL.notifyUser("Can't check for updates.");
                    return;
                }
                File parent = new File(f.getParentFile().getParent() + "/tmp");
                if (!parent.exists())
                    trash = parent.mkdir();
                parent = new File(parent.getPath() + "/latest.jar");
                if (parent.exists()) {
                    trash = parent.delete();
                }
                downloadFile("https://github.com/AriliusClient/Cornos/raw/master/builds/latest.jar", parent.getAbsolutePath());
                HashCode hc = Files.asByteSource(f).hash(Hashing.crc32());
                HashCode hc1 = Files.asByteSource(parent).hash(Hashing.crc32());
                if (!hc.equals(hc1)) {
                    try {
                        Files.move(parent, f);
                    } catch (Exception exc) {
                        notifyUser("Your cornos installation is out of sync with the latest build!");
                        notifyUser(
                                "Failed to automatically update. Head over to https://github.com/AriliusClient/Cornos/raw/master/builds/latest.jar to get the latest version");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                notifyUser("Failed to check for updates!");
            }
        }).start();
    }

    public static void downloadFile(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public static void drop(int index) {
        ItemStack is = Cornos.minecraft.player.getInventory().getStack(index);
        Int2ObjectMap<ItemStack> mod = Int2ObjectMaps.emptyMap();
        mod.put(0,is);
        ClickSlotC2SPacket p = new ClickSlotC2SPacket(0, index, 1, SlotActionType.THROW, is, mod);
        Cornos.minecraft.getNetworkHandler().sendPacket(p);
    }

    public static Map<String, String> downloadCapes() throws IOException {
        URL u = new URL(
                "https://raw.githubusercontent.com/cornos/cornos.github.io/master/contributors.txt");
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setInstanceFollowRedirects(true);
        huc.connect();
        InputStream response = huc.getInputStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(response));
        Map<String, String> resp = new HashMap<>();
        String current;
        while ((current = r.readLine()) != null) {
            String[] cH = current.split(" ");
            if (cH.length < 1) continue;
            String username, cape;
            username = cH[0];
            if (cH.length == 1) {
                cape = "https://raw.githubusercontent.com/cornos/cornos-files/master/contribcape.png";
            } else cape = cH[1];
            resp.put(username, cape);
        }
        r.close();
        response.close();
        huc.disconnect();
        return resp;
    }

    public static float getClientTPS() {
        IRenderTickCounterAccessor accessor = (IRenderTickCounterAccessor) ((IMinecraftClientAccessor) Cornos.minecraft).getRenderTickCounter();
        return 1000f / accessor.getTickTime();
    }

    public static void setClientTPS(float newTPS) {
        IRenderTickCounterAccessor accessor = (IRenderTickCounterAccessor) ((IMinecraftClientAccessor) Cornos.minecraft).getRenderTickCounter();
        accessor.setTickTime(1000f / newTPS);
    }

    public static double roundToNTh(double in, int n) {
        double s = Math.pow(10, n);
        return Math.round(in * s) / s;
    }

    public static void interactWithItemInHotbar(int slot, BlockPos position) {
        if (slot < 0 || slot > 8 || Cornos.minecraft.player == null || Cornos.minecraft.interactionManager == null)
            return;
        int prev = Cornos.minecraft.player.getInventory().selectedSlot;
        Cornos.minecraft.player.getInventory().selectedSlot = slot;
        int ofx = Direction.DOWN.getOffsetX(), ofy = Direction.DOWN.getOffsetY(), ofz = Direction.DOWN.getOffsetZ();
        BlockHitResult bhr = new BlockHitResult(new Vec3d(position.getX(), position.getY(), position.getZ()).add(.5, .5, .5).add(ofx, ofy, ofz), Direction.DOWN, position, false);
        Cornos.minecraft.interactionManager.interactBlock(Cornos.minecraft.player, Cornos.minecraft.world,
                Hand.MAIN_HAND, bhr);
        Cornos.minecraft.player.getInventory().selectedSlot = prev;
    }
}
