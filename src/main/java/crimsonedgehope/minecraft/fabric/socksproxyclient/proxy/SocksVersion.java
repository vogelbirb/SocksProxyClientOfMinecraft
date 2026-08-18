package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy;

public enum SocksVersion {
    SOCKS4("Socks 4", 4),
    SOCKS5("Socks 5", 5);
    public final String desc;
    public final int ver;

    SocksVersion(String desc, int ver) {
        this.desc = desc;
        this.ver = ver;
    }
}