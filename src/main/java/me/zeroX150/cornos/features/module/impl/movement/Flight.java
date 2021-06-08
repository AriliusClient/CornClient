package me.zeroX150.cornos.features.module.impl.movement;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.event.EventHelper;
import me.zeroX150.cornos.etc.event.EventType;
import me.zeroX150.cornos.etc.event.arg.PacketEvent;
import me.zeroX150.cornos.etc.helper.Rnd;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Flight extends Module {
    public static int tpid = 0;
    int counter = 0;
    int counter1 = 0;
    float flyingSince = 0;
    MConfMultiOption mode = new MConfMultiOption("mode", "vanilla",
            new String[]{"vanilla", "3d", "static", "jetpack", "airhop", "zika", "packet", "jboost"}, "Fly mode");
    MConfToggleable toggleFast = new MConfToggleable("toggleFast", true, "Toggle flight ever so often to bypass vanilla ac");
    MConfNum speed = new MConfNum("speed", 1.0, 10, 0, "yea");
    MConfNum airhopUp = new MConfNum("airhopUp", 1.0, 3, 0.1, "(airhop only) speed to yeet you upwards with");
    MConfToggleable sendAbilitiesUpdate = new MConfToggleable("abilities", true, "Send abilities update to bypass some acs");
    PlayerAbilities abilitiesBefore = null;
    double startheight = 0;
    List<PlayerMoveC2SPacket> trustedPackets = new ArrayList<>();

    public Flight() {
        super("Flight", "Allows you to fly", ModuleType.MOVEMENT);
        this.mconf.add(mode);
        this.mconf.add(toggleFast);
        this.mconf.add(sendAbilitiesUpdate);
        this.mconf.add(speed);
        this.mconf.add(airhopUp);
        Module parent = this;
        EventHelper.BUS.registerEvent(EventType.ONPACKETSEND, event -> {
            PacketEvent pe = (PacketEvent) event;
            if (pe.packet instanceof PlayerMoveC2SPacket && parent.isEnabled() && mode.value.equals("packet")) {
                PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) pe.packet;
                if (trustedPackets.contains(p)) {
                    trustedPackets.remove(p);
                } else {
                    event.cancel();
                }
            }
        });
    }

    @Override
    public String getContext() {
        char[] uFriendlyS = mode.value.toCharArray();
        uFriendlyS[0] = Character.toUpperCase(uFriendlyS[0]);
        return new String(uFriendlyS);
    }

    @Override
    public void onExecute() {
        if (abilitiesBefore == null) {
            assert Cornos.minecraft.player != null;
            abilitiesBefore = Cornos.minecraft.player.getAbilities();
        }
        double speed = ((MConfNum) this.mconf.getByName("speed")).getValue();
        counter1++;
        if (counter1 > 10) {
            counter1 = 0;
            assert Cornos.minecraft.player != null;
            PlayerAbilities pa = Cornos.minecraft.player.getAbilities();
            pa.allowFlying = true;
            UpdatePlayerAbilitiesC2SPacket p = new UpdatePlayerAbilitiesC2SPacket(pa);
            Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(p);
        }
        if (((MConfToggleable) this.mconf.getByName("toggleFast")).isEnabled()) {
            if (counter > 10)
                counter = 0;
            counter++;
        } else
            counter = 0;
        GameOptions go = Cornos.minecraft.options;
        switch (this.mconf.getByName("mode").value) {
            case "vanilla":
                assert Cornos.minecraft.player != null;
                Cornos.minecraft.player.getAbilities().flying = !(counter > 9);
                Cornos.minecraft.player.getAbilities().setFlySpeed((float) (speed / 10));
                break;
            case "3d":
                assert Cornos.minecraft.player != null;
                Vec3d rot = Cornos.minecraft.player.getRotationVector();
                rot = rot.multiply(speed);
                if (counter > 9)
                    Cornos.minecraft.player.setVelocity(0, -0.1, 0);
                else if (Cornos.minecraft.options.keyForward.isPressed())
                    Cornos.minecraft.player.setVelocity(rot.x, rot.y, rot.z);
                else if (Cornos.minecraft.options.keyBack.isPressed())
                    Cornos.minecraft.player.setVelocity(-rot.x, -rot.y, -rot.z);
                else
                    Cornos.minecraft.player.setVelocity(0, 0, 0);
                break;
            case "packet":
            case "static":
                assert Cornos.minecraft.player != null;
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
                if (mode.value.equalsIgnoreCase("static"))
                    Cornos.minecraft.player.setVelocity(nv3);
                else {
                    boolean flag1 = go.keyForward.isPressed() || go.keyBack.isPressed() || go.keyRight.isPressed()
                            || go.keyLeft.isPressed() || go.keySneak.isPressed() || go.keyJump.isPressed();
                    Cornos.minecraft.player.setVelocity(0, 0, 0);
                    if (flag1) {
                        Vec3d ppos = Cornos.minecraft.player.getPos();
                        Vec3d bruh = nv3.multiply(0.8);
                        // Cornos.minecraft.player.updatePosition(ppos.x+bruh.x,ppos.y+bruh.y,ppos.z+bruh.z);
                        PlayerMoveC2SPacket.PositionAndOnGround p = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 1850, ppos.z,
                                Cornos.minecraft.player.isOnGround());
                        PlayerMoveC2SPacket.PositionAndOnGround p1 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x + bruh.x, ppos.y + bruh.y,
                                ppos.z + bruh.z, Cornos.minecraft.player.isOnGround());
                        trustedPackets.add(p);
                        trustedPackets.add(p1);
                        trustedPackets.add(p1);
                        Cornos.minecraft.getNetworkHandler().sendPacket(p1);
                        Cornos.minecraft.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(++tpid));
                        Cornos.minecraft.getNetworkHandler().sendPacket(p);
                        Cornos.minecraft.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(++tpid));
                        Cornos.minecraft.getNetworkHandler().sendPacket(p1);
                        Cornos.minecraft.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(tpid));
                        Cornos.minecraft.player.setPos(ppos.x + bruh.x, ppos.y + bruh.y, ppos.z + bruh.z);
                        // Cornos.minecraft.getNetworkHandler().sendPacket(p);
                    }
                }
                break;
            case "jetpack":
                if (Cornos.minecraft.world == null)
                    return;
                if (Cornos.minecraft.options.keyJump.isPressed()) {
                    assert Cornos.minecraft.player != null;
                    Cornos.minecraft.player.addVelocity(0, speed / 30, 0);
                    Vec3d vp = Cornos.minecraft.player.getPos();
                    for (int i = 0; i < 10; i++) {
                        Cornos.minecraft.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, vp.x, vp.y, vp.z,
                                Rnd.rndD(.25) - .125, Rnd.rndD(.25) - .125, Rnd.rndD(.25) - .125);
                    }
                }
                break;
            case "airhop":
                assert Cornos.minecraft.player != null;
                Cornos.minecraft.player.addVelocity(0, -0.1, 0);
                if (Cornos.minecraft.player.getPos().y < startheight) {
                    Vec3d vel = Cornos.minecraft.player.getVelocity();
                    boolean isMoving = go.keyForward.isPressed() || go.keyBack.isPressed() || go.keyRight.isPressed()
                            || go.keyLeft.isPressed();
                    double mpt = isMoving ? 1.2 : 1;
                    Cornos.minecraft.player.setVelocity(vel.x * mpt, airhopUp.getValue(), vel.z * mpt);
                }
                break;
            case "zika":
                boolean swap = false;
                flyingSince++;
                if (flyingSince > 5) {
                    flyingSince = 0;
                    swap = true;
                }
                assert Cornos.minecraft.player != null;
                Cornos.minecraft.player.setSwimming(true);
                Vec3d ppos = Cornos.minecraft.player.getPos();
                Cornos.minecraft.player.setVelocity(0, 0, 0);
                if (ppos.y > startheight + 5) {
                    Vec3d rotVec = Cornos.minecraft.player.getRotationVector();
                    Vec3d np = ppos.add(new Vec3d(rotVec.x, 0, rotVec.z).multiply(2));
                    Cornos.minecraft.player.updatePosition(np.x, np.y + (swap ? -1 : 0.01), np.z);
                } else {
                    Cornos.minecraft.player.updatePosition(ppos.x, ppos.y + 0.8, ppos.z);
                }
                break;
            case "jboost":
                Cornos.minecraft.player.setOnGround(true);
                Cornos.minecraft.player.setAir(0);
                Cornos.minecraft.player.fallDistance = 0;
        }
        super.onExecute();
    }

    @Override
    public void onEnable() {
        assert Cornos.minecraft.player != null;
        startheight = Cornos.minecraft.player.getPos().y;
        abilitiesBefore = Cornos.minecraft.player.getAbilities();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        flyingSince = 0;
        if (abilitiesBefore == null)
            return;
        assert Cornos.minecraft.player != null;
        Cornos.minecraft.player.getAbilities().setFlySpeed(abilitiesBefore.getFlySpeed());
        Cornos.minecraft.player.getAbilities().allowFlying = Cornos.minecraft.player.isCreative();
        Cornos.minecraft.player.getAbilities().flying = false;
        super.onDisable();
    }
}
