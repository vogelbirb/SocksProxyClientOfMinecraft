package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.screen;

import com.google.common.net.HostAndPort;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.Credential;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.SocksVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.net.IDN;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ProxyEntryEditScreen extends Screen {
    private final Screen parent;

    @Nullable
    private ProxyEntry entry;

    private CycleButton<SocksVersion> setSocksVersionButton;
    /* Host and port */
    private EditBox proxyAddressField;
    private EditBox usernameField;
    private EditBox passwordField;
    private Button setButton;

    private final Runnable callback;

    public ProxyEntryEditScreen(Screen parent, @Nullable ProxyEntry entry, @Nullable Runnable callback) {
        super(Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY));
        this.parent = parent;
        this.entry = entry;
        this.callback = callback;
    }

    @Override
    protected void init() {
        this.proxyAddressField = new EditBox(this.font, this.width / 2 - 100, 46, 200, 20,
                Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PROXYADDRESS));
        this.proxyAddressField.setMaxLength(262);
        this.proxyAddressField.setValue(Objects.isNull(entry) ? "" : ((InetSocketAddress) entry.getProxy().address()).getHostString() + ":" + ((InetSocketAddress) entry.getProxy().address()).getPort());
        this.proxyAddressField.setResponder(s -> updateSetButton());
        this.addWidget(this.proxyAddressField);

        this.usernameField = new EditBox(this.font, this.width / 2 - 100, 86, 200, 20,
                Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_USERNAME));
        this.usernameField.setMaxLength(255);
        this.usernameField.setValue(Objects.isNull(entry) ? "" : entry.getCredential().getUsername());
        this.usernameField.setResponder(s -> updateSetButton());
        this.addWidget(this.usernameField);

        this.passwordField = new EditBox(this.font, this.width / 2 - 100, 126, 200, 20,
                Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PASSWORD));
        this.passwordField.setMaxLength(255);
        this.passwordField.setValue(Objects.isNull(entry) ? "" : entry.getCredential().getPassword());
        this.passwordField.setResponder(s -> updateSetButton());
        this.addWidget(this.passwordField);

        this.setSocksVersionButton = this.addRenderableWidget(
                CycleButton.<SocksVersion>builder(o -> Component.literal(o.toString()), SocksVersion.SOCKS5)
                        .withValues(SocksVersion.values())
                        .create(this.width / 2 - 100, this.height / 4 + 92 + 18, 200, 20,
                                Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_SOCKSVERSION),
                                (button, socksVersion) -> {
                                    entry.setVersion(socksVersion);
                                    this.passwordField.active = !socksVersion.equals(SocksVersion.SOCKS4);
                                }));
        this.setSocksVersionButton.setValue(Objects.nonNull(entry) ? entry.getVersion() : SocksVersion.SOCKS5);

        this.setButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> setAndClose())
                .bounds(this.width / 2 - 100, this.height / 4 + 116 + 18, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height / 4 + 140 + 18, 200, 20).build());

        this.updateSetButton();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.proxyAddressField);
    }

    @Override
    public void resize(int width, int height) {
        String string = this.proxyAddressField.getValue();
        String string2 = this.usernameField.getValue();
        String string3 = this.passwordField.getValue();
        this.init();
        this.proxyAddressField.setValue(string);
        this.usernameField.setValue(string2);
        this.passwordField.setValue(string3);
    }

    @Override
    public void onClose() {
        if (Objects.nonNull(this.callback)) {
            this.callback.run();
        }
        this.minecraft.setScreenAndShow(this.parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 17, 16777215);
        context.text(this.font, Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PROXYADDRESS), this.width / 2 - 100 + 1, 33, 10526880);
        context.text(this.font, Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_USERNAME), this.width / 2 - 100 + 1, 74, 10526880);
        context.text(this.font, Component.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PASSWORD), this.width / 2 - 100 + 1, 115, 10526880);
        this.proxyAddressField.extractRenderState(context, mouseX, mouseY, delta);
        this.usernameField.extractRenderState(context, mouseX, mouseY, delta);
        this.passwordField.extractRenderState(context, mouseX, mouseY, delta);
    }

    private void setAndClose() {
        HostAndPort hostAndPort = HostAndPort.fromString(this.proxyAddressField.getValue());
        if (Objects.isNull(entry)) {
            entry = new ProxyEntry(
                    this.setSocksVersionButton.getValue(),
                    InetSocketAddress.createUnresolved(hostAndPort.getHost(), hostAndPort.getPort()),
                    this.usernameField.getValue(),
                    this.passwordField.getValue());
        } else {
            entry.setVersion(this.setSocksVersionButton.getValue());
            entry.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostAndPort.getHost(), hostAndPort.getPort())));
            entry.setCredential(new Credential(this.usernameField.getValue(), this.passwordField.getValue()));
        }
        this.onClose();
    }

    private void updateSetButton() {
        this.setButton.active = ((Supplier<Boolean>) () -> {
            try {
                HostAndPort hostAndPort = HostAndPort.fromString(this.proxyAddressField.getValue()).withDefaultPort(0);
                String string = hostAndPort.getHost();
                int port = hostAndPort.getPort();
                if (!string.isEmpty() && port > 0 && port <= 65535) {
                    IDN.toASCII(string);
                    return true;
                }
            } catch (Exception e) {
                // No op
            }
            return false;
        }).get();
    }
}
