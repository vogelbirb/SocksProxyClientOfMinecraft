package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.MiscellaneousConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.YACLConfigScreen;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin {
    @Unique
    private static final int CONFIG_BUTTON_WIDTH = 98;
    @Unique
    private static final int CONFIG_BUTTON_MARGIN = 6;

    @Inject(method = "init", at = @At("TAIL"))
    private void socksProxyClient$addConfigButton(CallbackInfo ci) {
        if (!MiscellaneousConfig.showButtonsInMultiplayerScreen()) {
            return;
        }
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        Button configButton = Button.builder(
                Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_SCREEN_CONFIG),
                button -> {
                    try {
                        Minecraft.getInstance().setScreenAndShow(YACLConfigScreen.getScreen((JoinMultiplayerScreen) (Object) this));
                    } catch (Exception e) {
                        SocksProxyClient.logger(this.getClass().getSimpleName()).error("Where's my config screen?", e);
                        button.active = false;
                    }
                })
                .bounds(screenWidth - CONFIG_BUTTON_WIDTH - CONFIG_BUTTON_MARGIN, CONFIG_BUTTON_MARGIN, CONFIG_BUTTON_WIDTH, 20)
                .build();
        ((ScreenAccessor) this).invokeAddRenderableWidget(configButton);
    }
}
