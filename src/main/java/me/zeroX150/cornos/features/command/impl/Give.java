package me.zeroX150.cornos.features.command.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;

public class Give extends Command {
    public Give() {
        super("Give", "Like /give but without the /. (Requires Creative)", new String[]{"give"});
    }

    @Override
    public void onExecute(String[] args) {
        assert Cornos.minecraft.player != null;
        if (!Cornos.minecraft.player.isCreative()) {
            STL.notifyUser(
                    "\u00A7cYou must be in creative to use this command. I can't make items magically appear, sorry!");
            return;
        }
        if (args.length == 0) {
            STL.notifyUser("Usage: }give <Item>{nbt} <amount>");
            return;
        }

        ItemStackArgumentType itemStackArgumentType = new ItemStackArgumentType();

        ItemStackArgument itemStackArgument;

        try {
            itemStackArgument = itemStackArgumentType.parse(new StringReader(args[0]));
        } catch (CommandSyntaxException e) {
            STL.notifyUser("\u00A7cError: Invalid item.");
            return;
        }

        int amount = 1;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ignore) {

        }

        ItemStack itemStack;

        try {
            itemStack = itemStackArgument.createStack(amount, false);
        } catch (CommandSyntaxException e) {
            STL.notifyUser("\u00A7cThere was an error trying to create the ItemStack, please check all arguments.");
            return;
        }

        Cornos.minecraft.player.getInventory().addPickBlock(itemStack);
        STL.notifyUser("Gave you " + amount + " "
                + (amount > 1 ? itemStack.getName().getString() + "s" : itemStack.getName().getString()));

        super.onExecute(args);
    }
}
