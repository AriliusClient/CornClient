/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: Clip
# Created by constantin at 17:46, Feb 28 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.features.command.impl;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.util.math.Vec3d;

public class Clip extends Command {
	int failedAttempts = 0;

	public Clip() {
		super("Clip", "Basically teleporting but cool", new String[]{"clip", "cl", "tp"});
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			STL.notifyUser("i need some arguments like \"v\" or \"h\" ygm");
			return;
		}
		assert Cornos.minecraft.player != null;
		Vec3d p = Cornos.minecraft.player.getPos();
		Vec3d np = p;
		switch (args[0].toLowerCase()) {
			case "v" :
				if (args.length == 1) {
					STL.notifyUser("Please gimme a number as 2nd arg");
					failedAttempts = 1;
					break;
				}
				if (!STL.tryParseI(args[1])) {
					STL.notifyUser(failedAttempts == 1
							? "homie i said a number"
							: (failedAttempts == 2 ? "DUDE" : "can i get a valid number as 2nd arg"));
					failedAttempts = 2;
					break;
				}
				int h = Integer.parseInt(args[1]);
				np = new Vec3d(p.x, p.y + h, p.z);
				break;
			case "h" :
				if (args.length == 1) {
					STL.notifyUser("Please gimme a number as 2nd arg");
					failedAttempts = 1;
					break;
				}
				if (!STL.tryParseI(args[1])) {
					STL.notifyUser(failedAttempts == 1
							? "homie i said a number"
							: (failedAttempts == 2 ? "DUDE" : "can i get a valid number as 2nd arg"));
					failedAttempts = 2;
					break;
				}
				int mtp = Integer.parseInt(args[1]);
				Vec3d origv = Cornos.minecraft.player.getRotationVector();
				Vec3d newv = new Vec3d(origv.x, 0, origv.z).multiply(mtp);
				np = new Vec3d(p.x + newv.x, p.y, p.z + newv.z);
				break;
			default :
				STL.notifyUser("run the command w/o arguments and follow the instructions please");
				return;
		}
		Cornos.minecraft.player.updatePosition(np.x, np.y, np.z);
		super.onExecute(args);
	}
}
