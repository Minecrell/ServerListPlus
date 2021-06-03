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

package net.minecrell.serverlistplus.bungee;

import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.DEBUG;
import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.ERROR;
import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.INFO;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;
import net.minecrell.serverlistplus.bungee.integration.BungeeBanBanProvider;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconCache;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.player.ban.integration.AdvancedBanBanProvider;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.Randoms;
import net.minecrell.serverlistplus.core.util.UUIDs;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BungeePlugin extends BungeePluginBase implements ServerListPlusPlugin {
    private ServerListPlusCore core;
    private RGBFormat rgbFormat = RGBFormat.UNSUPPORTED;
    private Listener connectionListener, pingListener;

    private final FaviconCache<Favicon> faviconCache = new FaviconCache<Favicon>() {
        @Override protected Favicon createFavicon(BufferedImage image) throws Exception {
            return Favicon.create(image);
        }
    };;

    private boolean isPluginLoaded(String pluginName) {
        return getProxy().getPluginManager().getPlugin(pluginName) != null;
    }

    @Override
    public void onEnable() {
        // Check if RGB color codes are supproted
        if (colorize("&x&a&b&c&d&e&f").charAt(0) != '&') {
            this.rgbFormat = RGBFormat.WEIRD_BUNGEE;
        }

        try { // Load the core first
            ServerListPlusLogger clogger = new JavaServerListPlusLogger(getLogger(), ServerListPlusLogger.CORE_PREFIX);
            this.core = new ServerListPlusCore(this, clogger);
            getLogger().log(INFO, "Successfully loaded!");
        } catch (ServerListPlusException e) {
            getLogger().log(INFO, "Please fix the error before restarting the server!"); return;
        } catch (Exception e) {
            getLogger().log(ERROR, "An internal error occurred while loading the core.", e);
            return;
        }

        faviconCache.setCore(core);

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new ServerListPlusCommand());

        try {
            if (isPluginLoaded("AdvancedBan")) {
                core.setBanProvider(new AdvancedBanBanProvider());
            } else if (isPluginLoaded("BungeeBan")) {
                core.setBanProvider(new BungeeBanBanProvider());
            }
        } catch (Throwable e) {
            getLogger().log(ERROR, "Failed to register ban provider", e);
        }
    }

    @Override
    public void onDisable() {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    // Commands
    public final class ServerListPlusCommand extends Command implements TabExecutor {
        private ServerListPlusCommand() {
            super("serverlistplus", "serverlistplus.command", "slp");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            core.executeCommand(new BungeeCommandSender(sender), getName(), args);
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            return core.tabComplete(new BungeeCommandSender(sender), getName(), args);
        }
    }

    // Player tracking
    public final class ConnectionListener implements Listener {
        private ConnectionListener() {}

        @EventHandler
        public void onPlayerLogin(LoginEvent event) {
            handleConnection(event.getConnection());
        }

        @EventHandler
        public void onPlayerLogout(PlayerDisconnectEvent event) {
            handleConnection(event.getPlayer().getPendingConnection());
        }

        private void handleConnection(PendingConnection con) {
            if (core == null) return; // Too early, we haven't finished initializing yet
            core.updateClient(con.getAddress().getAddress(), con.getUniqueId(), con.getName());
        }
    }

    // Status listener
    public final class PingListener implements Listener {
        private PingListener() {}

        @EventHandler
        public void onProxyPing(ProxyPingEvent event) {
            if (core == null) return; // Too early, we haven't finished initializing yet
            if (event.getResponse() == null) return; // Check if response is not empty

            PendingConnection con = event.getConnection();
            StatusRequest request = core.createRequest(con.getAddress().getAddress());

            request.setProtocolVersion(con.getVersion());
            InetSocketAddress host = con.getVirtualHost();
            if (host != null) {
                ServerInfo forcedHost = AbstractReconnectHandler.getForcedHost(con);
                request.setTarget(host, forcedHost != null ? forcedHost.getName() : null);
            }

            final ServerPing ping = event.getResponse();
            final ServerPing.Players players = ping.getPlayers();
            final ServerPing.Protocol version = ping.getVersion();

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

            // Description
            String description = response.getDescription();
            if (description != null) ping.setDescription(description);

            if (version != null) {
                // Version name
                String versionName = response.getVersion();
                if (versionName != null) version.setName(versionName);
                // Protocol version
                Integer protocol = response.getProtocolVersion();
                if (protocol != null) version.setProtocol(protocol);
            }

            if (players != null) {
                if (response.hidePlayers()) {
                    ping.setPlayers(null);
                } else {
                    // Online players
                    Integer onlinePlayers = response.getOnlinePlayers();
                    if (onlinePlayers != null) players.setOnline(onlinePlayers);
                    // Max players
                    Integer maxPlayers = response.getMaxPlayers();
                    if (maxPlayers != null) players.setMax(maxPlayers);

                    // Player hover
                    String playerHover = response.getPlayerHover();
                    if (playerHover != null) {
                        if (playerHover.isEmpty()) {
                            players.setSample(null);
                        } else {
                            List<String> lines = Helper.splitLinesToList(playerHover);

                            ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[lines.size()];
                            for (int i = 0; i < sample.length; i++)
                                sample[i] = new ServerPing.PlayerInfo(lines.get(i), UUIDs.EMPTY);

                            players.setSample(sample);
                        }
                    }
                }
            }

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon == FaviconSource.NONE) {
                ping.setFavicon((Favicon) null);
            } else if (favicon != null) {
                Optional<Favicon> icon;
                // Check if instanceof AsyncEvent for compatibility with 1.7.10
                if (event instanceof AsyncEvent && !faviconCache.contains(favicon)) {
                    // Load favicon asynchronously
                    event.registerIntent(BungeePlugin.this);
                    getProxy().getScheduler().runAsync(BungeePlugin.this, new AsyncFaviconLoader(event, favicon));
                } else {
                    icon = faviconCache.get(favicon);
                    if (icon.isPresent()) {
                        ping.setFavicon(icon.get());
                    }
                }
            }
        }
    }

    private final class AsyncFaviconLoader implements Runnable {

        private final ProxyPingEvent event;
        private final FaviconSource source;

        private AsyncFaviconLoader(ProxyPingEvent event, FaviconSource source) {
            this.event = event;
            this.source = source;
        }

        @Override
        public void run() {
            Optional<Favicon> favicon = faviconCache.get(this.source);
            if (favicon.isPresent()) {
                event.getResponse().setFavicon(favicon.get());
            }

            event.completeIntent(BungeePlugin.this);
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
        return getProxy().getName() + " " + getProxy().getVersion();
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        ServerInfo server = getProxy().getServerInfo(location);
        return server != null ? server.getPlayers().size() : null;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        return getRandomPlayers(getProxy().getPlayers());
    }

    @Override
    public Iterator<String> getRandomPlayers(String location) {
        ServerInfo server = getProxy().getServerInfo(location);
        return server != null ? getRandomPlayers(server.getPlayers()) : null;
    }

    private static Iterator<String> getRandomPlayers(Collection<ProxiedPlayer> players) {
        if (Helper.isNullOrEmpty(players)) return null;

        List<String> result = new ArrayList<>(players.size());

        for (ProxiedPlayer player : players) {
            result.add(player.getName());
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
        getProxy().getScheduler().runAsync(this, task);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return new ScheduledBungeeTask(getProxy().getScheduler().schedule(this, task, repeat, repeat, unit));
    }

    @Override
    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public RGBFormat getRGBFormat() {
        return rgbFormat;
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
            faviconCache.reload(spec);
        } else {
            // Delete favicon cache
            faviconCache.clear();
        }
    }

    @Override
    public void configChanged(ServerListPlusCore core, InstanceStorage<Object> confs) {
        // Player tracking
        if (confs.get(PluginConf.class).PlayerTracking.Enabled) {
            if (connectionListener == null) {
                registerListener(this.connectionListener = new ConnectionListener());
                getLogger().log(DEBUG, "Registered proxy player tracking listener.");
            }
        } else if (connectionListener != null) {
            unregisterListener(connectionListener);
            this.connectionListener = null;
            getLogger().log(DEBUG, "Unregistered proxy player tracking listener.");
        }
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // Status listener
        if (hasChanges) {
            if (pingListener == null) {
                registerListener(this.pingListener = new PingListener());
                getLogger().log(DEBUG, "Registered proxy ping listener.");
            }
        } else if (pingListener != null) {
            unregisterListener(pingListener);
            this.pingListener = null;
            getLogger().log(DEBUG, "Unregistered proxy ping listener.");
        }
    }
}
