package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import com.google.common.net.InetAddresses;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ServerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddressResolver;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.client.multiplayer.resolver.ServerRedirectHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DohResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.hosts.HostsFileParser;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ServerNameResolver.class)
public abstract class ServerNameResolverMixin {
    @Unique
    private static final Logger LOGGER = SocksProxyClient.logger("Resolve");

    @Unique
    private static DohResolver dohResolver;

    @Redirect(
            method = "resolveAddress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/resolver/ServerAddressResolver;resolve(Lnet/minecraft/client/multiplayer/resolver/ServerAddress;)Ljava/util/Optional;",
                    ordinal = 0
            )
    )
    private Optional<ResolvedServerAddress> socksProxyClient$redirectedResolve(ServerAddressResolver instance, ServerAddress serverAddress) {
        if (!ServerConfig.minecraftRemoteResolve()) {
            return instance.resolve(serverAddress);
        }
        return socksProxyClient$resolveAddress(serverAddress);
    }

    @Redirect(
            method = "resolveAddress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/resolver/ServerRedirectHandler;lookupRedirect(Lnet/minecraft/client/multiplayer/resolver/ServerAddress;)Ljava/util/Optional;"
            )
    )
    private Optional<ServerAddress> socksProxyClient$redirectedRedirect(ServerRedirectHandler instance, ServerAddress serverAddress) {
        if (!ServerConfig.minecraftRemoteResolve() || serverAddress.getPort() != 25565) {
            return instance.lookupRedirect(serverAddress);
        }
        try {
            if (InetAddresses.isInetAddress(serverAddress.getHost())) {
                return Optional.of(serverAddress);
            }
            return socksProxyClient$resolveRedirect(serverAddress);
        } catch (Throwable e) {
            LOGGER.debug("Couldn't resolve server {} redirect", serverAddress.getHost(), e);
        }
        return Optional.empty();
    }

    @Unique
    private static Optional<ResolvedServerAddress> socksProxyClient$resolveAddress(ServerAddress serverAddress) {
        try {
            if (InetAddresses.isInetAddress(serverAddress.getHost())) {
                InetAddress inet = InetAddress.getByName(serverAddress.getHost());
                return Optional.of(ResolvedServerAddress.from(new InetSocketAddress(inet, serverAddress.getPort())));
            }
            Record[] records = socksProxyClient$resolver(serverAddress.getHost(), Type.A);
            final ARecord arec = (ARecord) records[0];
            InetAddress inetAddress = arec.getAddress();
            LOGGER.info("Resolve {} to {}", serverAddress.getHost(), inetAddress.getHostAddress());
            return Optional.of(ResolvedServerAddress.from(new InetSocketAddress(inetAddress, serverAddress.getPort())));
        } catch (Throwable e) {
            LOGGER.debug("Couldn't resolve server {} address", serverAddress.getHost(), e);
        }
        return Optional.empty();
    }

    @Unique
    private static Optional<ServerAddress> socksProxyClient$resolveRedirect(ServerAddress serverAddress) throws Exception {
        String addr0 = "_minecraft._tcp." + serverAddress.getHost();
        Record[] records = socksProxyClient$resolver(addr0, Type.SRV);
        final SRVRecord srv = (SRVRecord) records[0];
        LOGGER.debug("{}", records[0]);
        String host = srv.getTarget().toString(true);
        LOGGER.info("Resolve {} to {}:{}", addr0, host, srv.getPort());
        return Optional.of(new ServerAddress(host, srv.getPort()));
    }

    @Unique
    private static Record[] socksProxyClient$resolver(final String domainName, final int recordType) throws Exception {
        if (Objects.isNull(dohResolver)) {
            dohResolver = new DohResolver(ServerConfig.minecraftRemoteResolveProviderUrl(), 2, Duration.ofSeconds(2L));
            dohResolver.setUsePost(true);
        }
        Lookup lookup;
        lookup = new Lookup(domainName, recordType);
        lookup.setResolver(dohResolver);
        if (ServerConfig.minecraftRemoteResolveDismissSystemHosts()) {
            lookup.setHostsFileParser(null);
        } else {
            lookup.setHostsFileParser(new HostsFileParser());
        }
        lookup.run();
        Record[] records = lookup.getAnswers();
        if (records == null || records.length <= 0) {
            throw new UnknownHostException();
        }
        return records;
    }
}
