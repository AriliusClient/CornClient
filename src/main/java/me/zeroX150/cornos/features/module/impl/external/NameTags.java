package me.zeroX150.cornos.features.module.impl.external;

import com.mojang.blaze3d.platform.GlStateManager;
import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import me.zeroX150.cornos.gui.screen.TabGUI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class NameTags extends Module {

    MConfToggleable health = new MConfToggleable("Health", true, "Whether or not to show the player's health");

    public NameTags() {
        super("NameTags", "big name labels", ModuleType.RENDER);
        this.mconf.add(health);
    }

    public void renderCustomLabel(Entity entity, Text text, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers, int light, EntityRenderDispatcher dispatcher) {
        double d = dispatcher.getSquaredDistanceToCamera(entity);
        if (d > 4096) {
            return;
        }
        String tag = entity.getEntityName();
        float f = entity.getHeight() + 0.5F;
        assert Cornos.minecraft.player != null;
        if (entity.getUuid() == Cornos.minecraft.player.getUuid()) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            if (Cornos.friendsManager.getFriends().containsKey(tag)) {
                tag = "\u00A79" + Cornos.friendsManager.getFriends().get(tag).getFakeName();
            }
            matrices.push();
            float scale = 3F;
            scale /= 50.0f;
            scale *= (float) 0.55;
            assert Cornos.minecraft.player != null;
            if (Cornos.minecraft.player.distanceTo(entity) > 10)
                scale *= Cornos.minecraft.player.distanceTo(entity) / 10;
            matrices.translate(0.0D, f + (scale * 6), 0.0D);
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-scale, -scale, scale);
            TextRenderer textRenderer = Cornos.minecraft.textRenderer;
            int health = (int) ((PlayerEntity) entity).getHealth();
            double healthPercentage = (((PlayerEntity) entity).getHealth() / ((PlayerEntity) entity).getMaxHealth());
            healthPercentage = MathHelper.clamp(healthPercentage, 0, 1);
            double red = Math.abs(healthPercentage - 1.0);
            Color c = new Color((int) MathHelper.clamp(255.0 * red, 0, 255),
                    (int) MathHelper.clamp(255.0 * healthPercentage, 0, 255), 0);
            if (health <= ((PlayerEntity) entity).getMaxHealth() * 0.25) { // If health is below 25%
                tag += "§4";
            } else if (health <= ((PlayerEntity) entity).getMaxHealth() * 0.5) { // If health is below 50%
                tag += "§6";
            } else if (health <= ((PlayerEntity) entity).getMaxHealth() * 0.75) { // If health is below 75%
                tag += "§e";
            } else if (health <= ((PlayerEntity) entity).getMaxHealth()) { // If health is below 100% (aka. everything
                // else)
                tag += "§2";
            }
            tag = this.health.isEnabled() ? tag + " " + health : tag;
            int width = textRenderer.getWidth(tag) / 2;
            GlStateManager._enablePolygonOffset();
            GlStateManager._polygonOffset(1f, -1500000);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            float size = .5f;
            Matrix4f matrix4f = matrices.peek().getModel();
            TabGUI.drawGuiRect(-width - 4, textRenderer.fontHeight + 2, width + 4, 1,
                    new Color(20, 20, 20, 220).getRGB(), matrix4f);
            TabGUI.drawGuiRect((float) ((-width - 4) * healthPercentage), textRenderer.fontHeight + 2,
                    (float) ((width + 4) * healthPercentage), textRenderer.fontHeight + 2 + size, c.getRGB(), matrix4f);
            Cornos.minecraft.textRenderer.draw(matrices, tag, -Cornos.minecraft.textRenderer.getWidth(tag) / 2F, f,
                    Color.WHITE.getRGB());
            GlStateManager._disablePolygonOffset();
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GlStateManager._polygonOffset(1f, 1500000);
            matrices.pop();
        }
    }
}