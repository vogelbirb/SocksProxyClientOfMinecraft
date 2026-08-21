package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.MiscellaneousConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.YACLConfigScreen;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(JoinMultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 2
            )
    )
    private LayoutElement injected(LinearLayout row, LayoutElement element) {
        row.addChild(element);
        if (MiscellaneousConfig.showButtonsInMultiplayerScreen()) {
            row.addChild(Button.builder(
                    Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_SCREEN_CONFIG),
                    button -> {
                        try {
                            Minecraft.getInstance().setScreenAndShow(YACLConfigScreen.getScreen((JoinMultiplayerScreen) (Object) this));
                        } catch (Exception e) {
                            SocksProxyClient.logger(this.getClass().getSimpleName()).error("Where's my config screen?", e);
                            button.active = false;
                        }
                    }).width(98).build());
        }
        return row;
    }
}
