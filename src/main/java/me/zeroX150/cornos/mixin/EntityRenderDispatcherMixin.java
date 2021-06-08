package me.zeroX150.cornos.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.Chams;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ModuleRegistry.search(Chams.class).isEnabled()) {
            GlStateManager._enablePolygonOffset();
            GlStateManager._polygonOffset(1, -1500000);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public <E extends Entity> void render1(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ModuleRegistry.search(Chams.class).isEnabled()) {
            GlStateManager._disablePolygonOffset();
            GlStateManager._polygonOffset(1, 1500000);
        }
    }
}
