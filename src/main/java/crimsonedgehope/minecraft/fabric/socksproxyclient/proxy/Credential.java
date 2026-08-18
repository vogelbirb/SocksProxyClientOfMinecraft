package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy;

import org.jetbrains.annotations.Nullable;

public class Credential {
    @Nullable private String username;
    @Nullable private String password;

    public Credential(@Nullable String username, @Nullable String password) {
        this.username = username;
        this.password = password;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }
}
