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

package net.minecrell.serverlistplus.server;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconCache;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.Log4j2ServerListPlusLogger;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.replacement.util.Literals;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.FormattingCodes;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.Randoms;
import net.minecrell.serverlistplus.core.util.UUIDs;
import net.minecrell.serverlistplus.server.config.ServerConf;
import net.minecrell.serverlistplus.server.network.Netty;
import net.minecrell.serverlistplus.server.network.NetworkManager;
import net.minecrell.serverlistplus.server.status.Favicon;
import net.minecrell.serverlistplus.server.status.StatusClient;
import net.minecrell.serverlistplus.server.status.StatusPingResponse;
import net.minecrell.serverlistplus.server.status.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ServerListPlusServer implements ServerListPlusPlugin {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().build();

    private static final Logger logger = LogManager.getLogger();
    private static ServerListPlusServer instance;

    private final ServerListPlusCore core;
    private final Path workingDir;

    private final NetworkManager network;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private boolean started;

    private boolean playerTracking;
    private ImmutableList<String> loginMessages;

    private FaviconCache<String> faviconCache;

    public ServerListPlusServer() throws UnknownHostException {
        checkState(instance == null, "Server was already initialized");
        instance = this;

        this.workingDir = Paths.get("");

        logger.info("Loading...");
        ServerListPlusLogger clogger = new Log4j2ServerListPlusLogger(LogManager.getLogger(ServerListPlusCore.class), null);
        this.core = new ServerListPlusCore(this, clogger, new ServerProfileManager());

        ServerConf conf = this.core.getConf(ServerConf.class);
        this.network = new NetworkManager(this, Netty.parseAddress(conf.Address));
        logger.info("Successfully loaded!");
    }

    public boolean start() {
        this.started = true;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        try {
            this.network.start();
        } catch (Exception e) {
            logger.error("Failed to start network manager", e);
            this.stop();
            return false;
        }

        return true;
    }

    public boolean isRunning() {
        return this.started;
    }

    public void join() throws InterruptedException {
        this.network.join();
    }

    public void stop() {
        if (this.started) {
            logger.info("Stopping...");

            try {
                this.network.stop();
            } catch (Exception e) {
                logger.error("Failed to stop network manager", e);
                return;
            }

            this.core.stop();

            this.started = false;
        }
    }

    public static StatusPingResponse postLegacy(InetSocketAddress address, InetSocketAddress virtualHost) {
        StatusPingResponse response = instance.handle(new StatusClient(address, OptionalInt.empty(), virtualHost));
        response.getVersion().setProtocol(Byte.MAX_VALUE);
        if (response.getPlayers() == null) {
            response.setPlayers(new StatusPingResponse.Players(0, -1, null));
        }
        return response;
    }

    public static StatusPingResponse post(StatusClient client) {
        return instance.handle(client);
    }

    public static Component postLogin(StatusClient client, String name) {
        return instance.handleLogin(client, name);
    }

    public Component handleLogin(StatusClient client, String name) {
        if (this.playerTracking) {
            core.updateClient(client.getAddress().getAddress(), null, name);
        }

        logger.info("Player '{}' tried to log in from {}", name, client);

        String message = Randoms.nextEntry(this.loginMessages);
        return LEGACY_SERIALIZER.deserialize(Literals.replace(message, "%player%", name));
    }

    public StatusPingResponse handle(StatusClient client) {
        StatusPingResponse ping = new StatusPingResponse();

        StatusRequest request = core.createRequest(client.getAddress().getAddress());
        client.getProtocol().ifPresent(request::setProtocolVersion);

        InetSocketAddress host = client.getVirtualHost();
        if (host != null) {
            request.setTarget(host);
        }

        final StatusPingResponse.Players players = ping.getPlayers();
        final StatusPingResponse.Version version = ping.getVersion();

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
        if (description != null) ping.setDescription(LEGACY_SERIALIZER.deserialize(description));

        if (version != null) {
            // Version name
            String versionName = response.getVersion();
            if (versionName != null) version.setName(versionName);
            // Protocol version
            Integer protocol = response.getProtocolVersion();
            if (protocol != null) version.setProtocol(protocol);
        }

        // Favicon
        FaviconSource favicon = response.getFavicon();
        if (favicon != null && favicon != FaviconSource.NONE) {
            Optional<String> icon = faviconCache.get(favicon);
            if (icon.isPresent())
                ping.setFavicon(icon.get());
        }

        if (response.hidePlayers()) {
            ping.setPlayers(null);
        } else {
            StatusPingResponse.Players newPlayers = players;
            if (newPlayers == null) {
                newPlayers = new StatusPingResponse.Players(0, 0, null);
                ping.setPlayers(newPlayers);
            }

            // Online players
            Integer onlinePlayers = response.getOnlinePlayers();
            if (onlinePlayers != null) newPlayers.setOnline(onlinePlayers);
            // Max players
            Integer maxPlayers = response.getMaxPlayers();
            if (maxPlayers != null) newPlayers.setMax(maxPlayers);

            // Player hover
            String playerHover = response.getPlayerHover();
            if (playerHover != null && !playerHover.isEmpty()) {
                List<String> lines = Helper.splitLinesToList(playerHover);

                UserProfile[] sample = new UserProfile[lines.size()];
                for (int i = 0; i < sample.length; i++)
                    sample[i] = new UserProfile(lines.get(i), UUIDs.EMPTY);

                newPlayers.setSample(sample);
            }
        }

        return ping;
    }

    private static final ImmutableSet<String> COMMAND_ALIASES = ImmutableSet.of("serverlistplus", "slp");
    private static final String DEFAULT_ALIAS = "serverlistplus";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public void processCommand(List<String> args) {
        String cmd = DEFAULT_ALIAS;
        if (!args.isEmpty() && COMMAND_ALIASES.contains(args.get(0).toLowerCase(Locale.ENGLISH))) {
            cmd = args.remove(0);
        }

        String subcommand = args.isEmpty() ? "" : args.get(0);
        if (subcommand.equalsIgnoreCase("stop")) {
            this.stop();
            return;
        }

        this.core.executeCommand(ConsoleCommandSender.INSTANCE, cmd, args.toArray(EMPTY_STRING_ARRAY));
        if (subcommand.equalsIgnoreCase("help")) {
            ConsoleCommandSender.INSTANCE.sendMessage(ServerListPlusCore.buildCommandHelp(
                    "stop", null, "Stop the server."));
        }
    }

    public List<String> tabComplete(List<String> args) {
        String cmd = null;
        if (!args.isEmpty() && COMMAND_ALIASES.contains(args.get(0).toLowerCase(Locale.ENGLISH))) {
            cmd = args.remove(0);
        }

        List<String> result = this.core.tabComplete(ConsoleCommandSender.INSTANCE,
                cmd != null ? cmd : DEFAULT_ALIAS, args.toArray(EMPTY_STRING_ARRAY));
        if (args.size() > 1)
            return result;

        if (cmd == null) {
            result.addAll(COMMAND_ALIASES);
        }
        result.add("stop");
        return result;
    }

    @Override
    public ServerListPlusCore getCore() {
        return this.core;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.SERVER;
    }

    @Override
    public String getServerImplementation() {
        return "ServerListPlusServer";
    }

    @Override
    public Path getPluginFolder() {
        return this.workingDir;
    }

    @Override
    public Integer getOnlinePlayers(String location) {
        return null;
    }

    @Override
    public Iterator<String> getRandomPlayers() {
        return null;
    }

    @Override
    public Iterator<String> getRandomPlayers(String location) {
        return null;
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
        this.scheduler.execute(task);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return new ScheduledFutureTask(this.scheduler.scheduleAtFixedRate(task, 0, repeat, unit));
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
        core.registerConf(ServerConf.class, new ServerConf(), ServerConf.getExample(), "Server");
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
                    return Favicon.create(image);
                }
            };
        } else {
            faviconCache.reload(spec);
        }
    }

    @Override
    public void configChanged(ServerListPlusCore core, InstanceStorage<Object> confs) {
        this.playerTracking = confs.get(PluginConf.class).PlayerTracking.Enabled;

        ServerConf conf = confs.get(ServerConf.class);

        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String message : conf.Login.Message) {
            builder.add(ReplacementManager.replaceStatic(core, message));
        }

        this.loginMessages = builder.build();
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {

    }
}
