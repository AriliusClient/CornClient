package me.zeroX150.cornos.features.module.impl.world;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class Scaffold extends Module {
    public static MConfToggleable preventFalling = new MConfToggleable("preventFall", true, "Prevent falling down");
    MConfToggleable lookForBlocks = new MConfToggleable("lookForBlocks", false, "look for blocks in your inv and switch before placing");
    MConfToggleable placeMidAir = new MConfToggleable("placeMidair", true, "place blocks mid air");

    public Scaffold() {
        super("Scaffold", "Tired of falling but dont wanna use safewalk?", ModuleType.WORLD);
        this.mconf.add(lookForBlocks);
        this.mconf.add(placeMidAir);
        this.mconf.add(preventFalling);
    }

    @Override
    public void onExecute() {
        assert Cornos.minecraft.player != null;
        BlockPos current = Cornos.minecraft.player.getBlockPos().down();
        assert Cornos.minecraft.world != null;
        BlockState bs = Cornos.minecraft.world.getBlockState(current);
        if (bs.getMaterial().isReplaceable()) {
            boolean shouldPlace = false;
            if (placeMidAir.isEnabled())
                shouldPlace = true;
            else {
                int[][] bruh = new int[][]{new int[]{1, 0, 0}, new int[]{0, 1, 0}, new int[]{0, 0, 1},
                        new int[]{-1, 0, 0}, new int[]{0, -1, 0}, new int[]{0, 0, -1}, new int[]{1, 0, 1},
                        new int[]{-1, 0, 1}, new int[]{1, 0, -1}, new int[]{-1, 0, -1}};
                for (int[] c : bruh) {
                    if (!Cornos.minecraft.world.getBlockState(current.add(c[0], c[1], c[2])).isAir()) {
                        shouldPlace = true;
                        break;
                    }
                }
            }
            if (shouldPlace) {
                int prevIndex = Cornos.minecraft.player.getInventory().selectedSlot;
                int isIndex = -1;
                if (!lookForBlocks.isEnabled())
                    isIndex = prevIndex;
                else {
                    for (int i = 0; i < 9; i++) {
                        ItemStack currStack = Cornos.minecraft.player.getInventory().getStack(i);
                        if (Block.getBlockFromItem(currStack.getItem()) != Blocks.AIR) {
                            isIndex = i;
                            break;
                        }
                    }
                }
                if (isIndex == -1)
                    return;
                STL.interactWithItemInHotbar(isIndex, current);
                Cornos.minecraft.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            }
        }
        super.onExecute();
    }
}
