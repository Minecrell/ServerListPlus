/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.velocity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.text.serializer.ComponentSerializers;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconHelper;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.logging.Slf4jServerListPlusLogger;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.FormattingCodes;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.Randoms;
import net.minecrell.serverlistplus.core.util.SnakeYAML;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(id = "serverlistplus")
public class VelocityPlugin implements ServerListPlusPlugin {
    private final Logger logger;

    private final ProxyServer proxy;
    private final Path pluginFolder;

    private ServerListPlusCore core;
    private EventHandler<ProxyPingEvent> pingListener;
    private Object connectionListener;

    // Favicon cache
    private final CacheLoader<FaviconSource, Optional<Favicon>> faviconLoader =
            new CacheLoader<FaviconSource, Optional<Favicon>>() {
                @Override
                public Optional<Favicon> load(FaviconSource source) throws Exception {
                    // Try loading the favicon
                    BufferedImage image = FaviconHelper.loadSafely(core, source);
                    if (image == null) return Optional.empty(); // Favicon loading failed
                    else return Optional.of(Favicon.create(image)); // Success!
                }
            };
    private LoadingCache<FaviconSource, Optional<Favicon>> faviconCache;

    @Inject
    public VelocityPlugin(Logger logger, ProxyServer proxy, @DataDirectory Path pluginFolder) {
        this.logger = logger;
        this.proxy = proxy;
        this.pluginFolder = pluginFolder;
    }

    @Subscribe
    public void initialize(ProxyInitializeEvent event) {
        try {
            this.proxy.getPluginManager().addToClasspath(this, SnakeYAML.load(this.pluginFolder));
        } catch (Exception e) {
            logger.error("Failed to load snakeyaml dependency", e);
            return;
        }

        try { // Load the core first
            this.core = new ServerListPlusCore(this);
            logger.info("Successfully loaded!");
        } catch (ServerListPlusException e) {
            logger.info("Please fix the error before restarting the server!");
            return;
        } catch (Exception e) {
            logger.error("An internal error occurred while loading the core.", e);
            return;
        }

        // Register commands
        this.proxy.getCommandManager().register(new ServerListPlusCommand(), "serverlistplus",
                "serverlist+", "serverlist", "slp", "sl+", "s++", "serverping+", "serverping", "spp", "slus");
    }

    @Subscribe
    public void shutdown(ProxyShutdownEvent event) {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    // Commands
    public final class ServerListPlusCommand implements Command {
        private ServerListPlusCommand() {}

        @Override
        public void execute(@Nonnull CommandSource source, @Nonnull String[] args) {
            core.executeCommand(new VelocityCommandSender(proxy, source), "serverlistplus", args);
        }

        @Override
        public List<String> suggest(@Nonnull CommandSource source, @Nonnull String[] currentArgs) {
            return core.tabComplete(new VelocityCommandSender(proxy, source), "serverlistplus", currentArgs);
        }

    }

    // Player tracking
    public final class ConnectionListener {
        private ConnectionListener() {}

        @Subscribe
        public void onPlayerLogin(LoginEvent event) {
            handleConnection(event.getPlayer());
        }

        @Subscribe
        public void onPlayerLogout(DisconnectEvent event) {
            handleConnection(event.getPlayer());
        }

        private void handleConnection(Player player) {
            if (core == null) return; // Too early, we haven't finished initializing yet
            core.updateClient(player.getRemoteAddress().getAddress(), player.getUniqueId(), player.getUsername());
        }
    }

    // Status listener
    public final class PingListener implements EventHandler<ProxyPingEvent> {
        private PingListener() {}

        @Override
        public void execute(ProxyPingEvent event) {
            if (core == null) return; // Too early, we haven't finished initializing yet

            InboundConnection con = event.getConnection();
            StatusRequest request = core.createRequest(con.getRemoteAddress().getAddress());

            request.setProtocolVersion(con.getProtocolVersion().getProtocol());
            con.getVirtualHost().ifPresent(request::setTarget);

            final ServerPing ping = event.getPing();
            final ServerPing.Players players = ping.getPlayers().orElse(null);
            final ServerPing.Version version = ping.getVersion();

            StatusResponse response = request.createResponse(core.getStatus(),
                    // Return unknown player counts if it has been hidden
                    new ResponseFetcher() {
                        @Override
                        public Integer getOnlinePlayers() {
                            return players != null ? players.getOnline() : null;
                        }

                        @Override
                        public Integer getMaxPlayers() {
                            return players != null ? players.getMax() : null;
                        }

                        @Override
                        public int getProtocolVersion() {
                            return version != null ? version.getProtocol() : 0;
                        }
                    });

            ServerPing.Builder builder = ping.asBuilder();

            // Description
            String message = response.getDescription();
            if (message != null) builder.description(ComponentSerializers.LEGACY.deserialize(message));

            if (version != null) {
                // Version name
                message = response.getVersion();
                // Protocol version
                Integer protocol = response.getProtocolVersion();

                if (message != null || protocol != null) builder.version(new ServerPing.Version(
                        protocol != null ? protocol : version.getProtocol(),
                        message != null ? message : version.getName()
                ));
            }

            if (players != null) {
                if (response.hidePlayers()) {
                    builder.nullPlayers();
                } else {
                    // Online players
                    Integer count = response.getOnlinePlayers();
                    if (count != null) builder.onlinePlayers(count);
                    // Max players
                    count = response.getMaxPlayers();
                    if (count != null) builder.maximumPlayers(count);

                    // Player hover
                    message = response.getPlayerHover();
                    if (message != null) {
                        builder.clearSamplePlayers();

                        if (!message.isEmpty()) {
                            if (response.useMultipleSamples()) {
                                count = response.getDynamicSamples();
                                List<String> lines = count != null ? Helper.splitLinesCached(message, count) :
                                        Helper.splitLinesCached(message);

                                ServerPing.SamplePlayer[] sample = new ServerPing.SamplePlayer[lines.size()];
                                for (int i = 0; i < sample.length; i++)
                                    sample[i] = new ServerPing.SamplePlayer(lines.get(i), StatusManager.EMPTY_UUID);

                                builder.samplePlayers(sample);
                            } else
                                builder.samplePlayers(new ServerPing.SamplePlayer(message, StatusManager.EMPTY_UUID));
                        }
                    }
                }
            }

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon != null) {
                Optional<Favicon> icon = faviconCache.getUnchecked(favicon);
                icon.ifPresent(builder::favicon);
            }

            event.setPing(builder.build());
        }
    }

    @Override
    public ServerListPlusCore getCore() {
        return core;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
    }

    @Override
    public String getServerImplementation() {
        return "Velocity"; // TODO
        //return getProxy().getVersion() + " (MC: " + getProxy().getGameVersion() + ')';
    }

    @Override
    public Path getPluginFolder() {
        return this.pluginFolder;
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        Optional<RegisteredServer> server = proxy.getServer(location);
        if (!server.isPresent()) {
            return null;
        }

        return server.get().getPlayersConnected().size();
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        Collection<Player> players = this.proxy.getAllPlayers();
        if (Helper.isNullOrEmpty(players)) return null;

        List<String> result = new ArrayList<>(players.size());
        for (Player player : players) {
            result.add(player.getUsername());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Iterator<String> getRandomPlayers(String location) {
        Optional<RegisteredServer> server = proxy.getServer(location);
        if (!server.isPresent()) {
            return null;
        }

        ArrayList<String> result = new ArrayList<>();
        for (Player player : server.get().getPlayersConnected()) {
            result.add(player.getUsername());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Cache<?, ?> getRequestCache() {
        return null;
    }

    @Override
    public LoadingCache<FaviconSource, Optional<Favicon>> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public void runAsync(Runnable task) {
        proxy.getScheduler().buildTask(this, task).schedule();
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return new ScheduledVelocityTask(proxy.getScheduler()
                .buildTask(this, task)
                .delay((int) repeat, unit)
                .repeat((int) repeat, unit)
                .schedule());
    }

    @Override
    public String colorize(String s) {
        return FormattingCodes.colorize(s);
    }

    @Override
    public ServerListPlusLogger createLogger(ServerListPlusCore core) {
        return new Slf4jServerListPlusLogger(core, this.logger);
    }

    @Override
    public void initialize(ServerListPlusCore core) {

    }

    @Override
    public void reloadCaches(ServerListPlusCore core) {

    }

    @Override
    public void reloadFaviconCache(CacheBuilderSpec spec) {
        if (spec != null) {
            this.faviconCache = CacheBuilder.from(spec).build(faviconLoader);
        } else {
            // Delete favicon cache
            faviconCache.invalidateAll();
            faviconCache.cleanUp();
            this.faviconCache = null;
        }
    }

    @Override
    public void configChanged(ServerListPlusCore core, InstanceStorage<Object> confs) {
        // Player tracking
        if (confs.get(PluginConf.class).PlayerTracking.Enabled) {
            if (connectionListener == null) {
                this.proxy.getEventManager().register(this, this.connectionListener = new ConnectionListener());
                logger.debug("Registered player tracking listener.");
            }
        } else if (connectionListener != null) {
            this.proxy.getEventManager().unregisterListener(this, connectionListener);
            this.connectionListener = null;
            logger.debug("Unregistered player tracking listener.");
        }
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // Status listener
        if (hasChanges) {
            if (pingListener == null) {
                this.proxy.getEventManager().register(this, ProxyPingEvent.class, this.pingListener = new PingListener());
                logger.debug("Registered ping listener.");
            }
        } else if (pingListener != null) {
            this.proxy.getEventManager().unregister(this, this.pingListener);
            this.pingListener = null;
            logger.debug("Unregistered ping listener.");
        }
    }

}
