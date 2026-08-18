package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Objects;

@Environment(EnvType.CLIENT)
final class YACLAccess {
    private final Screen parentScreen;
    private final YetAnotherConfigLib.Builder configBuilder;
    private YetAnotherConfigLib yacl;
    private Screen generatedScreen;

    YACLAccess(Screen parentScreen, Component title) {
        this.parentScreen = parentScreen;
        this.configBuilder = YetAnotherConfigLib.createBuilder().title(title);
    }

    public YetAnotherConfigLib buildYacl() {
        if (Objects.isNull(yacl)) {
            yacl = configBuilder.build();
        }
        return yacl;
    }

    public Screen generateScreen() {
        if (Objects.isNull(generatedScreen)) {
            generatedScreen = buildYacl().generateScreen(parentScreen);
        }
        return generatedScreen;
    }

    public Screen getParentScreen() {
        return this.parentScreen;
    }

    public YetAnotherConfigLib.Builder getConfigBuilder() {
        return this.configBuilder;
    }

    public YetAnotherConfigLib getYacl() {
        return this.yacl;
    }

    public Screen getGeneratedScreen() {
        return this.generatedScreen;
    }
}
