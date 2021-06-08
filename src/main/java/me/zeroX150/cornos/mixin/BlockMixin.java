package me.zeroX150.cornos.mixin;

import me.zeroX150.cornos.etc.config.CConf;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.XRAY;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "shouldDrawSide", cancellable = true, at = @At("HEAD"))
    private static void checkBlock(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleRegistry.search(XRAY.class).isEnabled()) {
            boolean isIncluded = false;
            for (Block b : CConf.xrayBlocks) {
                if (state.getBlock() == (b))
                    isIncluded = true;
            }
            cir.setReturnValue(isIncluded);
        }
    }

    @Inject(method = "isTranslucent", cancellable = true, at = @At("HEAD"))
    public void checkTranslucent(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleRegistry.search(XRAY.class).isEnabled()) {
            boolean isIncluded = false;
            for (Block b : CConf.xrayBlocks) {
                if (state.getBlock() == (b))
                    isIncluded = true;
            }
            cir.setReturnValue(!isIncluded);
        }
    }

}
