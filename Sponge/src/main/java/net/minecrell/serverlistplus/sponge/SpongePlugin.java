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

package net.minecrell.serverlistplus.sponge;

import static org.spongepowered.api.Platform.Component.API;
import static org.spongepowered.api.Platform.Component.IMPLEMENTATION;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import net.minecrell.mcstats.SpongeStatsLite;
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
import net.minecrell.serverlistplus.sponge.protocol.DummyStatusProtocolHandler;
import net.minecrell.serverlistplus.sponge.protocol.StatusProtocolHandler;
import net.minecrell.serverlistplus.sponge.protocol.StatusProtocolHandlerImpl;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

@Plugin(id = "serverlistplus", name = "ServerListPlus",
        dependencies = @Dependency(id = "statusprotocol", optional = true))
public class SpongePlugin implements ServerListPlusPlugin {

    @Inject protected Game game;
    @Inject protected Logger logger;

    @ConfigDir(sharedRoot = false) @Inject
    protected File configDir;

    @Inject
    protected SpongeStatsLite stats;

    private final StatusProtocolHandler handler;

    private ServerListPlusCore core;

    private Object loginListener, pingListener;

    // Favicon cache
    private final CacheLoader<FaviconSource, Optional<Favicon>> faviconLoader =
            new CacheLoader<FaviconSource, Optional<Favicon>>() {
                @Override
                public Optional<Favicon> load(FaviconSource source) throws Exception {
                    // Try loading the favicon
                    BufferedImage image = FaviconHelper.loadSafely(core, source);
                    if (image == null) return Optional.empty(); // Favicon loading failed
                    else return Optional.of(game.getRegistry().loadFavicon(image)); // Success!
                }
            };
    private LoadingCache<FaviconSource, Optional<Favicon>> faviconCache;

    @Inject
    public SpongePlugin(PluginManager pluginManager) {
        this.handler = pluginManager.isLoaded("statusprotocol") ? new StatusProtocolHandlerImpl() : new DummyStatusProtocolHandler();
    }

    @Listener
    public void enable(GamePreInitializationEvent event) {
        if (this.handler.isDummy()) {
            this.logger.warn("You don't have StatusProtocol installed. Support for custom player slots will be disabled. Please install it from "
                    + "https://github.com/Minecrell/statusprotocol/releases if you intend to use this feature.");
        }

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

        game.getCommandManager().register(this, new ServerListPlusCommand(), "serverlistplus", "serverlist+",
                "serverlist", "slp", "sl+", "s++", "serverping+", "serverping", "spp", "slus");
        
        core.setBanProvider(new SpongeBanProvider());
    }

    @Listener
    public void disable(GameStoppingServerEvent event) {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile(" ", Pattern.LITERAL);

    @NonnullByDefault
    public final class ServerListPlusCommand implements CommandCallable {

        @Override
        public CommandResult process(CommandSource source, String arguments) {
            String[] args = arguments.isEmpty() ? new String[0] : ARGUMENT_PATTERN.split(arguments);
            core.executeCommand(new SpongeCommandSender(source), "serverlistplus", args);
            return CommandResult.success();
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) {
            return core.tabComplete(new SpongeCommandSender(source), "serverlistplus",
                    ARGUMENT_PATTERN.split(arguments));
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return true;
        }

        @Override
        public Optional<Text> getShortDescription(CommandSource source) {
            return Optional.empty();
        }

        @Override
        public Optional<Text> getHelp(CommandSource source) {
            return Optional.empty();
        }

        @Override
        public Text getUsage(CommandSource source) {
            return Text.of();
        }

    }

    // Player tracking
    public final class LoginListener {
        private LoginListener() {}

        @Listener
        public void onPlayerJoin(ClientConnectionEvent.Login event) {
            core.updateClient(event.getConnection().getAddress().getAddress(),
                    event.getProfile().getUniqueId(), event.getProfile().getName().get());
        }

        @Listener
        public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
            core.updateClient(event.getTargetEntity().getConnection().getAddress().getAddress(),
                    event.getTargetEntity().getUniqueId(), event.getTargetEntity().getName());
        }
    }

    public final class PingListener {
        private PingListener() {}

        @Listener
        public void onStatusPing(ClientPingServerEvent event) {
            StatusRequest request = core.createRequest(event.getClient().getAddress().getAddress());
            event.getClient().getVirtualHost().ifPresent(request::setTarget);
            handler.getProtocolVersion(event).ifPresent(request::setProtocolVersion);

            final ClientPingServerEvent.Response ping = event.getResponse();
            final ClientPingServerEvent.Response.Players players = ping.getPlayers().orElse(null);

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
                    return handler.getProtocolVersion(ping).orElse(-1);
                }
            });

            // Description
            String message = response.getDescription();
            if (message != null) ping.setDescription(TextSerializers.LEGACY_FORMATTING_CODE.deserialize(message));

            // Version
            handler.setVersion(ping, response);

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
                                profiles.add(GameProfile.of(StatusManager.EMPTY_UUID, line));
                            }
                        } else
                            profiles.add(GameProfile.of(StatusManager.EMPTY_UUID, message));
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
        Platform platform = game.getPlatform();
        return platform.getContainer(IMPLEMENTATION).getName() + " v" + platform.getContainer(IMPLEMENTATION).getVersion().orElse("UNKNOWN")
                + " (" + platform.getContainer(API).getName() + " v" + platform.getContainer(API).getVersion().orElse("UNKNOWN") + ')';
    }

    @Override
    public Path getPluginFolder() {
        return configDir.toPath();
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        World world = game.getServer().getWorld(location).orElse(null);
        if (world == null) return null;

        int count = 0;
        for (Player player : game.getServer().getOnlinePlayers()) {
            if (player.getWorld().equals(world)) count++;
        }

        return count;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        Collection<Player> players = game.getServer().getOnlinePlayers();
        List<String> result = new ArrayList<>(players.size());

        for (Player player : players) {
            result.add(player.getName());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Iterator<String> getRandomPlayers(String location) {
        World world = game.getServer().getWorld(location).orElse(null);
        if (world == null) return null;

        Collection<Player> players = game.getServer().getOnlinePlayers();
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
        game.getScheduler().createTaskBuilder().async().execute(task).submit(this);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return new ScheduledSpongeTask(game.getScheduler().createTaskBuilder()
                .async().interval(repeat, unit).execute(task).submit(this));
    }

    @Override
    public String colorize(String s) {
        return TextSerializers.FORMATTING_CODE.replaceCodes(s, TextSerializers.LEGACY_FORMATTING_CODE);
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
    public void configChanged(ServerListPlusCore core, InstanceStorage<Object> confs) {
        // Player tracking
        if (confs.get(PluginConf.class).PlayerTracking.Enabled) {
            if (loginListener == null) {
                game.getEventManager().registerListeners(this, this.loginListener = new LoginListener());
                logger.debug("Registered player tracking listener.");
            }
        } else if (loginListener != null) {
            game.getEventManager().unregisterListeners(loginListener);
            this.loginListener = null;
            logger.debug("Unregistered player tracking listener.");
        }

        // Plugin statistics
        if (confs.get(PluginConf.class).Stats) {
            this.stats.start();
        } else {
            this.stats.stop();
        }
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // Status listener
        if (hasChanges) {
            if (pingListener == null) {
                game.getEventManager().registerListeners(this, this.pingListener = new PingListener());
                logger.debug("Registered ping listener.");
            }
        } else if (pingListener != null) {
            game.getEventManager().unregisterListeners(pingListener);
            this.pingListener = null;
            logger.debug("Unregistered ping listener.");
        }
    }
}
