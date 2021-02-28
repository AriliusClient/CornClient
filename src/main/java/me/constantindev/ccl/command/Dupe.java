package me.constantindev.ccl.command;

import me.constantindev.ccl.etc.base.Command;
import me.constantindev.ccl.etc.helper.ClientHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class Dupe extends Command {
    public Dupe() {
        super("Dupe", "Duplicates the item you are holding", new String[]{"dupe", "d", "duplicate"});
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            ClientHelper.sendChat("Available modes: popbob, 11/11");
            return;
        }
        if (mc.player == null) return;
        switch (args[0]) {
            case "popbob":
                new Thread(() -> {
                    for (int i = 0; i < ((9 * 4) - 1); i++) {

                        mc.player.inventory.setStack(i, mc.player.inventory.getMainHandStack());
                        for (int ii = 0; ii < 64; ii++) {
                            mc.player.inventory.getStack(i).setCount(ii);
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
                assert mc.player != null;
                mc.player.dropSelectedItem(true);
                Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of("bruh"));
        }
        super.onExecute(args);
    }
}
