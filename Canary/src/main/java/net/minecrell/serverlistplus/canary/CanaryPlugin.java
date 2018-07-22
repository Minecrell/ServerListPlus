/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.canary;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.hook.player.PreConnectionHook;
import net.canarymod.hook.system.ServerListPingHook;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconHelper;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.Randoms;
import net.visualillusionsent.utils.TaskManager;
import org.mcstats.MetricsLite;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CanaryPlugin extends Plugin implements ServerListPlusPlugin {
    private ServerListPlusCore core;

    private Path pluginFolder;
    private PluginListener loginListener, pingListener;

    private MetricsLite metrics;

    private final Field PROFILES_FIELD;

    // Favicon cache
    private final CacheLoader<FaviconSource, Optional<String>> faviconLoader =
            new CacheLoader<FaviconSource, Optional<String>>() {
                @Override
                public Optional<String> load(FaviconSource source) throws Exception {
                    // Try loading the favicon
                    BufferedImage image = FaviconHelper.loadSafely(core, source);
                    if (image == null) return Optional.absent(); // Favicon loading failed
                    else return Optional.of(CanaryFavicon.create(image)); // Success!
                }
            };
    private LoadingCache<FaviconSource, Optional<String>> faviconCache;

    @SneakyThrows
    public CanaryPlugin() {
        SnakeYAML.load();
        this.PROFILES_FIELD = ServerListPingHook.class.getDeclaredField("profiles");
        PROFILES_FIELD.setAccessible(true);
    }

    @Override
    public boolean enable() {
        this.pluginFolder = Paths.get(Canary.getWorkingPath(), "config", getName());

        try {
            this.core = new ServerListPlusCore(this);
            getLogman().info("Successfully loaded!");
        } catch (ServerListPlusException e) {
            getLogman().info("Please fix the error before restarting the server!");
            return false;
        } catch (Exception e) {
            getLogman().error("An internal error occurred while loading the core.", e);
            return false;
        }

        // Register command
        try {
            registerCommands(new ServerListPlusCommand(), false);
        } catch (CommandDependencyException e) {
            getLogman().error("Failed to register command", e);
            return false;
        }
        
        core.setBanProvider(new CanaryBanProvider());

        return true;
    }

    @Override
    public void disable() {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    public final class ServerListPlusCommand implements CommandListener {
        private ServerListPlusCommand() {}

        @Command(aliases = {"serverlistplus", "serverlist+", "serverlist", "slp", "sl+", "s++", "serverping+",
                "serverping", "spp", "slus"}, permissions = "", description = "ServerListPlus", toolTip = "")
        public void onCommand(MessageReceiver sender, String[] args) {
            core.executeCommand(new CanaryCommandSender(sender), args[0], Arrays.copyOfRange(args, 1, args.length));
        }
    }

    // Player tracking
    public final class LoginListener implements PluginListener {
        private LoginListener() {}

        @HookHandler
        public void onPreConnect(PreConnectionHook hook) throws UnknownHostException {
            core.updateClient(InetAddress.getByName(hook.getIp()), hook.getUUID(), hook.getName());
        }

        @HookHandler
        public void onDisconnect(DisconnectionHook hook) throws UnknownHostException {
            core.updateClient(InetAddress.getByName(hook.getPlayer().getIP()), hook.getPlayer().getUUID(), hook.getPlayer().getName());
        }
    }

    public final class PingListener implements PluginListener {
        private PingListener() {}

        @HookHandler
        public void onServerListPing(final ServerListPingHook hook) throws Exception {
            StatusRequest request = core.createRequest(hook.getRequesterAddress());
            request.setProtocolVersion(hook.getRequesterProtocol());
            request.setTarget(hook.getHostNamePinged(), hook.getPortPinged());

            StatusResponse response = request.createResponse(core.getStatus(), new ResponseFetcher() {
                @Override
                public Integer getOnlinePlayers() {
                    return hook.getCurrentPlayers();
                }

                @Override
                public Integer getMaxPlayers() {
                    return hook.getMaxPlayers();
                }

                @Override
                public int getProtocolVersion() {
                    return hook.getRequesterProtocol(); // :|
                }
            });

            // Description
            String message = response.getDescription();
            if (message != null) hook.setMotd(message);

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon != null) {
                Optional<String> icon = faviconCache.getUnchecked(favicon);
                if (icon.isPresent()) hook.setFavicon(icon.get());
            }

            // Online players
            Integer count = response.getOnlinePlayers();
            if (count != null) hook.setCurrentPlayers(count);
            // Max players
            count = response.getMaxPlayers();
            if (count != null) hook.setMaxPlayers(count);

            // Player hover
            message = response.getPlayerHover();
            if (message != null) {
                List<GameProfile> profiles = hook.getProfiles();
                if (!(profiles instanceof ArrayList)) {
                    profiles = new ArrayList<>();
                    PROFILES_FIELD.set(hook, profiles);
                } else {
                    profiles.clear();
                }

                if (response.useMultipleSamples()) {
                    count = response.getDynamicSamples();
                    List<String> lines = count != null ? Helper.splitLinesCached(message, count) :
                            Helper.splitLinesCached(message);

                    for (String line : lines) {
                        profiles.add(new GameProfile(StatusManager.EMPTY_UUID, line));
                    }
                } else
                    profiles.add(new GameProfile(StatusManager.EMPTY_UUID, message));
            }
        }
    }

    @Override
    public ServerListPlusCore getCore() {
        return core;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.CANARY;
    }

    @Override
    public String getServerImplementation() {
        return Canary.getImplementationTitle() + " v" + Canary.getServer().getCanaryModVersion() + " (MC: " + Canary.getServer().getName() + ')';
    }

    @Override
    public Path getPluginFolder() {
        return pluginFolder;
    }

    @Override
    public Integer getOnlinePlayers(String worldName) {
        World world = Canary.getServer().getWorld(worldName);
        return world != null ? world.getPlayerList().size() : null;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        List<Player> players = Canary.getServer().getPlayerList();
        List<String> result = new ArrayList<>(players.size());

        for (Player player : players) {
            result.add(player.getName());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Iterator<String> getRandomPlayers(String worldName) {
        World world = Canary.getServer().getWorld(worldName);
        if (world == null) return null;

        List<Player> players = world.getPlayerList();
        List<String> result = new ArrayList<>(players.size());

        for (Player player : players) {
            result.add(player.getName());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Cache<?, ?> getRequestCache() {
        return null;
    }

    @Override
    public LoadingCache<FaviconSource, ?> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public void runAsync(Runnable task) {
        TaskManager.executeTask(task);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return new ScheduledCanaryTask(TaskManager.scheduleDelayedTask(task, repeat, unit));
    }

    private static final Pattern COLOR_CODE = Pattern.compile("(?i)&([0-9A-FK-OR])");

    @Override
    public String colorize(String s) {
        return COLOR_CODE.matcher(s).replaceAll(ChatFormat.MARKER + "$1");
    }

    @Override
    public ServerListPlusLogger createLogger(ServerListPlusCore core) {
        return new Log4j2ServerListPlusLogger(core, getLogman());
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
            if (loginListener == null) {
                registerListener(this.loginListener = new LoginListener());
                getLogman().debug("Registered proxy player tracking listener.");
            }
        } else if (loginListener != null) {
            Canary.hooks().unregisterPluginListener(loginListener);
            this.loginListener = null;
            getLogman().debug("Unregistered proxy player tracking listener.");
        }

        // Plugin statistics
        if (confs.get(PluginConf.class).Stats) {
            if (metrics == null)
                try {
                    this.metrics = new MetricsLite(this);
                    metrics.start();
                } catch (Throwable e) {
                    getLogman().debug("Failed to enable plugin statistics: {}", Helper.causedException(e));
                }
        } else if (metrics != null)
            try {
                metrics.disable();
                this.metrics = null;
            } catch (Throwable e) {
                getLogman().debug("Failed to disable plugin statistics: ", Helper.causedException(e));
            }
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // Status listener
        if (hasChanges) {
            if (pingListener == null) {
                registerListener(this.pingListener = new PingListener());
                getLogman().debug("Registered proxy ping listener.");
            }
        } else if (pingListener != null) {
            Canary.hooks().unregisterPluginListener(pingListener);
            this.pingListener = null;
            getLogman().debug("Unregistered proxy ping listener.");
        }
    }
}
