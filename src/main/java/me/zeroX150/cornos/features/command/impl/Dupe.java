package me.zeroX150.cornos.features.command.impl;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.command.Command;
import net.minecraft.text.Text;

import java.util.Objects;

public class Dupe extends Command {
    public Dupe() {
        super("Dupe", "Duplicates the item you are holding", new String[]{"dupe", "d", "duplicate"});
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            STL.notifyUser("Available modes: popbob, 11/11");
            return;
        }
        if (Cornos.minecraft.player == null)
            return;
        switch (args[0]) {
            case "popbob":
                new Thread(() -> {
                    for (int i = 0; i < ((9 * 4) - 1); i++) {

                        Cornos.minecraft.player.getInventory().setStack(i,
                                Cornos.minecraft.player.getInventory().getMainHandStack().copy());
                        for (int ii = 0; ii < 64; ii++) {
                            Cornos.minecraft.player.getInventory().getStack(i).setCount(ii);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
            case "11/11":
                Cornos.minecraft.player.dropSelectedItem(true);
                Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).getConnection()
                        .disconnect(Text.of("[CCL controlled disconnect] Reconnect real quick please"));
        }
        super.onExecute(args);
    }
}
