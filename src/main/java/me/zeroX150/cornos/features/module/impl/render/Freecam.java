package me.zeroX150.cornos.features.module.impl.render;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.event.EventHelper;
import me.zeroX150.cornos.etc.event.EventType;
import me.zeroX150.cornos.etc.event.arg.PacketEvent;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Objects;

public class Freecam extends Module {
    // this shit is excessively documented because even i dont understand how it
    // works

    // options
    MConfNum speed = new MConfNum("speed", 1, 10, 0, "schbeed");
    // start cache
    Vec3d startloc;
    float pitch = 0, yaw = 0;

    public Freecam() {
        super("Freecam", "You can look outside yourself now", ModuleType.RENDER);
        this.mconf.add(speed);
        Module parent = this;
        // prevent movement packets from sending when module is enabled
        EventHelper.BUS.registerEvent(EventType.ONPACKETSEND, event -> {
            if (!parent.isEnabled())
                return;
            PacketEvent pe = (PacketEvent) event;
            if (pe.packet instanceof PlayerMoveC2SPacket) {
                event.cancel();
            }
            if (pe.packet instanceof PlayerInputC2SPacket) {
                event.cancel();
            }
            if (pe.packet instanceof PlayerInteractBlockC2SPacket) {
                event.cancel();
            }
            if (pe.packet instanceof PlayerActionC2SPacket) {
                event.cancel();
            }
        });
    }

    @Override
    public void onEnable() {
        // save cache
        assert Cornos.minecraft.player != null;
        startloc = Cornos.minecraft.player.getPos();
        pitch = Cornos.minecraft.player.getPitch();
        yaw = Cornos.minecraft.player.getYaw();
        // make player entity that shows where we left off when we started the module
        assert Cornos.minecraft.world != null;

        // make us fly & disable vanilla flight
        Cornos.minecraft.player.getAbilities().flying = true;
        Cornos.minecraft.player.getAbilities().setFlySpeed(0);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        assert Cornos.minecraft.player != null;
        // place us back where we started
        if (startloc != null) {
            Cornos.minecraft.player.updatePositionAndAngles(startloc.x, startloc.y, startloc.z, yaw, pitch);
        }
        startloc = null;
        yaw = pitch = 0;
        // disable flight & re-enable vanilla flight
        Cornos.minecraft.player.getAbilities().flying = false;
        Cornos.minecraft.player.getAbilities().setFlySpeed(0.05f);
        // disable noclip
        Cornos.minecraft.player.noClip = false;
        Objects.requireNonNull(Cornos.minecraft.getCameraEntity()).noClip = false;
        // vel 0
        Cornos.minecraft.player.setVelocity(0, 0, 0);
        super.onDisable();
    }

    @Override
    public void onExecute() {
        // set us flying & keep vanilla flight at a 0
        assert Cornos.minecraft.player != null;
        Cornos.minecraft.player.getAbilities().flying = true;
        Cornos.minecraft.player.getAbilities().setFlySpeed(0);
        // set ground to false and fall distance to 0
        Cornos.minecraft.player.setOnGround(false);
        Cornos.minecraft.player.fallDistance = 0;
        // first perspective
        Cornos.minecraft.options.setPerspective(Perspective.FIRST_PERSON);
        Cornos.minecraft.player.setSwimming(false);
        Cornos.minecraft.player.setPose(EntityPose.STANDING);

        // static flight, not much to see here, just moving the player

        GameOptions go = Cornos.minecraft.options;
        float speed = (float) this.speed.getValue() / 5;
        float y = Cornos.minecraft.player.getYaw();
        int mx = 0, my = 0, mz = 0;
        if (go.keyJump.isPressed())
            my++;
        if (go.keyBack.isPressed())
            mz++;
        if (go.keyLeft.isPressed())
            mx--;
        if (go.keyRight.isPressed())
            mx++;
        if (go.keySneak.isPressed())
            my--;
        if (go.keyForward.isPressed())
            mz--;
        double ts = speed / 2;
        double s = Math.sin(Math.toRadians(y));
        double c = Math.cos(Math.toRadians(y));
        double nx = ts * mz * s;
        double nz = ts * mz * -c;
        double ny = ts * my;
        nx += ts * mx * -c;
        nz += ts * mx * -s;
        Vec3d nv3 = new Vec3d(nx, ny, nz);
        Vec3d ov3 = Cornos.minecraft.player.getPos();
        Vec3d bruh = ov3.add(nv3);
        Cornos.minecraft.player.updatePosition(bruh.x, bruh.y, bruh.z);
        Cornos.minecraft.player.setVelocity(Vec3d.ZERO);
        super.onExecute();
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        // enable noclip on onRender() because minecraft is retarded
        assert Cornos.minecraft.player != null;
        Cornos.minecraft.player.noClip = true;
        assert Cornos.minecraft.cameraEntity != null;
        Cornos.minecraft.cameraEntity.noClip = true;

        if (startloc != null) {
            Renderer.renderBlockOutline(startloc.subtract(0.6 / 2, 0, 0.6 / 2), new Vec3d(0.6, 1.8, 0.6), 50, 255, 173,
                    255);
            Renderer.renderLine(Renderer.getCrosshairVector(), startloc.subtract(0, -1.8 / 2, 0),
                    new Color(50, 255, 173), 2);
        }

        super.onRender(ms, td);
    }
}
