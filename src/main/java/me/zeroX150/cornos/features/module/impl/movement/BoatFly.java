package me.zeroX150.cornos.features.module.impl.movement;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.KeyBind;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.etc.render.Notification;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class BoatFly extends Module {
    Entity lastRide = null;
    KeyBind keyDown = new KeyBind(GLFW.GLFW_KEY_LEFT_ALT);
    MConfMultiOption mode = new MConfMultiOption("mode", "vanilla", new String[]{"vanilla", "static", "velocity"}, "weee mode");

    public BoatFly() {
        super("EntityFly", "weee", ModuleType.MOVEMENT);
        mconf.add(mode);
    }

    @Override
    public void onEnable() {
        STL.notifyUser("alt = down. dont use shift");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (lastRide != null)
            lastRide.setNoGravity(false);
        super.onDisable();
    }

    @Override
    public void onExecute() {
        assert Cornos.minecraft.player != null;
        Entity vehicle = Cornos.minecraft.player.getVehicle();
        if (vehicle == null) {
            Notification.create("EntityFly", new String[]{"Disabled due to lack", "of riding entity"}, 4000);
            this.setEnabled(false);
            return;
        }
        lastRide = vehicle;
        vehicle.setNoGravity(true);
        Vec3d np = vehicle.getPos();
        GameOptions go = Cornos.minecraft.options;

        switch (mode.value) {
            case "vanilla":
                if (go.keyJump.isPressed())
                    np = np.add(0, .2, 0);
                if (keyDown.isHeld())
                    np = np.add(0, -.2, 0);
                vehicle.updatePosition(np.x, np.y, np.z);
                Vec3d v = vehicle.getVelocity();
                Vec3d nv = new Vec3d(v.x, 0, v.z);
                vehicle.setVelocity(nv);
                break;
            case "velocity":
            case "static":
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
                if (keyDown.isHeld())
                    my--;
                if (go.keyForward.isPressed())
                    mz--;
                double ts = 1;
                double s = Math.sin(Math.toRadians(y));
                double c = Math.cos(Math.toRadians(y));
                double nx = ts * mz * s;
                double nz = ts * mz * -c;
                double ny = ts * my;
                nx += ts * mx * -c;
                nz += ts * mx * -s;
                Vec3d nv3 = new Vec3d(nx, ny, nz);
                np = np.add(nv3.multiply(0.4));
                if (mode.value.equals("static")) {
                    vehicle.updatePosition(np.x, np.y, np.z);
                    vehicle.setVelocity(0, 0, 0);
                } else {
                    nv3 = nv3.multiply(0.1);
                    vehicle.addVelocity(nv3.x, nv3.y, nv3.z);
                }
                break;
        }
        vehicle.setYaw(Cornos.minecraft.player.getYaw());
        // vehicle.pitch = Cornos.minecraft.player.pitch;
        VehicleMoveC2SPacket p = new VehicleMoveC2SPacket(vehicle);
        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(p);
        super.onExecute();
    }
}
