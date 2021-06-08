package me.zeroX150.cornos.features.module.impl.misc;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.features.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Hand;

import java.lang.reflect.Field;

public class AutoFish extends Module {
    public AutoFish() {
        super("AutoFish", "Makes you fish, automatically");
    }

    @Override
    public void onExecute() {
        assert Cornos.minecraft.player != null;
        FishingBobberEntity fishingBobberEntity = Cornos.minecraft.player.fishHook;
        if (fishingBobberEntity == null)
            return;
        try {
            Field f = fishingBobberEntity.getClass().getDeclaredField("caughtFish");
            f.setAccessible(true);
            boolean caughtFish = f.getBoolean(fishingBobberEntity);
            if (caughtFish) {
                assert Cornos.minecraft.interactionManager != null;
                Cornos.minecraft.interactionManager.interactItem(Cornos.minecraft.player, Cornos.minecraft.world,
                        Hand.MAIN_HAND);
                fishingBobberEntity.remove(Entity.RemovalReason.DISCARDED);
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Cornos.minecraft.interactionManager.interactItem(Cornos.minecraft.player, Cornos.minecraft.world,
                            Hand.MAIN_HAND);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onExecute();
    }
}
