/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: Rename
# Created by constantin at 18:45, Mär 26 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.features.command.impl;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class Rename extends Command {
    public Rename() {
        super("Rename", "Renames the current item", new String[]{"rename", "re", "rn", "name", "setname"});
    }

    @Override
    public void onExecute(String[] args) {
        assert Cornos.minecraft.player != null;
        ItemStack is = Cornos.minecraft.player.getInventory().getStack(Cornos.minecraft.player.getInventory().selectedSlot);
        if (is.isEmpty()) {
            STL.notifyUser("man u gotta hold sum");
            return;
        }
        if (args.length == 0) {
            STL.notifyUser("Homie ima need the new name");
            return;
        }
        NbtCompound compoundTag = is.getOrCreateSubTag("display");
        compoundTag.putString("Name",
                Text.Serializer.toJson(Text.of("§r" + String.join(" ", args).replaceAll("&", "§"))));

        super.onExecute(args);
    }
}
