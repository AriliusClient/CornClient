package me.zeroX150.cornos.features.module.impl.combat;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Surround extends Module {
    MConfNum delay = new MConfNum("delay", 0, 20, 0, "Delay in ticks");
    MConfToggleable checkOnGround = new MConfToggleable("checkOnGround", true, "Check if you are on solid ground");
    MConfToggleable checkVelocity = new MConfToggleable("checkVelocity", false, "Check if you are not moving");
    MConfToggleable instant = new MConfToggleable("instant", false, "Place all blocks instantly");
    MConfToggleable switchToObby = new MConfToggleable("useObby", true, "Switch to obsidian before placing");
    MConfToggleable blockMovements = new MConfToggleable("blockMovements", true, "Block movements while placing");
    GameOptions backup;
    int delayWaited = 0;

    public Surround() {
        super("Surround", "Surrounds you with shit", ModuleType.COMBAT);
        mconf.add(delay);
        mconf.add(checkOnGround);
        mconf.add(checkVelocity);
        mconf.add(instant);
        mconf.add(switchToObby);
        mconf.add(blockMovements);
    }

    @Override
    public void onExecute() {
        Vec3d pvel = Cornos.minecraft.player.getVelocity();
        Vec3d ppos = Cornos.minecraft.player.getPos();
        if (ppos.y > 255)
            return;
        boolean otherCheck = true;
        boolean velCheck = true;
        if (checkOnGround.isEnabled()) {
            otherCheck = Cornos.minecraft.player.isOnGround();
        }
        if (checkVelocity.isEnabled()) {
            velCheck = pvel.x == 0 && pvel.z == 0 && pvel.y <= 0 && pvel.y > -0.079;
        }
        if (otherCheck && velCheck) {
            delayWaited++;
            if (delayWaited > delay.getValue()) {
                Vec3d[] positions = new Vec3d[]{new Vec3d(0, -1, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, 1),
                        new Vec3d(-1, 0, 0), new Vec3d(0, 0, -1)};
                List<Vec3d> positionsWeCanReplace = new ArrayList<>();
                for (Vec3d position : positions) {
                    Vec3d current = Cornos.minecraft.player.getPos().add(position);
                    BlockPos currentBP = new BlockPos(current.x, current.y, current.z);
                    BlockState w = Cornos.minecraft.world.getBlockState(currentBP);
                    if (w.getMaterial().isReplaceable())
                        positionsWeCanReplace.add(position);
                }
                if (positionsWeCanReplace.isEmpty()) {
                    KeyBinding.updatePressedStates();
                    return;
                }
                if (blockMovements.isEnabled()) {
                    Cornos.minecraft.player.setVelocity(0, 0, 0);
                    GameOptions go = Cornos.minecraft.options;
                    if (backup == null)
                        backup = go;
                    go.keyRight.setPressed(false);
                    go.keyLeft.setPressed(false);
                    go.keyForward.setPressed(false);
                    go.keyBack.setPressed(false);
                    go.keyJump.setPressed(false);
                }
                int prevIndex = Cornos.minecraft.player.getInventory().selectedSlot;
                int obsidianIndex = -1;
                if (switchToObby.isEnabled()) {
                    for (int i = 0; i < 9; i++) {
                        ItemStack current = Cornos.minecraft.player.getInventory().getStack(i);
                        if (current.getItem() == Items.OBSIDIAN) {
                            obsidianIndex = i;
                            break;
                        }
                    }
                    if (obsidianIndex == -1) {
                        // obsidian issue
                        return;
                    }
                    if (Cornos.minecraft.player.getInventory().selectedSlot != obsidianIndex) {
                        Cornos.minecraft.player.getInventory().selectedSlot = obsidianIndex;
                    }
                }
                BlockPos bruh = Cornos.minecraft.player.getBlockPos();
                Vec3d newPos = new Vec3d(bruh.getX() + .5, bruh.getY(), bruh.getZ() + .5);
                Cornos.minecraft.player.updatePosition(newPos.x, newPos.y, newPos.z);
                for (Vec3d position : positionsWeCanReplace) {
                    Vec3d current = Cornos.minecraft.player.getPos().add(position);
                    BlockPos currentBP = new BlockPos(current.x, current.y, current.z);
                    BlockHitResult bhr = new BlockHitResult(new Vec3d(.5, .5, 5), Direction.DOWN, currentBP, false);
                    Cornos.minecraft.interactionManager.interactBlock(Cornos.minecraft.player, Cornos.minecraft.world,
                            Hand.MAIN_HAND, bhr);
                    if (!instant.isEnabled())
                        break;
                }
                Cornos.minecraft.player.getInventory().selectedSlot = prevIndex;
            }
            if (delayWaited > delay.max + 1)
                delayWaited = (int) Math.ceil(delay.max);
        } else {
            delayWaited = 0;
        }
        super.onExecute();
    }
}
