/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: PacketApplyEvent
# Created by constantin at 13:54, Feb 28 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.etc.event.arg;

import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PacketApplyEvent extends Event {
    public Packet<?> packet;
    public ClientPlayPacketListener packetListener;

    public PacketApplyEvent(Packet<?> p, ClientPlayPacketListener cppl) {
        this.packet = p;
        this.packetListener = cppl;
    }
}