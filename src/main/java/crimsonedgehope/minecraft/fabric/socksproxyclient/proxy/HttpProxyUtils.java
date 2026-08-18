package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy;

import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;

import java.net.Proxy;

public final class HttpProxyUtils {

    private HttpProxyUtils() {
    }

    public static Proxy getProxyObject() {
        return getProxyObject(true);
    }

    public static Proxy getProxyObject(boolean useProxy) {
        SocksProxyClient.logger("HttpProxy").debug("getProxyObject: {}", useProxy);
        if (!useProxy || !HttpProxy.INSTANCE.isFired()) {
            return Proxy.NO_PROXY;
        } else {
            return new Proxy(Proxy.Type.HTTP, HttpProxy.INSTANCE.getChannel().localAddress());
        }
    }

}
