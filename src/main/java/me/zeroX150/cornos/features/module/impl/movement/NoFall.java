/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: NoFall
# Created by constantin at 18:56, Mär 06 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.features.module.impl.movement;

import java.util.Objects;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.event.EventHelper;
import me.zeroX150.cornos.etc.event.EventType;
import me.zeroX150.cornos.etc.event.arg.PacketEvent;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import me.zeroX150.cornos.mixin.packet.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
	MConfNum fallDistance = new MConfNum("fallDistance", 2, 10, 0.1);
	MConfMultiOption mode = new MConfMultiOption("mode", "packet", new String[]{"packet", "breakFall", "onGround"});

	public NoFall() {
		super("NoFall", "Tired of fall damage?", ModuleType.MOVEMENT);
		this.mconf.add(fallDistance);
		this.mconf.add(mode);
		Module parent = this;
		EventHelper.BUS.registerEvent(EventType.ONPACKETSEND, event -> {
			PacketEvent pe = (PacketEvent) event;
			if (pe.packet instanceof PlayerMoveC2SPacket && parent.isEnabled()
					&& mode.value.equalsIgnoreCase("onground")) {
				PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) pe.packet;
				((PlayerMoveC2SPacketAccessor) p).setOnGround(true);
			}
		});
	}

	@Override
	public void onExecute() {
		// bruh moment #2
		if (Cornos.minecraft.player == null)
			return;
		if (Cornos.minecraft.player.fallDistance >= fallDistance.getValue()) {
			switch (mode.value) {
				case "packet" :
					Objects.requireNonNull(Cornos.minecraft.getNetworkHandler())
							.sendPacket(new PlayerMoveC2SPacket(true));
					break;
				case "breakFall" :
					Cornos.minecraft.player.setVelocity(0, 0.1, 0);
					Cornos.minecraft.player.fallDistance = 0;
					break;
			}
		}
		super.onExecute();
	}
}
