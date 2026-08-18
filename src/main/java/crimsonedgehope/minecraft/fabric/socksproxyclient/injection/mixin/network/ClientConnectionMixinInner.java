package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.SocksApply;
import io.netty.channel.Channel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.network.Connection$1")
public class ClientConnectionMixinInner {
    @Shadow(remap = false) @Final
    private Connection val$connection;

    @Inject(method = "initChannel", at = @At("HEAD"))
    private void injected(Channel channel, CallbackInfo ci) {
        SocksApply.fire(this.val$connection, channel.pipeline());
    }
}
