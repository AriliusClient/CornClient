package me.zeroX150.cornos.features.command.impl;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.etc.render.Notification;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class RemovePlayer extends Command {
    public RemovePlayer() {
        super("RemovePlayer", "Removes the player you are looking at (CLIENT SIDE!)",
                new String[]{"rmplayer", "removeplayer"});
    }

    @Override
    public void onExecute(String[] args) {
        HitResult hr = Cornos.minecraft.crosshairTarget;
        if (!(hr instanceof EntityHitResult)) {
            STL.notifyUser("Not sure if you are looking at an entity");
            return;
        }
        EntityHitResult ehr = (EntityHitResult) hr;
        Entity e = ehr.getEntity();
        if (!(e instanceof PlayerEntity)) {
            STL.notifyUser("Not sure if you are looking at a player");
            return;
        }
        e.remove(Entity.RemovalReason.DISCARDED);
        Notification.create("WHERE DID HE GO??!?!?",
                new String[]{"HOLY SHIT", e.getEntityName().toUpperCase() + " IS GONE!!!"}, 3000);
        super.onExecute(args);
    }
}
