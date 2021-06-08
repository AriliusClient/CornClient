package me.zeroX150.cornos.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.AntiBlockban;
import me.zeroX150.cornos.features.module.impl.external.Chams;
import me.zeroX150.cornos.features.module.impl.external.NameTags;
import me.zeroX150.cornos.features.module.impl.external.NoRender;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Inject(at = {@At("HEAD")}, method = "renderLabelIfPresent", cancellable = true)
    private void onRenderLabel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                               int light, CallbackInfo ci) {
        if (entity.getCustomName() != null
                && entity.getCustomName().equals(Text.of("DoNotRenderThisUsernameISwearToGod"))) {
            ci.cancel();
            return;
        }
        // GL11.glDisable(GL11.GL_LIGHTING);
        if (entity instanceof PlayerEntity) {
            if (Cornos.friendsManager.getFriends().containsKey(text.asString())) {
                text = Text.of("\u00A79" + Cornos.friendsManager.getFriends().get(text.asString()).getFakeName());
            }
            if (ModuleRegistry.search(NameTags.class).isEnabled()) {
                ((NameTags) ModuleRegistry.search(NameTags.class)).renderCustomLabel(entity, text, matrices,
                        vertexConsumers, light, dispatcher);
                ci.cancel();
            }
        }
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void sR(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType().equals(EntityType.AREA_EFFECT_CLOUD)
                && ModuleRegistry.search(AntiBlockban.class).isEnabled())
            cir.setReturnValue(false);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity.getType().equals(EntityType.AREA_EFFECT_CLOUD)
                && ModuleRegistry.search(AntiBlockban.class).isEnabled())
            ci.cancel();
        if (entity.getType().equals(EntityType.ITEM_FRAME) && NoRender.maps.isEnabled() && ModuleRegistry.search(NoRender.class).isEnabled()) {
            ItemFrameEntity current = (ItemFrameEntity) entity;
            if (current.getHeldItemStack().getItem() == Items.FILLED_MAP) {
                ci.cancel();
            }
        }
        if (ModuleRegistry.search(Chams.class).isEnabled()) {
            GlStateManager._enablePolygonOffset();
            GlStateManager._polygonOffset(1, -1500000);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render1(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ModuleRegistry.search(Chams.class).isEnabled()) {
            GlStateManager._disablePolygonOffset();
            GlStateManager._polygonOffset(1, 1500000);
        }
    }
}
