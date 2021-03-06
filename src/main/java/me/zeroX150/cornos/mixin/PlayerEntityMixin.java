package me.zeroX150.cornos.mixin;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.Safewalk;
import me.zeroX150.cornos.features.module.impl.world.Scaffold;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    public void cAL(CallbackInfoReturnable<Boolean> cir) {
        // shut up stinky
        PlayerEntity context = (PlayerEntity) ((Object) this);
        if (Cornos.minecraft.player == null)
            return;
        if (context.getUuid() == Cornos.minecraft.player.getUuid()) {
            if ((Scaffold.preventFalling.isEnabled() && ModuleRegistry.search(Scaffold.class).isEnabled())
                    || ModuleRegistry.search(Safewalk.class).isEnabled()) {
                cir.setReturnValue(true);
            }
        }
    }
}
