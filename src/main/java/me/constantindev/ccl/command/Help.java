package me.constantindev.ccl.command;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.base.Command;
import me.constantindev.ccl.etc.helper.ClientHelper;
import me.constantindev.ccl.etc.reg.CommandRegistry;
import me.constantindev.ccl.etc.reg.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Help extends Command {
    public Help() {
        super("Help", "Shows all commands and modules", new String[]{"h", "help", "man", "?", "commands", "modules"});
    }
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            ClientHelper.sendChat("Please either run this with modules or commands as 1st argument.");
        } else {
            switch (args[0]) {
                case "modules":
                    ClientHelper.sendChat("All modules:");
                    ModuleRegistry.getAll().forEach(module -> ClientHelper.sendChat(Formatting.AQUA + "  " + module.name + ": " + Formatting.BLUE + module.description));
                    break;
                case "commands":
                    ClientHelper.sendChat("All commands:");
                    CommandRegistry.getAll().forEach(command -> {
                        Style bonk = Text.of("").getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Triggers: " + String.join(", ", command.triggers))));
                        LiteralText bruh1 = new net.minecraft.text.LiteralText(Formatting.DARK_AQUA + "[ " + Formatting.AQUA + Cornos.MOD_ID.toUpperCase() + Formatting.DARK_AQUA + " ]   " + Formatting.RESET + Formatting.AQUA + "  " + command.displayName + " (" + command.triggers[0] + ")" + ": " + Formatting.BLUE + command.description);
                        bruh1.setStyle(bonk);
                        assert mc.player != null;
                        mc.player.sendMessage(bruh1, false);
                    });
                    break;
                default:
                    ClientHelper.sendChat("Please either run this with modules or commands as 1st argument.");
                    break;
            }
        }
        super.onExecute(args);
    }
}
