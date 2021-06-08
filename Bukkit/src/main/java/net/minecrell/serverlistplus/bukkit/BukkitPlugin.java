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

package net.minecrell.serverlistplus.bukkit;

import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.DEBUG;
import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.ERROR;
import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.INFO;
import static net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger.WARN;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecrell.serverlistplus.bukkit.config.BukkitConf;
import net.minecrell.serverlistplus.bukkit.config.ProtocolLibUsage;
import net.minecrell.serverlistplus.bukkit.handlers.BukkitEventHandler;
import net.minecrell.serverlistplus.bukkit.handlers.PaperEventHandler;
import net.minecrell.serverlistplus.bukkit.handlers.ProtocolLibHandler;
import net.minecrell.serverlistplus.bukkit.handlers.StatusHandler;
import net.minecrell.serverlistplus.bukkit.integration.BanManagerBanProvider;
import net.minecrell.serverlistplus.bukkit.integration.MaxBansBanProvider;
import net.minecrell.serverlistplus.bukkit.integration.PlaceholderAPIDynamicReplacer;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.CoreConf;
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
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.util.Randoms;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BukkitPlugin extends BukkitPluginBase implements ServerListPlusPlugin {
    private ServerListPlusCore core;
    private ServerType serverType;
    private RGBFormat rgbFormat = RGBFormat.UNSUPPORTED;

    private StatusHandler bukkit, protocol;
    private Listener loginListener, disconnectListener;

    private FaviconCache<CachedServerIcon> faviconCache;

    // Request cache
    private final CacheLoader<InetSocketAddress, StatusRequest> requestLoader =
            new CacheLoader<InetSocketAddress, StatusRequest>() {
        @Override
        public StatusRequest load(InetSocketAddress client) throws Exception {
            return core.createRequest(client.getAddress());
        }
    };

    private LoadingCache<InetSocketAddress, StatusRequest> requestCache;
    private String requestCacheConf;

    private boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    @Override
    public void onEnable() {
        this.serverType = ServerType.BUKKIT;

        try {
            Class.forName("org.spigotmc.SpigotConfig");
            this.serverType = ServerType.SPIGOT;
        } catch (ClassNotFoundException ignored) {}

        try {
            Class.forName("com.destroystokyo.paper.event.server.PaperServerListPingEvent");
            this.serverType = ServerType.PAPER;
            this.bukkit = new PaperEventHandler(this);
        } catch (ClassNotFoundException e) {
            this.bukkit = new BukkitEventHandler(this);
        }

        // Check if RGB color codes are supported
        if (colorize("&x&a&b&c&d&e&f").charAt(0) != '&') {
            this.rgbFormat = RGBFormat.WEIRD_BUNGEE;
        }

        if (isPluginEnabled("ProtocolLib")) {
            try {
                this.protocol = new ProtocolLibHandler(this);
            } catch (Throwable e) {
                getLogger().log(ERROR, "Failed to construct ProtocolLib handler. Is your ProtocolLib version up-to-date?", e);
            }
        } else if (serverType != ServerType.PAPER)
            getLogger().log(ERROR, "ProtocolLib IS NOT INSTALLED! Most features will NOT work! " +
                    "Alternatively, consider using Paper for full functionality without ProtocolLib!");

        if (isPluginEnabled("PlaceholderAPI")) {
            try {
                ReplacementManager.getDynamic().add(new PlaceholderAPIDynamicReplacer(getServer()));
            } catch (Throwable e) {
                getLogger().log(ERROR, "Failed to register PlaceholderAPI replacer", e);
            }
        }

        try { // Load the core first
            ServerListPlusLogger clogger = new JavaServerListPlusLogger(getLogger(), ServerListPlusLogger.CORE_PREFIX);
            this.core = new ServerListPlusCore(this, clogger);
            getLogger().log(INFO, "Successfully loaded!");
        } catch (ServerListPlusException e) {
            getLogger().log(INFO, "Please fix the error before restarting the server!");
            disablePlugin(); return; // Disable bukkit to show error in /plugins
        } catch (Exception e) {
            getLogger().log(ERROR, "An internal error occurred while loading the core!", e);
            disablePlugin(); return; // Disable bukkit to show error in /plugins
        }

        // Register commands
        getCommand("serverlistplus").setExecutor(new ServerListPlusCommand());

        try {
            if (isPluginEnabled("AdvancedBan")) {
                core.setBanProvider(new AdvancedBanBanProvider());
            } else if (isPluginEnabled("BanManager")) {
                core.setBanProvider(new BanManagerBanProvider());
            } else if (isPluginEnabled("MaxBans")) {
                core.setBanProvider(new MaxBansBanProvider());
            } else {
                core.setBanProvider(new BukkitBanProvider(getServer()));
            }
        } catch (Throwable e) {
            getLogger().log(ERROR, "Failed to register ban provider", e);
        }

        getLogger().log(INFO, getDisplayName() + " enabled.");
    }

    @Override
    public void onDisable() {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
        getLogger().log(INFO, getDisplayName() + " disabled.");
    }

    // Commands
    public final class ServerListPlusCommand implements TabExecutor {
        private ServerListPlusCommand() {}

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            core.executeCommand(new BukkitCommandSender(sender), cmd.getName(), args); return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
            return core.tabComplete(new BukkitCommandSender(sender), cmd.getName(), args);
        }
    }

    // Player tracking
    public final class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
            if (core == null) return; // Too early, we haven't finished initializing yet

            UUID uuid = null;
            try {
                uuid = event.getUniqueId();
            } catch (NoSuchMethodError ignored) {}
            core.updateClient(event.getAddress(), uuid, event.getName());
        }
    }

    public final class OfflineModeLoginListener implements Listener {
        private OfflineModeLoginListener() {}

        @EventHandler
        public void onPlayerLogin(PlayerLoginEvent event) {
            if (core == null) return; // Too early, we haven't finished initializing yet

            UUID uuid = null;
            try {
                uuid = event.getPlayer().getUniqueId();
            } catch (NoSuchMethodError ignored) {}
            core.updateClient(event.getAddress(), uuid, event.getPlayer().getName());
        }
    }

    public final class DisconnectListener implements Listener {
        private DisconnectListener() {}

        @EventHandler
        public void onPlayerDisconnect(PlayerQuitEvent event) {
            if (core == null) return; // Too early, we haven't finished initializing yet

            UUID uuid = null;
            try {
                uuid = event.getPlayer().getUniqueId();
            } catch (NoSuchMethodError ignored) {}
            core.updateClient(event.getPlayer().getAddress().getAddress(), uuid, event.getPlayer().getName());
        }
    }

    @Override
    public ServerListPlusCore getCore() {
        return core;
    }

    @Override
    public ServerType getServerType() {
        return serverType;
    }

    @Override
    public String getServerImplementation() {
        return getServer().getName() + " " + getServer().getVersion();
    }

    public StatusRequest getRequest(InetSocketAddress client) {
        return requestCache.getUnchecked(client);
    }

    public void requestCompleted(InetSocketAddress client) {
        requestCache.invalidate(client);
    }

    public CachedServerIcon getFavicon(FaviconSource source) {
        return faviconCache.get(source).orNull();
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        World world = getServer().getWorld(location);
        if (world == null) return null;

        int count = 0;
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.getWorld().equals(world)) count++;
        }

        return count;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<String> result = new ArrayList<>(players.size());

        for (Player player : players) {
            result.add(player.getName());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Iterator<String> getRandomPlayers(String worldName) {
        final World world = getServer().getWorld(worldName);
        if (world == null) return null;

        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<String> result = new ArrayList<>();

        for (Player player : players) {
            if (player.getWorld().equals(world)) {
                result.add(player.getName());
            }
        }

        if (result.isEmpty())
            return null;

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Cache<?, ?> getRequestCache() {
        return requestCache;
    }

    @Override
    public FaviconCache<?> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public void runAsync(Runnable task) {
        getServer().getScheduler().runTaskAsynchronously(this, task);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        repeat = unit.toMillis(repeat) / 50;
        return new ScheduledBukkitTask(
                getServer().getScheduler().runTaskTimerAsynchronously(this, task, repeat, repeat));
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
        core.registerConf(BukkitConf.class, new BukkitConf(), new BukkitConf(), "Bukkit");
    }

    @Override
    public void reloadCaches(ServerListPlusCore core) {
        CoreConf conf = core.getConf(CoreConf.class);
        // Check if request cache configuration has been changed
        if (requestCacheConf == null || requestCache == null || !requestCacheConf.equals(conf.Caches.Request)) {
            if (requestCache != null) {
                // Delete the request cache
                getLogger().log(DEBUG, "Deleting old request cache due to configuration changes.");
                requestCache.invalidateAll();
                requestCache.cleanUp();
                this.requestCache = null;
            }

            getLogger().log(DEBUG, "Creating new request cache...");

            try {
                this.requestCacheConf = conf.Caches.Request;
                this.requestCache = CacheBuilder.from(requestCacheConf).build(requestLoader);
            } catch (IllegalArgumentException e) {
                getLogger().log(ERROR, "Unable to create request cache using configuration settings.", e);
                this.requestCacheConf = core.getDefaultConf(CoreConf.class).Caches.Request;
                this.requestCache = CacheBuilder.from(requestCacheConf).build(requestLoader);
            }

            getLogger().log(DEBUG, "Request cache created.");
        }
    }

    @Override
    public void createFaviconCache(CacheBuilderSpec spec) {
        if (faviconCache == null) {
            faviconCache = new FaviconCache<CachedServerIcon>(this, spec) {
                @Override
                protected CachedServerIcon createFavicon(BufferedImage image) throws Exception {
                    return getServer().loadServerIcon(image);
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
            if (loginListener == null) {
                registerListener(this.loginListener = serverType != ServerType.BUKKIT || getServer().getOnlineMode()
                        ? new LoginListener() : new OfflineModeLoginListener());
                getLogger().log(DEBUG, "Registered player login listener.");
            }

            if (disconnectListener == null) {
                registerListener(this.disconnectListener = new DisconnectListener());
                getLogger().log(DEBUG, "Registered player disconnect listener.");
            }
        } else {
            if (loginListener != null) {
                unregisterListener(loginListener);
                this.loginListener = null;
                getLogger().log(DEBUG, "Unregistered player login listener.");
            }

            if (disconnectListener != null) {
                unregisterListener(disconnectListener);
                this.disconnectListener = null;
                getLogger().log(DEBUG, "Unregistered player disconnect listener.");
            }
        }
    }

    private boolean useProtocolLib(ServerListPlusCore core) {
        ProtocolLibUsage useProtocoLib = core.getConf(BukkitConf.class).ProtocolLib;
        if (serverType != ServerType.PAPER) {
            if (useProtocoLib == ProtocolLibUsage.AUTO) {
                getLogger().log(INFO, "Using ProtocolLib for full functionality. Alternatively, " +
                        "consider using Paper which supports full functionality without ProtocolLib");
            }
            return useProtocoLib != ProtocolLibUsage.DISABLE;
        }

        // Paper supports all functionality by default so keep ProtocolLib disabled if AUTO
        if (useProtocoLib == ProtocolLibUsage.AUTO) {
            getLogger().log(INFO, "ProtocolLib is no longer required (and used) by default when using Paper. " +
                    "If this causes compatibility issues with other plugins, try setting ProtocolLib: ENABLE");
        }
        return useProtocoLib == ProtocolLibUsage.ENABLE;
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // Status packet listener
        if (hasChanges) {
            if (bukkit.register())
                getLogger().log(DEBUG, "Registered ping event handler.");
            if (protocol != null) {
                if (useProtocolLib(status.getCore())) {
                    protocol.register();
                } else {
                    protocol.unregister();
                    if (serverType != ServerType.PAPER)
                        getLogger().log(WARN, "ProtocolLib IS NOT ENABLED! Most features will NOT work! " +
                                "Alternatively, consider using Paper for full functionality without ProtocolLib!");
                }
            } else if (serverType != ServerType.PAPER)
                getLogger().log(ERROR, "ProtocolLib IS NOT INSTALLED! Most features will NOT work! " +
                        "Alternatively, consider using Paper for full functionality without ProtocolLib!");
        } else {
            if (bukkit.unregister())
                getLogger().log(DEBUG, "Unregistered ping event handler.");
            if (protocol != null && protocol.unregister())
                getLogger().log(DEBUG, "Unregistered status protocol handler.");
        }
    }
}
