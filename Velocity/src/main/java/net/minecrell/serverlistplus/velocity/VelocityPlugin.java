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
import com.google.common.cache.CacheBuilderSpec;
import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
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
import com.velocitypowered.api.util.ProxyVersion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconCache;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.logging.Slf4jServerListPlusLogger;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.FormattingCodes;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.Randoms;
import net.minecrell.serverlistplus.core.util.UUIDs;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(id = "serverlistplus", name = "ServerListPlus", version = "%version%",
    description = "%description%", url = "%url%", authors = {"%author%"})
public class VelocityPlugin implements ServerListPlusPlugin {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().build();

    private final Logger logger;

    private final ProxyServer proxy;
    private final Path pluginFolder;

    private ServerListPlusCore core;
    private EventHandler<ProxyPingEvent> pingListener;
    private Object connectionListener;

    private FaviconCache<Favicon> faviconCache;

    @Inject
    public VelocityPlugin(Logger logger, ProxyServer proxy, @DataDirectory Path pluginFolder) {
        this.logger = logger;
        this.proxy = proxy;
        this.pluginFolder = pluginFolder;
    }

    @Subscribe
    public void initialize(ProxyInitializeEvent event) {
        try { // Load the core first
            ServerListPlusLogger clogger = new Slf4jServerListPlusLogger(this.logger, ServerListPlusLogger.CORE_PREFIX);
            this.core = new ServerListPlusCore(this, clogger);
            logger.info("Successfully loaded!");
        } catch (ServerListPlusException e) {
            logger.info("Please fix the error before restarting the server!");
            return;
        } catch (Exception e) {
            logger.error("An internal error occurred while loading the core.", e);
            return;
        }

        // Register commands
        this.proxy.getCommandManager().register("serverlistplus", new ServerListPlusCommand(), "slp");
    }

    @Subscribe
    public void shutdown(ProxyShutdownEvent event) {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    // Commands
    public final class ServerListPlusCommand implements SimpleCommand {
        private ServerListPlusCommand() {}

        @Override
        public void execute(Invocation invocation) {
            core.executeCommand(new VelocityCommandSender(proxy, invocation.source()),
                    invocation.alias(), invocation.arguments());
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            return core.tabComplete(new VelocityCommandSender(proxy, invocation.source()),
                    invocation.alias(), invocation.arguments());
        }

        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("serverlistplus.command");
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
            String description = response.getDescription();
            if (description != null) builder.description(LEGACY_SERIALIZER.deserialize(description));

            if (version != null) {
                // Version name
                String versionName = response.getVersion();
                // Protocol version
                Integer protocol = response.getProtocolVersion();

                if (versionName != null || protocol != null) builder.version(new ServerPing.Version(
                        protocol != null ? protocol : version.getProtocol(),
                        versionName != null ? versionName : version.getName()
                ));
            }

            if (players != null) {
                if (response.hidePlayers()) {
                    builder.nullPlayers();
                } else {
                    // Online players
                    Integer onlinePlayers = response.getOnlinePlayers();
                    if (onlinePlayers != null) builder.onlinePlayers(onlinePlayers);
                    // Max players
                    Integer maxPlayers = response.getMaxPlayers();
                    if (maxPlayers != null) builder.maximumPlayers(maxPlayers);

                    // Player hover
                    String playerHover = response.getPlayerHover();
                    if (playerHover != null) {
                        builder.clearSamplePlayers();

                        if (!playerHover.isEmpty()) {
                            List<String> lines = Helper.splitLinesToList(playerHover);

                            ServerPing.SamplePlayer[] sample = new ServerPing.SamplePlayer[lines.size()];
                            for (int i = 0; i < sample.length; i++)
                                sample[i] = new ServerPing.SamplePlayer(lines.get(i), UUIDs.EMPTY);

                            builder.samplePlayers(sample);
                        }
                    }
                }
            }

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon == FaviconSource.NONE) {
                builder.clearFavicon();
            } else if (favicon != null) {
                com.google.common.base.Optional<Favicon> icon = faviconCache.get(favicon);
                if (icon.isPresent())
                    builder.favicon(icon.get());
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
        return ServerType.VELOCITY;
    }

    @Override
    public String getServerImplementation() {
        ProxyVersion version = this.proxy.getVersion();
        return version.getName() + " " + version.getVersion();
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
    public FaviconCache<?> getFaviconCache() {
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
        return FormattingCodes.colorizeHex(s);
    }

    @Override
    public RGBFormat getRGBFormat() {
        return RGBFormat.ADVENTURE;
    }

    @Override
    public void initialize(ServerListPlusCore core) {

    }

    @Override
    public void reloadCaches(ServerListPlusCore core) {

    }

    @Override
    public void createFaviconCache(CacheBuilderSpec spec) {
        if (faviconCache == null) {
            faviconCache = new FaviconCache<Favicon>(this, spec) {
                @Override
                protected Favicon createFavicon(BufferedImage image) throws Exception {
                    return Favicon.create(image);
                }
            };
        } else {
            faviconCache.reload(spec);
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
