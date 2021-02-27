package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.Toggleable;
import me.constantindev.ccl.events.PacketEvent;
import me.constantindev.ccl.events.TickEvent;
import me.constantindev.ccl.mixin.PlayerMoveC2SPacketAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class DiscordPresence extends Module {
    public DiscordPresence() {
        super("Discord Presence", "Displays a RPC on Discord",MType.MISC);

}
    
}