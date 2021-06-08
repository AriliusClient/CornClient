package me.zeroX150.cornos.features.module.impl.render;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfMultiOption;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Animations extends Module {
    private static final MConfMultiOption type = new MConfMultiOption("type", "laidBack",
            new String[]{"laidBack", "small", "smaller", "custom"}, "mode");
    private static final MConfNum customOffX = new MConfNum("customX", 0, 5, -5, "(custom mode only) x location modifier");
    private static final MConfNum customOffY = new MConfNum("customY", 0, 5, -5, "(custom mode only) y location modifier");
    private static final MConfNum customOffZ = new MConfNum("customZ", 0, 5, -5, "(custom mode only) z location modifier");
    private static final MConfNum customScale = new MConfNum("customScale", 1, 5, 0, "(custom mode only) scale modifier");
    private static final MConfNum customRotX = new MConfNum("customMX", 0, 5, -5, "(custom mode only) x multiplication modifier");
    private static final MConfNum customRotY = new MConfNum("customMY", 0, 5, -5, "(custom mode only) y multiplication modifier");
    private static final MConfNum customRotZ = new MConfNum("customMZ", 0, 5, -5, "(custom mode only) z multiplication modifier");

    public Animations() {
        super("Animations", "The item in your hand shrunk", ModuleType.RENDER);
        this.mconf.add(type);
        this.mconf.add(customOffX);
        this.mconf.add(customOffY);
        this.mconf.add(customOffZ);
        this.mconf.add(customRotX);
        this.mconf.add(customRotY);
        this.mconf.add(customRotZ);
        this.mconf.add(customScale);
    }

    public static void render(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode,
                              boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                              CallbackInfo ci, ItemRenderer itemRenderer) {
        assert Cornos.minecraft.player != null;
        if (entity.getUuid() == Cornos.minecraft.player.getUuid()
                && Cornos.minecraft.options.getPerspective() == Perspective.FIRST_PERSON
                && ModuleRegistry.search(Animations.class).isEnabled()) {
            switch (type.value) {
                case "laidBack":
                    matrices.multiply(new Quaternion(0.3f, 0f, 0f, 1f));
                    break;
                case "small":
                    matrices.scale(.2f, .2f, .2f);
                    matrices.translate(0, 1, 0);
                    break;
                case "smaller":
                    matrices.scale(.05f, .05f, .05f);
                    matrices.translate(0, 3, 0);
                    break;
                case "custom":
                    matrices.scale((float) customScale.getValue(), (float) customScale.getValue(),
                            (float) customScale.getValue());
                    matrices.translate(customOffX.getValue(), customOffY.getValue(), customOffZ.getValue());
                    matrices.multiply(new Quaternion((float) customRotX.getValue(), (float) customRotY.getValue(),
                            (float) customRotZ.getValue(), 1f));
                    break;
                default:
                    return;
            }

        }
        if (!stack.isEmpty()) {
            itemRenderer.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV, entity.getId() + renderMode.ordinal());
        }
        ci.cancel();
    }
}
