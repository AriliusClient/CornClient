package me.zeroX150.cornos.features.module.impl.misc;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;

public class ChestStealer extends Module {
    int currentSlot = 0;
    MConfNum delay = new MConfNum("delay", 2.0, 20.0, 0, "How much delay in ticks to wait between items");
    int delayWaited = 0;

    public ChestStealer() {
        super("ChestStealer", "Steals chests", ModuleType.MISC);
        this.mconf.add(delay);
    }

    @Override
    public void onExecute() {
        if ((Cornos.minecraft.currentScreen instanceof GenericContainerScreen gc)
                && (Cornos.minecraft.crosshairTarget instanceof BlockHitResult)
                && (Cornos.minecraft.world != null && Cornos.minecraft.world
                .getBlockState(((BlockHitResult) Cornos.minecraft.crosshairTarget).getBlockPos()).getBlock()
                == (Blocks.CHEST))) {
            int lastIndex = 0;
            int interacted = 0;

            for (Slot s : gc.getScreenHandler().slots) {
                lastIndex++;
                if (lastIndex > gc.getScreenHandler().getRows() * 9)
                    break;
                if (s.getStack().getItem() == Items.AIR)
                    continue;
                delayWaited++;
                interacted++;
                if (delayWaited > delay.getValue())
                    delayWaited = 0;
                else
                    break;
                assert Cornos.minecraft.interactionManager != null;
                Cornos.minecraft.interactionManager.clickSlot(gc.getScreenHandler().syncId, s.id, 0,
                        SlotActionType.QUICK_MOVE, Cornos.minecraft.player);
            }
            if (interacted == 0)
                gc.onClose();
            currentSlot++;
        } else
            currentSlot = 0;
        super.onExecute();
    }
}
