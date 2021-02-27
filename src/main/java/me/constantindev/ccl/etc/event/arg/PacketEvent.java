/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: PacketSendEArg
# Created by constantin at 13:25, Feb 28 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.etc.event.arg;

import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    public Packet<?> packet;

    public PacketEvent(Packet<?> packetSent) {
        this.packet = packetSent;
    }
}