package me.constantindev.ccl.module;

import me.constantindev.ccl.etc.MType;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.Toggleable;
import me.constantindev.ccl.events.PacketEvent;
import me.constantindev.ccl.events.TickEvent;
import me.constantindev.ccl.mixin.PlayerMoveC2SPacketAccessor;
import me.constantindev.ccl.events.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module {
    public AntiHunger() {
        super("AntiHunger", "Reduces hunger consumption.",MType.MISC);
        this.mconf.add(new Toggleable("sprint", true));
        this.mconf.add(new Toggleable("onGround", true));
    }

//aaaaahhhhh, it works but its not activating pls help
    
    private boolean lastOnGround;
    private boolean sendOnGroundTruePacket;
    private boolean ignorePacket;

    public MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onExecute() {
        lastOnGround = mc.player.isOnGround();
        sendOnGroundTruePacket = true;
    }

    
    
    
    
    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (ignorePacket) return;

        if (event.packet instanceof ClientCommandC2SPacket && ((Toggleable) this.mconf.getByName("sprint")).isEnabled()) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) event.packet).getMode();

            if (mode == ClientCommandC2SPacket.Mode.START_SPRINTING || mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                event.cancel();
            }
        }

        if (event.packet instanceof PlayerMoveC2SPacket && ((Toggleable) this.mconf.getByName("onGround")).isEnabled() && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(false);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player.isOnGround() && !lastOnGround && !sendOnGroundTruePacket) sendOnGroundTruePacket = true;

        if (mc.player.isOnGround() && sendOnGroundTruePacket && ((Toggleable) this.mconf.getByName("onGround")).isEnabled()) {
            ignorePacket = true;
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket(true));
            ignorePacket = false;

            sendOnGroundTruePacket = false;
        }

        lastOnGround = mc.player.isOnGround();
    }
}