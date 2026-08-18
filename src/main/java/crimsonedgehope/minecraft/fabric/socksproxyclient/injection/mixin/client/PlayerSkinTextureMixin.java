package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.client;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ServerConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.HttpProxyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.net.Proxy;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class PlayerSkinTextureMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/SkinTextureDownloader;<init>(Ljava/net/Proxy;Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/util/concurrent/Executor;)V"
            ),
            index = 0
    )
    private Proxy redirectedGet(Proxy instance) {
        return HttpProxyUtils.getProxyObject(ServerConfig.shouldProxyPlayerSkinDownload());
    }
}
