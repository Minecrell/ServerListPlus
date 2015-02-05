/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
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

package net.minecrell.serverlistplus.sponge;

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
import net.minecrell.serverlistplus.core.util.SnakeYAML;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.entity.living.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.living.player.PlayerQuitEvent;
import org.spongepowered.api.event.server.StatusPingEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.world.World;

@Plugin(id = "serverlistplus", name = "ServerListPlus", version = "3.4-SNAPSHOT")
public class SpongePlugin implements ServerListPlusPlugin {
    @Inject protected Game game;
    @Inject protected Logger logger;

    @ConfigDir(sharedRoot = false) @Inject
    protected File configDir;

    private ServerListPlusCore core;

    private Object loginListener, pingListener;

    // Favicon cache
    private final CacheLoader<FaviconSource, Optional<Favicon>> faviconLoader =
            new CacheLoader<FaviconSource, Optional<Favicon>>() {
                @Override
                public Optional<Favicon> load(FaviconSource source) throws Exception {
                    // Try loading the favicon
                    BufferedImage image = FaviconHelper.loadSafely(core, source);
                    if (image == null) return Optional.absent(); // Favicon loading failed
                    else return Optional.of(game.getRegistry().loadFavicon(image)); // Success!
                }
            };
    private LoadingCache<FaviconSource, Optional<Favicon>> faviconCache;

    public SpongePlugin() {
        SnakeYAML.load();
    }

    @Subscribe
    public void enable(PreInitializationEvent event) {
        try {
            this.core = new ServerListPlusCore(this);
            logger.info("Successfully loaded!");
        } catch (ServerListPlusException e) {
            logger.info("Please fix the error before restarting the server!");
            return;
        } catch (Exception e) {
            logger.error("An internal error occurred while loading the core.", e);
            return;
        }

        game.getCommandDispatcher().register(this, new ServerListPlusCommand(), "serverlistplus", "serverlist+",
                "serverlist", "slp", "sl+", "s++", "serverping+", "serverping", "spp", "slus");
    }

    @Subscribe
    public void disable(ServerStoppingEvent event) {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile(" ", Pattern.LITERAL);

    @NonnullByDefault
    public final class ServerListPlusCommand implements CommandCallable {

        @Override
        public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
            String[] args = arguments.isEmpty() ? new String[0] : ARGUMENT_PATTERN.split(arguments);
            core.executeCommand(new SpongeCommandSender(source), Helper.getLastElement(parents), args);
            return true;
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return false; // fail
        }

        @Override
        public Optional<String> getShortDescription() {
            return Optional.absent();
        }

        @Override
        public Optional<String> getHelp() {
            return Optional.absent();
        }

        @Override
        public String getUsage() {
            return "";
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return core.tabComplete(new SpongeCommandSender(source), "serverlistplus",
                    ARGUMENT_PATTERN.split(arguments));
        }
    }

    // Player tracking
    public final class LoginListener {
        private LoginListener() {}

        @Subscribe
        public void onJoin(PlayerJoinEvent event) throws UnknownHostException {
            // TODO
            core.updateClient(InetAddress.getLocalHost(), event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }

        @Subscribe
        public void onQuit(PlayerQuitEvent event) throws UnknownHostException {
            // TODO
            core.updateClient(InetAddress.getLocalHost(), event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }
    }

    public final class PingListener {
        private PingListener() {}

        @Subscribe
        public void onStatusPing(StatusPingEvent event) {
            StatusRequest request = core.createRequest(event.getClient().getAddress().getAddress());

            StatusPingEvent.Response ping = event.getResponse();
            final StatusPingEvent.Response.Players players = ping.getPlayers().orNull();

            StatusResponse response = request.createResponse(core.getStatus(), new ResponseFetcher() {
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
                    return 0; // idk
                }
            });

            // Description
            String message = response.getDescription();
            if (message != null) ping.setDescription(Messages.of(message));

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon != null) {
                Optional<Favicon> icon = faviconCache.getUnchecked(favicon);
                if (icon.isPresent()) ping.setFavicon(icon.get());
            }

            if (players != null) {
                if (response.hidePlayers()) {
                    ping.setHidePlayers(true);
                } else {
                    // Online players
                    Integer count = response.getOnlinePlayers();
                    if (count != null) players.setOnline(count);

                    // Max players
                    count = response.getMaxPlayers();
                    if (count != null) players.setMax(count);

                    message = response.getPlayerHover();
                    if (message != null) {
                        List<GameProfile> profiles = players.getProfiles();
                        profiles.clear();

                        if (response.useMultipleSamples()) {
                            count = response.getDynamicSamples();
                            List<String> lines = count != null ? Helper.splitLinesCached(message, count) :
                                    Helper.splitLinesCached(message);

                            for (String line : lines) {
                                profiles.add(game.getRegistry().createGameProfile(StatusManager.EMPTY_UUID, line));
                            }
                        } else
                            profiles.add(game.getRegistry().createGameProfile(StatusManager.EMPTY_UUID, message));
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
        return ServerType.SPONGE;
    }

    @Override
    public String getServerImplementation() {
        return "Sponge v" + game.getAPIVersion() + " (" + game.getImplementationVersion() + ")";
    }

    @Override
    public Path getPluginFolder() {
        return configDir.toPath();
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        World world = game.getServer().get().getWorld(location).orNull();
        if (world == null) return null;

        int count = 0;
        for (Player player : game.getServer().get().getOnlinePlayers()) {
            if (player.getWorld().equals(world)) count++;
        }

        return count;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        Collection<Player> players = game.getServer().get().getOnlinePlayers();
        List<String> result = new ArrayList<>(players.size());

        for (Player player : players) {
            result.add(player.getName());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Iterator<String> getRandomPlayers(String location) {
        World world = game.getServer().get().getWorld(location).orNull();
        if (world == null) return null;

        Collection<Player> players = game.getServer().get().getOnlinePlayers();
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
        return null;
    }

    @Override
    public LoadingCache<FaviconSource, ?> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public void runAsync(Runnable task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    private static final Pattern COLOR_CODE = Pattern.compile("(?i)&([0-9A-FK-OR])");

    @Override
    public String colorize(String s) {
        return COLOR_CODE.matcher(s).replaceAll("\u00A7$1");
    }

    @Override
    public ServerListPlusLogger createLogger(ServerListPlusCore core) {
        return new Slf4jServerListPlusLogger(core, logger);
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
    public void configChanged(InstanceStorage<Object> confs) {
        // Player tracking
        if (confs.get(PluginConf.class).PlayerTracking.Enabled) {
            if (loginListener == null) {
                game.getEventManager().register(this, this.loginListener = new LoginListener());
                logger.debug("Registered player tracking listener.");
            }
        } else if (loginListener != null) {
            game.getEventManager().unregister(loginListener);
            this.loginListener = null;
            logger.debug("Unregistered player tracking listener.");
        }
    }

    @Override
    public void statusChanged(StatusManager status) {
        // Status listener
        if (status.hasChanges()) {
            if (pingListener == null) {
                game.getEventManager().register(this, this.pingListener = new PingListener());
                logger.debug("Registered ping listener.");
            }
        } else if (pingListener != null) {
            game.getEventManager().unregister(pingListener);
            this.pingListener = null;
            logger.debug("Unregistered ping listener.");
        }
    }
}
