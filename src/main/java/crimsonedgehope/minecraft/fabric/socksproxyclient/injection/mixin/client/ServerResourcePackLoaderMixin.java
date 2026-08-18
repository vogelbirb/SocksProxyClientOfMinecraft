package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.client;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ServerConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.HttpProxyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.User;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.server.packs.DownloadQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.net.Proxy;

@Environment(EnvType.CLIENT)
@Mixin(DownloadedPackSource.class)
public class ServerResourcePackLoaderMixin {
    @ModifyArg(
            method = "createDownloader",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/server/DownloadedPackSource$4;<init>(Lnet/minecraft/client/resources/server/DownloadedPackSource;Lnet/minecraft/client/User;Lnet/minecraft/server/packs/DownloadQueue;Ljava/net/Proxy;Ljava/util/concurrent/Executor;)V"
            ),
            index = 3
    )
    private Proxy redirectedGet(DownloadedPackSource source, User user, DownloadQueue queue, Proxy instance) {
        return HttpProxyUtils.getProxyObject(ServerConfig.shouldProxyServerResourceDownload());
    }
}
