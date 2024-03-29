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

package net.minecrell.serverlistplus.canary;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.mojang.authlib.GameProfile;
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
import net.minecrell.serverlistplus.core.favicon.FaviconCache;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.Log4j2ServerListPlusLogger;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
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
import net.visualillusionsent.utils.TaskManager;

import java.awt.image.BufferedImage;
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

    private FaviconCache<String> faviconCache;

    @Override
    public boolean enable() {
        this.pluginFolder = Paths.get(Canary.getWorkingPath(), "config", getName());

        try {
            ServerListPlusLogger clogger = new Log4j2ServerListPlusLogger(getLogman(), ServerListPlusLogger.CORE_PREFIX);
            this.core = new ServerListPlusCore(this, clogger);
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
        if (core == null) return;
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    public final class ServerListPlusCommand implements CommandListener {
        private ServerListPlusCommand() {}

        @Command(aliases = {"serverlistplus", "slp"}, description = "Configure ServerListPlus",
                permissions = "serverlistplus.command", toolTip = "")
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
            String description = response.getDescription();
            if (description != null) hook.setMotd(description);

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon == FaviconSource.NONE) {
                //hook.setFavicon(null); // FIXME (in Canary): Would cause a NPE
            } else if (favicon != null) {
                Optional<String> icon = faviconCache.get(favicon);
                if (icon.isPresent()) hook.setFavicon(icon.get());
            }

            // Online players
            Integer onlinePlayers = response.getOnlinePlayers();
            if (onlinePlayers != null) hook.setCurrentPlayers(onlinePlayers);
            // Max players
            Integer maxPlayers = response.getMaxPlayers();
            if (maxPlayers != null) hook.setMaxPlayers(maxPlayers);

            // Player hover
            String playerHover = response.getPlayerHover();
            if (playerHover != null) {
                List<GameProfile> profiles = hook.getProfiles();
                profiles.clear();

                if (!playerHover.isEmpty()) {
                    for (String line : Helper.splitLines(playerHover)) {
                        profiles.add(new GameProfile(UUIDs.EMPTY, line));
                    }
                }
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
    public FaviconCache<?> getFaviconCache() {
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
    public RGBFormat getRGBFormat() {
        return RGBFormat.UNSUPPORTED;
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
            faviconCache = new FaviconCache<String>(this, spec) {
                @Override
                protected String createFavicon(BufferedImage image) throws Exception {
                    return CanaryFavicon.create(image);
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
                registerListener(this.loginListener = new LoginListener());
                getLogman().debug("Registered proxy player tracking listener.");
            }
        } else if (loginListener != null) {
            Canary.hooks().unregisterPluginListener(loginListener);
            this.loginListener = null;
            getLogman().debug("Unregistered proxy player tracking listener.");
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
