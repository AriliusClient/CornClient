/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: MPScreenMixin
# Created by constantin at 12:38, Mär 12 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.mixin.gui;

import me.zeroX150.cornos.Cornos;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;
    @Shadow
    @Final
    private Screen parent;
    @Shadow
    private boolean initialized;
    @Shadow
    private ServerList serverList;
    @Shadow
    private LanServerQueryManager.LanServerEntryList lanServers;
    @Shadow
    private LanServerQueryManager.LanServerDetector lanServerDetector;
    @Shadow
    private ButtonWidget buttonJoin;
    @Shadow
    private ServerInfo selectedEntry;
    @Shadow
    private ButtonWidget buttonEdit;
    @Shadow
    private ButtonWidget buttonDelete;
    @Shadow
    private List<Text> tooltipText;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    public abstract void connect();

    @Shadow
    protected abstract void directConnect(boolean confirmedAction);

    @Shadow
    protected abstract void addEntry(boolean confirmedAction);

    @Shadow
    protected abstract void removeEntry(boolean confirmedAction);

    @Shadow
    protected abstract void refresh();

    @Shadow
    protected abstract void updateButtonActivationStates();

    @Shadow
    protected abstract void editEntry(boolean confirmedAction);

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo ci) {
        if (Cornos.config.mconf.getByName("mpscreen").value.equalsIgnoreCase("vanilla"))
            return;
        ci.cancel();
        if (this.client == null)
            return;
        super.init();
        this.client.keyboard.setRepeatEvents(true);
        if (this.initialized) {
            this.serverListWidget.updateSize(320, this.height, 32, this.height - 32);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.client);
            this.serverList.loadFile();
            this.lanServers = new LanServerQueryManager.LanServerEntryList();

            try {
                this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
                this.lanServerDetector.start();
            } catch (Exception var2) {
                LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
            }

            this.serverListWidget = new MultiplayerServerListWidget((MultiplayerScreen) ((Object) this), this.client,
                    320, this.height, 32, this.height - 32, 36);
            this.serverListWidget.setServers(this.serverList);
        }
        this.addDrawableChild(this.serverListWidget);
        this.buttonJoin = this
                .addDrawableChild(new ButtonWidget(this.width - 105, this.height / 2 - 10 - 25 /* account for offset */, 100,
                        20, new TranslatableText("selectServer.select"), (buttonWidget) -> this.connect()));
        this.addDrawableChild(new ButtonWidget(this.width - 105, this.height / 2 - 10, 100, 20,
                new TranslatableText("selectServer.direct"), (buttonWidget) -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
            this.client.openScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
        }));
        this.addDrawableChild(new ButtonWidget(this.width - 105, this.height / 2 - 10 + 25, 100, 20,
                new TranslatableText("selectServer.add"), (buttonWidget) -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
            this.client.openScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
        }));
        this.buttonEdit = this.addDrawableChild(new ButtonWidget(this.width - 105, this.height / 2 - 10 - 25 - 25 - 25, 100,
                20, new TranslatableText("selectServer.edit"), (buttonWidget) -> {
            MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry) entry).getServer();
                this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
                this.selectedEntry.copyFrom(serverInfo);
                this.client.openScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
            }

        }));
        this.buttonDelete = this.addDrawableChild(new ButtonWidget(this.width - 105, this.height / 2 - 10 - 25 - 25, 100, 20,
                new TranslatableText("selectServer.delete"), (buttonWidget) -> {
            MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                String string = ((MultiplayerServerListWidget.ServerEntry) entry).getServer().name;
                if (string != null) {
                    Text text = new TranslatableText("selectServer.deleteQuestion");
                    Text text2 = new TranslatableText("selectServer.deleteWarning", string);
                    Text text3 = new TranslatableText("selectServer.deleteButton");
                    Text text4 = ScreenTexts.CANCEL;
                    this.client.openScreen(new ConfirmScreen(this::removeEntry, text, text2, text3, text4));
                }
            }

        }));
        this.addDrawableChild(new ButtonWidget(this.width - 105, this.height / 2 - 10 + 25 + 25, 100, 20,
                new TranslatableText("selectServer.refresh"), (buttonWidget) -> this.refresh()));
        this.addDrawableChild(new ButtonWidget(this.width - 105, this.height - 25, 100, 20, ScreenTexts.CANCEL,
                (buttonWidget) -> this.client.openScreen(this.parent)));
        this.updateButtonActivationStates();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        this.tooltipText = null;
        this.renderBackground(matrices);
        this.serverListWidget.render(matrices, mouseX, mouseY, delta);
        // drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20,
        // 16777215); // no title
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderTooltip(matrices, this.tooltipText, mouseX, mouseY);
        }
    }
}
