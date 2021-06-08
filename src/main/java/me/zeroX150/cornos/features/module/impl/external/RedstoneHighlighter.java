package me.zeroX150.cornos.features.module.impl.external;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfColor;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RedstoneHighlighter extends Module {
    // forgive me god
    MConfToggleable repeaters = new MConfToggleable("repeaters", true, "Render repeaters");
    MConfToggleable dust = new MConfToggleable("dust", true, "Render redstone dust");
    MConfToggleable repeatersS1 = new MConfToggleable("repeatersS1", false, "Render 1 tick delay repeaters");
    MConfToggleable repeatersS2 = new MConfToggleable("repeatersS2", true, "Render 2 tick delay repeaters");
    MConfToggleable repeatersS3 = new MConfToggleable("repeatersS3", true, "Render 3 tick delay repeaters");
    MConfToggleable repeatersS4 = new MConfToggleable("repeatersS4", true, "Render 4 tick delay repeaters");
    MConfNum range = new MConfNum("range", 30, 100, 3, "Range to search for blocks in");
    MConfColor redstoneColor = new MConfColor("redstoneColor", new Color(50, 255, 255), "Color of redstone wire");
    MConfColor repeaterColor = new MConfColor("repeaterColor", new Color(50, 255, 50), "Color of repeaters");
    int timeout = 0;
    List<BlockPos> blockMap = new ArrayList<>();

    public RedstoneHighlighter() {
        super("RedstoneHighlighter", "Highlights specific redstone components", ModuleType.RENDER);
        mconf.add(repeaters);
        mconf.add(dust);
        mconf.add(repeatersS1);
        mconf.add(repeatersS2);
        mconf.add(repeatersS3);
        mconf.add(repeatersS4);
        mconf.add(range);
        mconf.add(redstoneColor);
        mconf.add(repeaterColor);
    }

    @Override
    public void onExecute() {
        timeout++;
        if (timeout > 40)
            timeout = 0; // only refresh every 2 secs
        else
            return;
        blockMap.clear();
        BlockPos ppos = Cornos.minecraft.player.getBlockPos();
        int r = (int) (range.getValue() / 2);
        int x = ppos.getX();
        int y = ppos.getY();
        int z = ppos.getZ();
        // god fuck i know theres a more efficient way but everything i tried didnt work
        for (int _x = x - r; _x < x + r; _x++) {
            for (int _y = y - r; _y < y + r; _y++) {
                for (int _z = z - r; _z < z + r; _z++) {
                    BlockState bs = Cornos.minecraft.world.getBlockState(new BlockPos(_x, _y, _z));
                    if (bs.getBlock() == (Blocks.REDSTONE_WIRE) && bs.get(RedstoneWireBlock.POWER) != 0 && dust.isEnabled())
                        blockMap.add(new BlockPos(_x, _y, _z));
                    else if (bs.getBlock() == (Blocks.REPEATER)) {
                        int delay = bs.get(RepeaterBlock.DELAY);
                        if ((delay == 1 && repeatersS1.isEnabled()) || (delay == 2 && repeatersS2.isEnabled())
                                || (delay == 3 && repeatersS3.isEnabled()) || (delay == 4 && repeatersS4.isEnabled()))
                            blockMap.add(new BlockPos(_x, _y, _z));
                    }
                }
            }
        }
        super.onExecute();
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        for (BlockPos renderableBlock : blockMap.toArray(new BlockPos[0])) {
            BlockState state = Cornos.minecraft.world.getBlockState(renderableBlock);
            if (state.getBlock()!=(Blocks.REPEATER) && state.getBlock()!=(Blocks.REDSTONE_WIRE))
                continue;
            Color red = redstoneColor.getColor();
            Color rep = repeaterColor.getColor();
            int r = red.getRed();
            int g = red.getGreen();
            int b = red.getBlue();
            if (state.getBlock()!=(Blocks.REPEATER)) {
                r = rep.getRed();
                g = rep.getGreen();
                b = rep.getBlue();
            }
            Renderer.renderBlockOutline(
                    new Vec3d(renderableBlock.getX(), renderableBlock.getY(), renderableBlock.getZ()),
                    new Vec3d(1, 0.125, 1), r, g, b, 255);
        }
        super.onRender(ms, td);
    }

    @Override
    public String getContext() {
        return blockMap.size() + "";
    }
}