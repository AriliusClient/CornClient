package me.constantindev.ccl.command;

import me.constantindev.ccl.etc.base.Command;
import me.constantindev.ccl.etc.helper.ClientHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Vclip extends Command {
    public Vclip() {
        super("Vclip", "clip vertically", new String[]{ "vclip"});
    }

    public MinecraftClient mc = MinecraftClient.getInstance();
    
    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            
            ClientPlayerEntity player = mc.player;
            assert player != null;

            double blocks = 5;
            player.updatePosition(player.getX(), player.getY() + blocks, player.getZ());
            
            return;
        }
        
        ClientHelper.sendChat("Wrong haha bad");

      
    }
}
