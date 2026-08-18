package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IClientConnectionMixin;
import io.netty.channel.ChannelFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Connection;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;

@Environment(EnvType.CLIENT)
@Mixin(Connection.class)
public class ClientConnectionMixin implements IClientConnectionMixin {
    @Unique
    private InetSocketAddress remote;

    @Override
    public InetSocketAddress socksProxyClient$getInetSocketAddress() {
        return remote;
    }

    @Override
    public void socksProxyClient$setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.remote = inetSocketAddress;
    }

    @Inject(
            method = "connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;",
            at = @At("HEAD")
    )
    private static void injected(InetSocketAddress address, EventLoopGroupHolder eventLoopGroupHolder, Connection connection, CallbackInfoReturnable<ChannelFuture> cir) {
        ((IClientConnectionMixin) connection).socksProxyClient$setInetSocketAddress(address);
        SocksProxyClient.logger("Connect").debug("Remote Minecraft server {}", address);
    }
}
