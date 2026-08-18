package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ConfigUtils;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.SocksProxyClientConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class YACLConfigScreen {
    private YACLConfigScreen() {
    }
    public static Screen getScreen(Screen parent) throws Exception {
        YACLAccess yacl = new YACLAccess(parent, Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG));
        GeneralCategory general = new GeneralCategory(yacl);
        ServerCategory server = new ServerCategory(yacl);
        MiscellaneousCategory miscellaneous = new MiscellaneousCategory(yacl);
        yacl.getConfigBuilder()
                .category(general.buildConfigCategory())
                .category(server.buildConfigCategory())
                .category(miscellaneous.buildConfigCategory())
                .save(() -> {
                    try {
                        ConfigUtils.saveAllConfig();
                    } catch (Throwable e) {
                        SocksProxyClientConfig.LOGGER.error("There's something wrong whilst saving config!", e);
                    }
                });
        yacl.buildYacl();
        yacl.generateScreen();
        return yacl.getGeneratedScreen();
    }
}
