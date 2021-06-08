package me.zeroX150.cornos.features.module.impl.world;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoMLG extends Module {
    MConfNum dist = new MConfNum("fallDist", 6, 20, 3, "Distance to fall to do the funnies");

    Item[] lifesavers = new Item[]{
            Items.SLIME_BLOCK,
            Items.WATER_BUCKET,
            Items.HAY_BLOCK,
            Items.COBWEB
    };

    public AutoMLG() {
        super("AutoMLG", "GAMER MOMENT", ModuleType.WORLD);
        mconf.add(dist);
    }

    @Override
    public void onFastUpdate() {
        if (!Cornos.minecraft.player.isOnGround() && Cornos.minecraft.player.fallDistance > dist.getValue()) {
            Item used = null;
            int itemToUse = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack is = Cornos.minecraft.player.getInventory().getStack(i);
                boolean isGood = false;
                for (Item lifesaver : lifesavers) {
                    if (is.getItem() == lifesaver) {
                        isGood = true;
                        break;
                    }
                }
                if (isGood) {
                    itemToUse = i;
                    used = is.getItem();
                    break;
                }
            }
            if (itemToUse == -1) return; // no item found that can be used for a mlg
            BlockPos bp = Cornos.minecraft.player.getBlockPos();
            for (int yMin = bp.getY(); yMin > bp.getY() - 13; yMin--) {
                BlockPos c = new BlockPos(bp.getX(), yMin, bp.getZ());
                BlockState bs = Cornos.minecraft.world.getBlockState(c);
                Vec3d v = new Vec3d(c.getX(), c.getY(), c.getZ());
                if (!bs.isAir()) {
                    if (used != Items.WATER_BUCKET) {
                        if (Cornos.minecraft.player.getPos().distanceTo(v) > 4) continue;
                        STL.interactWithItemInHotbar(itemToUse, c.add(0, 1, 0));
                    } else {
                        // i hate this, thanks minecraft
                        Cornos.minecraft.player.getInventory().selectedSlot = itemToUse;
                        Cornos.minecraft.player.setPitch(90);
                        PlayerInteractItemC2SPacket p = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND);
                        Cornos.minecraft.getNetworkHandler().sendPacket(p);
                    }
                    Cornos.minecraft.player.fallDistance = 0;
                    break;
                }
            }
        }
    }
}
