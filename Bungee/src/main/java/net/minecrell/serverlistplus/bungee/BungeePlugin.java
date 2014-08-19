/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.bungee;

import net.minecrell.metrics.BungeeMetricsLite;
import net.minecrell.serverlistplus.bungee.replacement.ServerOnlinePlaceholder;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.favicon.FaviconHelper;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;
import net.minecrell.serverlistplus.core.status.PlayerFetcher;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.InstanceStorage;

import java.awt.image.BufferedImage;
import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;

import static net.minecrell.serverlistplus.core.logging.Logger.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.ERROR;
import static net.minecrell.serverlistplus.core.logging.Logger.INFO;

public class BungeePlugin extends BungeePluginBase implements ServerListPlusPlugin {
    private ServerListPlusCore core;
    private LoadingCache<FaviconSource, Optional<Favicon>> faviconCache;

    private LoginListener loginListener;
    private PingListener pingListener;

    private BungeeMetricsLite metrics;

    @Override
    public void onEnable() {
        try { // Load the core first
            this.core = new ServerListPlusCore(this);
            getLogger().log(INFO, "Successfully loaded!");
        } catch (ServerListPlusException e) {
            getLogger().log(INFO, "Please fix the error before restarting the server!"); return;
        } catch (Exception e) {
            getLogger().log(ERROR, "An internal error occurred while loading the core.", e);
            return;
        }

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new ServerListPlusCommand());
    }

    // Commands
    public final class ServerListPlusCommand extends Command implements TabExecutor {
        private ServerListPlusCommand() {
            super("serverlistplus", null, "serverlist+", "serverlist", "slp", "sl+", "s++",
                    "serverping+", "serverping", "spp", "slus");
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
    public final class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(LoginEvent event) {
            PendingConnection con = event.getConnection();
            core.addClient(con.getAddress().getAddress(), new PlayerIdentity(con.getUniqueId(), con.getName()));
        }
    }

    // Status listener
    public final class PingListener implements Listener {
        private PingListener() {}

        @EventHandler
        public void onProxyPing(ProxyPingEvent event) {
            if (event.getResponse() == null) return; // Check if response is not empty
            final ServerPing ping = event.getResponse();
            final ServerPing.Players players = ping.getPlayers();

            StatusResponse response = core.getRequest(event.getConnection().getAddress().getAddress())
                    .createResponse(core.getStatus(),
                            // Return unknown player counts if it has been hidden
                            players == null ? new PlayerFetcher.Hidden() :
                                    new PlayerFetcher() {
                                        @Override
                                        public Integer getOnlinePlayers() {
                                            return players.getOnline();
                                        }

                                        @Override
                                        public Integer getMaxPlayers() {
                                            return players.getMax();
                                        }
                                    });

            // Description
            String message = response.getDescription();
            if (message != null) ping.setDescription(message);

            ServerPing.Protocol version = ping.getVersion();
            if (version != null) {
                // Version name
                message = response.getVersion();
                if (message != null) version.setName(message);
                // Protocol version
                Integer protocol = response.getProtocol();
                if (protocol != null) version.setProtocol(protocol);
            }

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon != null) {
                Optional<Favicon> icon = faviconCache.getUnchecked(favicon);
                if (icon.isPresent()) ping.setFavicon(icon.get());
            }

            if (players != null) {
                if (response.hidePlayers()) {
                    ping.setPlayers(null);
                } else {
                    // Online players
                    Integer count = response.getOnlinePlayers();
                    if (count != null) players.setOnline(count);
                    // Max players
                    count = response.getMaxPlayers();
                    if (count != null) players.setMax(count);

                    // Player hover
                    message = response.getPlayerHover();
                    if (message != null) players.setSample(new ServerPing.PlayerInfo[]{
                            new ServerPing.PlayerInfo(message, StatusManager.EMPTY_UUID) });
                }
            }
        }
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
    }

    @Override
    public String getServerImplementation() {
        return getProxy().getVersion() + " (MC: " + getProxy().getGameVersion() + ')';
    }

    @Override
    public String getRandomPlayer() {
        int tmp = getProxy().getOnlineCount();
        if (tmp == 0) return null;
        if (tmp == 1) return getProxy().getPlayers().iterator().next().getName();
        // TODO: Make this complete faster
        Collection<ProxiedPlayer> players = getProxy().getPlayers();
        int i = 0; tmp = Helper.random().nextInt(players.size());
        for (ProxiedPlayer player : players)
            if (i++ == tmp) return player.getName();
        return null;
    }

    @Override
    public LoadingCache<FaviconSource, Optional<Favicon>> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void initialize(ServerListPlusCore core) {
        // Register the BungeeCord replacers
        ReplacementManager.getDynamic().add(new ServerOnlinePlaceholder(getProxy()));
    }

    @Override
    public void reloadFaviconCache(CacheBuilderSpec spec) {
        if (spec != null) {
            this.faviconCache = CacheBuilder.from(spec).build(new CacheLoader<FaviconSource, Optional<Favicon>>() {
                @Override
                public Optional<Favicon> load(FaviconSource source) throws Exception {
                    // Try loading the favicon
                    BufferedImage image = FaviconHelper.loadSafely(core, source);
                    if (image == null) return Optional.absent(); // Favicon loading failed
                    else return Optional.of(Favicon.create(image)); // Success!
                }
            });
        } else {
            // Delete favicon cache
            faviconCache.invalidateAll();
            faviconCache.cleanUp();
            this.faviconCache = null;
        }
    }

    @Override
    public void configChanged(InstanceStorage<Object> confs) {
        // Player tracking
        if (confs.get(PluginConf.class).PlayerTracking) {
            if (loginListener == null) {
                registerListener(this.loginListener = new LoginListener());
                getLogger().log(DEBUG, "Registered proxy player tracking listener.");
            }
        } else if (loginListener != null) {
            unregisterListener(loginListener);
            this.loginListener = null;
            getLogger().log(DEBUG, "Unregistered proxy player tracking listener.");
        }

        // Plugin statistics
        if (confs.get(PluginConf.class).Stats) {
            if (metrics == null)
                try {
                    this.metrics = new BungeeMetricsLite(this);
                    metrics.start();
                } catch (Throwable e) {
                    getLogger().log(DEBUG, "Failed to enable plugin statistics: " + Helper.causedError(e));
                }
        } else if (metrics != null)
            try {
                metrics.stop();
                this.metrics = null;
            } catch (Throwable e) {
                getLogger().log(DEBUG, "Failed to disable plugin statistics: " + Helper.causedError(e));
            }
    }

    @Override
    public void statusChanged(StatusManager status) {
        // Status listener
        if (status.hasChanges()) {
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
