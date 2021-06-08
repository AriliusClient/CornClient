package me.zeroX150.cornos.features.module.impl.world;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.KeyBind;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.etc.render.Notification;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class AutoWither extends Module {
    KeyBind sneak = new KeyBind(GLFW.GLFW_KEY_LEFT_ALT);
    Vec3d[] sandPlacements = new Vec3d[0];
    Vec3d[] skullPlacements = new Vec3d[0];

    public AutoWither() {
        super("AutoWither", "Automatically makes a wither", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        Notification.create("AutoWither", new String[]{"Press §cLAlt§r to place wither"}, 5000);
        super.onEnable();
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        if (sandPlacements.length < 4 || skullPlacements.length < 3)
            return;
        for (Vec3d v : sandPlacements) {
            Vec3d v1 = new Vec3d(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
            Renderer.renderBlockOutline(v1, new Vec3d(1, 1, 1), 255, 255, 20, 255);
        }
        for (Vec3d v : skullPlacements) {
            Vec3d v1 = new Vec3d(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
            Renderer.renderBlockOutline(v1.add(.25, 0, .25), new Vec3d(.5, .5, .5), 255, 20, 255, 255);
        }
        super.onRender(ms, td);
    }

    @Override
    public void onExecute() {
        HitResult hr = Cornos.minecraft.crosshairTarget;
        if (hr == null) {
            return;
        }
        Vec3d b = hr.getPos();
        Vec3d u = b.add(0, 1, 0);
        Vec3d r = u.add(1, 0, 0);
        Vec3d l = u.add(-1, 0, 0);
        Vec3d sb = u.add(0, 1, 0);
        Vec3d sr = sb.add(1, 0, 0);
        Vec3d sl = sb.add(-1, 0, 0);
        this.sandPlacements = new Vec3d[]{b, u, r, l};
        this.skullPlacements = new Vec3d[]{sb, sr, sl};
        if (sneak.isPressed()) {
            int soulSandIndex = -1;
            int witherSkullIndex = -1;
            for (int i = 0; i < 8; i++) {
                assert Cornos.minecraft.player != null;
                ItemStack current = Cornos.minecraft.player.getInventory().getStack(i);
                if (current.isEmpty())
                    continue;
                if (current.getItem().equals(Items.SOUL_SAND))
                    soulSandIndex = i;
                if (current.getItem().equals(Items.WITHER_SKELETON_SKULL))
                    witherSkullIndex = i;
            }
            if (soulSandIndex != -1 && witherSkullIndex != -1) {
                ItemStack sand = Cornos.minecraft.player.getInventory().getStack(soulSandIndex);
                ItemStack skull = Cornos.minecraft.player.getInventory().getStack(witherSkullIndex);
                new Thread(() -> {
                    Vec3d[] sandCopy = sandPlacements;
                    Vec3d[] skullCopy = skullPlacements;
                    Cornos.minecraft.player.getInventory().addPickBlock(sand);
                    STL.sleep(70);
                    for (Vec3d current : sandCopy) {
                        BlockHitResult bhr = new BlockHitResult(new Vec3d(.5, .5, .5), Direction.DOWN,
                                new BlockPos(current.x, current.y, current.z), false);
                        PlayerInteractBlockC2SPacket p = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr);
                        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(p);
                    }
                    STL.sleep(70);
                    Cornos.minecraft.player.getInventory().addPickBlock(skull);
                    STL.sleep(70);
                    for (Vec3d current : skullCopy) {
                        BlockHitResult bhr = new BlockHitResult(new Vec3d(.5, .5, .5), Direction.DOWN,
                                new BlockPos(current.x, current.y, current.z), false);
                        PlayerInteractBlockC2SPacket p = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr);
                        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(p);
                    }
                }).start();
            }
        }
        super.onExecute();
    }
}
