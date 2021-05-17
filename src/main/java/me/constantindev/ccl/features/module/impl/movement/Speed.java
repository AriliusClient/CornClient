/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: Speed
# Created by constantin at 10:21, Apr 03 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.features.module.impl.movement;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.config.MConfMultiOption;
import me.constantindev.ccl.etc.config.MConfNum;
import me.constantindev.ccl.features.module.Module;
import me.constantindev.ccl.features.module.ModuleType;
import net.minecraft.client.options.GameOptions;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.Objects;

public class Speed extends Module {
    public static MConfMultiOption mode = new MConfMultiOption("mode", "vanilla", new String[]{"vanilla", "bhop", "legit", "minihop"});
    public static MConfNum speedMulti = new MConfNum("speed", 2, 5, 0);
    public static MConfNum bhopDown = new MConfNum("bhopDown", 1, 10, -0.7);
    double prev = 0.1;

    public Speed() {
        super("Speed", "why is he going fast", ModuleType.MOVEMENT);
        this.mconf.add(mode);
        this.mconf.add(speedMulti);
        this.mconf.add(bhopDown);
    }

    @Override
    public void onEnable() {
        assert Cornos.minecraft.player != null;
        EntityAttributeInstance eai = Cornos.minecraft.player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (eai != null) prev = eai.getValue();
        super.onEnable();
    }

    @Override
    public void onExecute() {
        if (!mode.value.equalsIgnoreCase("legit")) {
            assert Cornos.minecraft.player != null;
            Objects.requireNonNull(Cornos.minecraft.player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(speedMulti.getValue());
        }
        switch (mode.value) {
            case "bhop":
                if (isMoving()) {
                    if (Cornos.minecraft.player.isOnGround()) Cornos.minecraft.player.jump();
                    else Cornos.minecraft.player.addVelocity(0, -(bhopDown.getValue() / 10), 0);
                }
                break;
            case "legit":
                if (isMoving()) {
                    assert Cornos.minecraft.player != null;
                    if (Cornos.minecraft.player.isOnGround()) {
                        Cornos.minecraft.player.jump();
                    }
                }
                break;
            case "minihop":
                if (isMoving()) {
                    if (Cornos.minecraft.player.isOnGround()) {
                        Cornos.minecraft.player.jump();
                        Cornos.minecraft.player.addVelocity(0, -.35, 0);
                    } else {
                        Cornos.minecraft.player.setVelocity(Cornos.minecraft.player.getVelocity().multiply(1.1));
                    }
                }
                break;
        }
        super.onExecute();
    }

    @Override
    public void onDisable() {
        assert Cornos.minecraft.player != null;
        Objects.requireNonNull(Cornos.minecraft.player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(prev);
        super.onDisable();
    }

    boolean isMoving() {
        boolean flag1 = false;
        GameOptions go = Cornos.minecraft.options;
        if (go.keyForward.isPressed() || go.keyBack.isPressed() || go.keyRight.isPressed() || go.keyLeft.isPressed())
            flag1 = true;
        return flag1;
    }
}
