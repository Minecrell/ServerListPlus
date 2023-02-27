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

package net.minecrell.serverlistplus.sponge;

import static org.spongepowered.api.Platform.Component.API;
import static org.spongepowered.api.Platform.Component.IMPLEMENTATION;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import net.minecrell.serverlistplus.core.util.FormattingCodes;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.Randoms;
import net.minecrell.serverlistplus.core.util.UUIDs;
import net.minecrell.serverlistplus.sponge.protocol.StatusProtocolHandler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StoppedGameEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Plugin("serverlistplus")
public class SpongePlugin implements ServerListPlusPlugin {

    static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().build();

    @Inject protected Game game;
    @Inject protected Logger logger;
    @Inject protected PluginContainer plugin;

    @ConfigDir(sharedRoot = false) @Inject
    protected Path configDir;

    private StatusProtocolHandler handler;
    private ServerListPlusCore core;

    private Object loginListener, pingListener;

    private FaviconCache<Favicon> faviconCache;

    @Listener
    public void construct(ConstructPluginEvent event) {
        this.handler = StatusProtocolHandler.create(this.logger);

        try {
            ServerListPlusLogger clogger = new Log4j2ServerListPlusLogger(this.logger, ServerListPlusLogger.CORE_PREFIX);
            this.core = new ServerListPlusCore(this, clogger);
            logger.info("Successfully loaded!");
        } catch (ServerListPlusException e) {
            logger.info("Please fix the error before restarting the server!");
            return;
        } catch (Exception e) {
            logger.error("An internal error occurred while loading the core.", e);
            return;
        }

        core.setBanProvider(new SpongeBanProvider());
    }

    @Listener
    public void registerCommand(RegisterCommandEvent<Command.Raw> event) {
        event.register(this.plugin, new ServerListPlusCommand(), "serverlistplus", "slp");
    }

    @Listener
    public void stop(StoppedGameEvent event) {
        try {
            core.stop();
        } catch (ServerListPlusException ignored) {}
    }

    @Listener
    public void refresh(RefreshGameEvent event) {
        core.reload();
    }

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile(" ", Pattern.LITERAL);

    static String[] splitArgs(ArgumentReader arguments) {
        String input = arguments.input();
        return input.isEmpty() ? new String[0] : ARGUMENT_PATTERN.split(input);
    }

    public final class ServerListPlusCommand implements Command.Raw {

        @Override
        public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) {
            core.executeCommand(new SpongeCommandSender(cause), "serverlistplus", splitArgs(arguments));
            return CommandResult.success();
        }

        @Override
        public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
            return core.tabComplete(new SpongeCommandSender(cause), "serverlistplus", splitArgs(arguments))
                    .stream()
                    .map(CommandCompletion::of)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean canExecute(CommandCause cause) {
            return cause.hasPermission("serverlistplus.command");
        }

        @Override
        public Optional<Component> shortDescription(CommandCause cause) {
            return Optional.of(Component.text("Configure ServerListPlus"));
        }

        @Override
        public Optional<Component> extendedDescription(CommandCause cause) {
            return Optional.empty();
        }

        @Override
        public Component usage(CommandCause cause) {
            return Component.text("/serverlistplus");
        }
    }

    // Player tracking
    public final class LoginListener {
        private LoginListener() {}

        @Listener
        public void onPlayerJoin(ServerSideConnectionEvent.Login event) {
            core.updateClient(event.connection().address().getAddress(),
                    event.profile().uuid(), event.profile().name().get());
        }

        @Listener
        public void onPlayerQuit(ServerSideConnectionEvent.Disconnect event) {
            core.updateClient(event.player().connection().address().getAddress(),
                    event.profile().uuid(), event.player().name());
        }
    }

    public final class PingListener {
        private PingListener() {}

        @Listener
        public void onStatusPing(ClientPingServerEvent event) {
            StatusRequest request = core.createRequest(event.client().address().getAddress());
            event.client().virtualHost().ifPresent(request::setTarget);
            handler.getProtocolVersion(event).ifPresent(request::setProtocolVersion);

            final ClientPingServerEvent.Response ping = event.response();
            final ClientPingServerEvent.Response.Players players = ping.players().orElse(null);

            StatusResponse response = request.createResponse(core.getStatus(), new ResponseFetcher() {
                @Override
                public Integer getOnlinePlayers() {
                    return players != null ? players.online() : null;
                }

                @Override
                public Integer getMaxPlayers() {
                    return players != null ? players.max() : null;
                }

                @Override
                public int getProtocolVersion() {
                    return handler.getProtocolVersion(ping).orElse(-1);
                }
            });

            // Description
            String description = response.getDescription();
            if (description != null) ping.setDescription(LEGACY_SERIALIZER.deserialize(description));

            // Version
            handler.setVersion(ping, response);

            // Favicon
            FaviconSource favicon = response.getFavicon();
            if (favicon == FaviconSource.NONE) {
                ping.setFavicon(null);
            } else if (favicon != null) {
                com.google.common.base.Optional<Favicon> icon = faviconCache.get(favicon);
                if (icon.isPresent())
                    ping.setFavicon(icon.get());
            }

            if (players != null) {
                if (response.hidePlayers()) {
                    ping.setHidePlayers(true);
                } else {
                    // Online players
                    Integer onlinePlayers = response.getOnlinePlayers();
                    if (onlinePlayers != null) players.setOnline(onlinePlayers);

                    // Max players
                    Integer maxPlayers = response.getMaxPlayers();
                    if (maxPlayers != null) players.setMax(maxPlayers);

                    String playerHover = response.getPlayerHover();
                    if (playerHover != null) {
                        List<GameProfile> profiles = players.profiles();
                        profiles.clear();

                        if (!playerHover.isEmpty()) {
                            for (String line : Helper.splitLines(playerHover)) {
                                profiles.add(GameProfile.of(new java.util.UUID(0, 42), line));
                            }
                        }
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
        Platform platform = game.platform();
        PluginContainer api = platform.container(API);
        PluginContainer impl = platform.container(IMPLEMENTATION);
        return impl.metadata().name().orElse("SpongeAPI") + " v" + impl.metadata().version()
                + " (" + api.metadata().name().orElse("Sponge") + " v" + api.metadata().version() + ')';
    }

    @Override
    public Path getPluginFolder() {
        return configDir;
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        ServerWorld world = game.server().worldManager().world(ResourceKey.resolve(location)).orElse(null);
        if (world == null) return null;

        int count = 0;
        for (Player player : game.server().onlinePlayers()) {
            if (player.world().equals(world)) count++;
        }

        return count;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        Collection<ServerPlayer> players = game.server().onlinePlayers();
        List<String> result = new ArrayList<>(players.size());

        for (Player player : players) {
            result.add(player.name());
        }

        return Randoms.shuffle(result).iterator();
    }

    @Override
    public Iterator<String> getRandomPlayers(String location) {
        ServerWorld world = game.server().worldManager().world(ResourceKey.resolve(location)).orElse(null);
        if (world == null) return null;

        Collection<ServerPlayer> players = game.server().onlinePlayers();
        List<String> result = new ArrayList<>();

        for (Player player : players) {
            if (player.world().equals(world)) {
                result.add(player.name());
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
    public FaviconCache<?> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public void runAsync(Runnable runnable) {
        Task task = Task.builder().plugin(this.plugin).execute(runnable).build();
        game.asyncScheduler().submit(task);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable runnable, long repeat, TimeUnit unit) {
        Task task = Task.builder().plugin(this.plugin).interval(repeat, unit).execute(runnable).build();
        return new ScheduledSpongeTask(game.asyncScheduler().submit(task));
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
                    return Favicon.load(image);
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
                game.eventManager().registerListeners(this.plugin, this.loginListener = new LoginListener());
                logger.debug("Registered player tracking listener.");
            }
        } else if (loginListener != null) {
            game.eventManager().unregisterListeners(loginListener);
            this.loginListener = null;
            logger.debug("Unregistered player tracking listener.");
        }
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // Status listener
        if (hasChanges) {
            if (pingListener == null) {
                game.eventManager().registerListeners(this.plugin, this.pingListener = new PingListener());
                logger.debug("Registered ping listener.");
            }
        } else if (pingListener != null) {
            game.eventManager().unregisterListeners(pingListener);
            this.pingListener = null;
            logger.debug("Unregistered ping listener.");
        }
    }
}
