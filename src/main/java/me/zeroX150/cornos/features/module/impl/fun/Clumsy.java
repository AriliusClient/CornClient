package me.zeroX150.cornos.features.module.impl.fun;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.helper.Rnd;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;

import java.util.Objects;

public class Clumsy extends Module {
    int i = 1;
    MConfMultiOption name = new MConfMultiOption("name", "randomEN",
            new String[]{"randomBin", "randomAscii", "randomEN", "none"}, "Mode to name blocks after");
    String[] enNames = new String[]{"Ashley~#6350 is hot", "suck my dick", "this item is worthless", "fucking retard",
            "i am dumb as fuck", "man.", "69420", "yep.", "just a random item", "sex is nice", "L", "retard moment"};
    int dropped = 0;

    public Clumsy() {
        super("Clumsy", "Seems as if you dropped the entirety of the game's items", ModuleType.FUN);
        this.mconf.add(name);
    }

    @Override
    public void onExecute() {
        assert Cornos.minecraft.player != null;
        if (!Cornos.minecraft.player.isCreative()) {
            STL.notifyUser("you need creative");
            setEnabled(false);
            return;
        }
        Item current = Item.byRawId(i);
        if (current == Items.AIR) {
            i = 0;
        }
        ItemStack is = new ItemStack(current);
        String name = "";
        switch (this.name.value) {
            case "randomBin":
                name = Rnd.rndBinStr(10);
                break;
            case "randomAscii":
                name = Rnd.rndAscii(10);
                break;
            case "randomEN":
                int index = (int) Math.floor(Math.random() * enNames.length);
                name = enNames[index];
                break;
        }
        if (!this.name.value.equals("none"))
            is.setCustomName(Text.of(name));
        CreativeInventoryActionC2SPacket c = new CreativeInventoryActionC2SPacket(9, is);
        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(c);
        dropped++;
        STL.drop(9);
        i++;
        super.onExecute();
    }

    @Override
    public String getContext() {
        return i + "-" + dropped;
    }
}
