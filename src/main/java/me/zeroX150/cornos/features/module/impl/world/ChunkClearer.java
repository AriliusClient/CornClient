package me.zeroX150.cornos.features.module.impl.world;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.etc.helper.STL;
import me.zeroX150.cornos.etc.render.Notification;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class ChunkClearer extends Module {
    BlockPos start = null;
    BlockPos end = null;
    MConfMultiOption mode = new MConfMultiOption("mode", "client", new String[]{"client", "packet", "legit"}, "da mode");
    MConfToggleable ignoreXray = new MConfToggleable("ignoreXray", true, "ignore xray blocks in config");
    MConfToggleable clientTeleport = new MConfToggleable("clientTP", false, "teleport the player client side and server side");
    MConfNum delay = new MConfNum("delay", 0, 20, 0, "da delay");
    int delayWaited = 0;
    MConfMultiOption fast = new MConfMultiOption("overdrive", "none", new String[]{"none", "light", "strong", "max"}, "how strong to fuck the chunk up");

    public ChunkClearer() {
        super("ChunkClearer", "makes a chunk perish", ModuleType.WORLD);
        this.mconf.add(mode);
        this.mconf.add(ignoreXray);
        this.mconf.add(clientTeleport);
        this.mconf.add(delay);
        this.mconf.add(fast);
    }

    @Override
    public void onEnable() {
        assert Cornos.minecraft.player != null;
        if (fast.value.equals("max"))
            STL.notifyUser("oh no");
        int cx = Cornos.minecraft.player.getChunkPos().x;
        int cz = Cornos.minecraft.player.getChunkPos().z;
        int startX = 16 * cx;
        int startZ = 16 * cz;
        int endX = startX + 15;
        int endZ = startZ + 15;
        start = new BlockPos(endX, 255, endZ);
        end = new BlockPos(startX, 0, startZ);
        super.onEnable();
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        if (start == null || end == null)
            return;
        Renderer.renderBlockOutline(new Vec3d(start.getX() - 15, 0, start.getZ() - 15), new Vec3d(16, 255, 16), 255, 0,
                255, 255);
        super.onRender(ms, td);
    }

    @Override
    public void onExecute() {
        delayWaited++;
        if (delayWaited > delay.getValue()) {
            delayWaited = 0;
        } else
            return;
        Vec3d block = null;
        boolean showParticles = mode.value.equals("client");
        boolean interacted = false;
        if (start == null || end == null)
            return;
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos bp1 = start.add(-x, -y, -z);
                    assert Cornos.minecraft.world != null;
                    BlockState bs = Cornos.minecraft.world.getBlockState(bp1);
                    boolean shouldSkip = bs.isOf(Blocks.AIR) || bs.isOf(Blocks.CAVE_AIR) || bs.isOf(Blocks.WATER)
                            || bs.isOf(Blocks.LAVA);
                    boolean shift = false;
                    if (ignoreXray.isEnabled() && !shouldSkip) {
                        for (Block xrayBlock : CConf.xrayBlocks) {
                            if (bs.getBlock() == (xrayBlock)) {
                                shouldSkip = true;
                                break;
                            }
                        }
                    }
                    if (!shouldSkip) {
                        BlockPos tpTo = bp1.up(2);
                        BlockPos check = tpTo.up();
                        BlockState b1 = Cornos.minecraft.world.getBlockState(tpTo);
                        BlockState b2 = Cornos.minecraft.world.getBlockState(check);
                        if (!b1.isAir())
                            shift = true;
                        if (!b2.isAir())
                            shift = true;
                    }
                    if (shouldSkip)
                        continue;
                    BlockPos tpGoal = null;
                    if (shift) {
                        for (int[] i : new int[][]{new int[]{1, 0, 0}, new int[]{1, 1, 0}, new int[]{0, 0, 1},
                                new int[]{0, 1, 1}, new int[]{-1, 0, 0}, new int[]{-1, 1, 0}, new int[]{0, 0, -1},
                                new int[]{0, 1, -1}, new int[]{1, 0, 1}, new int[]{-1, 0, -1}}) {
                            BlockPos to = bp1.add(i[0], i[1], i[2]);
                            BlockState b1 = Cornos.minecraft.world.getBlockState(to);
                            BlockState b2 = Cornos.minecraft.world.getBlockState(to.up());
                            boolean downGood = b1.isAir()
                                    || b1.getBlock() == (Blocks.WATER) || b1.getBlock() == (Blocks.LAVA);
                            boolean upGood = b2.isAir()
                                    || b2.getBlock() == (Blocks.WATER) || b2.getBlock() == (Blocks.LAVA);
                            if (downGood && upGood) {
                                tpGoal = to;
                                break;
                            }
                        }
                    } else
                        tpGoal = bp1.up(2);
                    if (tpGoal == null)
                        continue;
                    if (!mode.value.equals("legit")) {
                        PlayerMoveC2SPacket.PositionAndOnGround p = new PlayerMoveC2SPacket.PositionAndOnGround(tpGoal.getX() + .5,
                                tpGoal.getY(), tpGoal.getZ() + .5, true);
                        Objects.requireNonNull(Cornos.minecraft.getNetworkHandler()).sendPacket(p);
                        if (clientTeleport.isEnabled()) {
                            assert Cornos.minecraft.player != null;
                            Cornos.minecraft.player.updatePosition(tpGoal.getX() + .5, tpGoal.getY(),
                                    tpGoal.getZ() + .5);
                        }
                        if (showParticles) {
                            assert Cornos.minecraft.interactionManager != null;
                            Cornos.minecraft.interactionManager.attackBlock(bp1, Direction.DOWN);
                        } else {
                            PlayerActionC2SPacket p1 = new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, bp1, Direction.DOWN);
                            PlayerActionC2SPacket p2 = new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, bp1, Direction.DOWN);
                            Cornos.minecraft.getNetworkHandler().sendPacket(p1);
                            Cornos.minecraft.getNetworkHandler().sendPacket(p2);
                        }
                    } else {
                        block = new Vec3d(bp1.getX(), bp1.getY(), bp1.getZ());
                        interacted = true;
                        break;
                    }
                    interacted = true;
                    if (fast.value.equals("none"))
                        break;
                }
                if (interacted && !fast.value.equals("strong") && !fast.value.equals("max"))
                    break;
            }
            if (interacted && !fast.value.equals("max"))
                break;
        }
        if (block != null) {
            assert Cornos.minecraft.player != null;
            Cornos.minecraft.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, block.add(.5, .5, .5));
            Cornos.minecraft.player.setPitch(90);
            if ((block.y + 2) < Cornos.minecraft.player.getPos().y) {
                Cornos.minecraft.options.keySneak.setPressed(true);
                Cornos.minecraft.options.keyJump.setPressed(false);
            } else if ((block.y + 1) > Cornos.minecraft.player.getPos().y) {
                Cornos.minecraft.options.keySneak.setPressed(false);
                Cornos.minecraft.options.keyJump.setPressed(true);
            } else {
                Cornos.minecraft.options.keySneak.setPressed(false);
                Cornos.minecraft.options.keyJump.setPressed(false);
            }

            Cornos.minecraft.options.keyForward.setPressed(true);
            Cornos.minecraft.player.getAbilities().flying = true;
            if (Cornos.minecraft.crosshairTarget instanceof BlockHitResult) {
                BlockHitResult bhr = (BlockHitResult) Cornos.minecraft.crosshairTarget;
                if (bhr.getBlockPos().getX() == block.x && bhr.getBlockPos().getY() == block.y
                        && bhr.getBlockPos().getZ() == block.z) {
                    assert Cornos.minecraft.interactionManager != null;
                    Cornos.minecraft.interactionManager.attackBlock(bhr.getBlockPos(), Direction.DOWN);
                }
            }
        }
        if (!interacted) {
            Notification.create("ChunkClearer", new String[]{"done"}, 5000);
            this.setEnabled(false);
        }
        super.onExecute();
    }

    @Override
    public void onDisable() {
        KeyBinding.unpressAll();
        KeyBinding.updatePressedStates();
        super.onDisable();
    }
}
