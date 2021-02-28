/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: Clip
# Created by constantin at 17:46, Feb 28 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.command;

import me.constantindev.ccl.etc.base.Command;
import me.constantindev.ccl.etc.helper.ClientHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Clip extends Command {
    int failedAttempts = 0;

    public Clip() {
        super("Clip", "Basically teleporting but cool", new String[]{"clip", "cl", "tp"});
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            ClientHelper.sendChat("i need some arguments like \"v\" or \"h\" ygm");
            return;
        }
        Vec3d p = mc.player.getPos();
        Vec3d np = p;
        switch (args[0].toLowerCase()) {
            case "v":
                if (args.length == 1) {
                    ClientHelper.sendChat("Please gimme a number as 2nd arg");
                    failedAttempts = 1;
                    break;
                }
                if (!ClientHelper.isIntValid(args[1])) {
                    ClientHelper.sendChat(failedAttempts == 1 ? "homie i said a number" : (failedAttempts == 2 ? "DUDE" : "can i get a valid number as 2nd arg"));
                    failedAttempts = 2;
                    break;
                }
                int h = Integer.parseInt(args[1]);
                np = new Vec3d(p.x, p.y + h, p.z);
                break;
            case "h":
                if (args.length == 1) {
                    ClientHelper.sendChat("Please gimme a number as 2nd arg");
                    failedAttempts = 1;
                    break;
                }
                if (!ClientHelper.isIntValid(args[1])) {
                    ClientHelper.sendChat(failedAttempts == 1 ? "homie i said a number" : (failedAttempts == 2 ? "DUDE" : "can i get a valid number as 2nd arg"));
                    failedAttempts = 2;
                    break;
                }
                int mtp = Integer.parseInt(args[1]);
                Vec3d origv = MinecraftClient.getInstance().player.getRotationVector();
                Vec3d newv = new Vec3d(origv.x, 0, origv.z).multiply(mtp);
                np = new Vec3d(p.x + newv.x, p.y, p.z + newv.z);
                break;
            default:
                ClientHelper.sendChat("run the command w/o arguments and follow the instructions please");
                return;
        }
        mc.player.updatePosition(np.x, np.y, np.z);
        super.onExecute(args);
    }
}
