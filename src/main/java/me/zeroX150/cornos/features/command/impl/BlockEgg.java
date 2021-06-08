/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: BlockEgg
# Created by constantin at 23:08, Apr 03 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.features.command.impl;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEgg extends Command {
    public BlockEgg() {
        super("PBlock", "Places any block lulw", new String[]{"pblock", "placeblock"});
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            STL.notifyUser("bruh.");
            return;
        }
        Block b = Registry.BLOCK.get(new Identifier(args[0]));
        assert Cornos.minecraft.player != null;
        ItemStack is = Hologram.getFallingBlockStack(Cornos.minecraft.player.getPos(), b);
        Cornos.minecraft.player.getInventory().addPickBlock(is);
        super.onExecute(args);
    }
}
