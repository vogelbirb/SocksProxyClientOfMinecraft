package crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.SocksProxyClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SocksProxyClientConfigEntry<T> {
    @NotNull private final Class<? extends SocksProxyClientConfig> configClass;

    @NotNull private final String jsonEntry;

    @Nullable private final T defaultValue;
    @Nullable private T value;

    @NotNull private final MutableComponent entryTranslateKey;
    @Nullable private final MutableComponent descriptionTranslateKey;

    public SocksProxyClientConfigEntry(
            @NotNull Class<? extends SocksProxyClientConfig> configClass,
            @NotNull String jsonEntry,
            @NotNull MutableComponent entryTranslateKey,
            @Nullable T defaultValue
    ) {
        this(configClass, jsonEntry, entryTranslateKey, null, defaultValue);
    }

    public SocksProxyClientConfigEntry(
            @NotNull Class<? extends SocksProxyClientConfig> configClass,
            @NotNull String jsonEntry,
            @NotNull MutableComponent entryTranslateKey,
            @Nullable MutableComponent descriptionTranslateKey,
            @Nullable T defaultValue
    ) {
        this.configClass = configClass;
        this.jsonEntry = jsonEntry;
        this.entryTranslateKey = entryTranslateKey;
        this.descriptionTranslateKey = descriptionTranslateKey;
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public Class<? extends SocksProxyClientConfig> getConfigClass() {
        return this.configClass;
    }

    public String getJsonEntry() {
        return this.jsonEntry;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public MutableComponent getEntryTranslateKey() {
        return this.entryTranslateKey;
    }

    public MutableComponent getDescriptionTranslateKey() {
        return this.descriptionTranslateKey;
    }

    public void setValue(@Nullable T value) {
        this.value = value;
    }
}
