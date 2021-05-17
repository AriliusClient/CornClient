package me.constantindev.ccl.mixin.gui;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.gui.screen.MainScreen;
import me.constantindev.ccl.gui.widget.CustomButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    public TitleScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "init", cancellable = true, at = @At("HEAD"))
    public void init(CallbackInfo cbi) {
        if (Cornos.config.mconf.getByName("homescreen").value.equals("client")) {
            Cornos.minecraft.openScreen(new MainScreen());
            cbi.cancel();
            return;
        }
        this.addButton(new CustomButtonWidget(width - 121, 1, 120, 20, Text.of("Return to pog menu"), () -> {
            Cornos.config.mconf.getByName("homescreen").setValue("client");
            Cornos.minecraft.openScreen(this);
        }));
    }
}
