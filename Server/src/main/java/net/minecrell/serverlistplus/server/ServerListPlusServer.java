package net.minecrell.serverlistplus.server;

import static net.minecrell.serverlistplus.core.logging.Logger.INFO;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.LoadingCache;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.JavaServerListPlusLogger;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.status.StatusManager;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class ServerListPlusServer implements ServerListPlusPlugin {

    private final Logger logger;
    private final ServerListPlusCore core;
    private final Path workingDir;

    private boolean started;

    public ServerListPlusServer(InetSocketAddress socket, Logger logger) {
        this.logger = logger;
        this.workingDir = Paths.get("");

        logger.log(INFO, "Loading...");
        this.core = new ServerListPlusCore(this, new ServerProfileManager());
        logger.log(INFO, "Successfully loaded!");
    }

    public void start() throws Exception {
        // TODO

        this.started = true;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        if (this.started) {
            // TODO
        }
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
    public LoadingCache<FaviconSource, String> getFaviconCache() {
        return null; // TODO
    }

    @Override
    public void runAsync(Runnable task) {
        // TODO
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return null; // TODO
    }

    @Override
    public String colorize(String s) {
        return s.replace('&', '\u00A7'); // TODO: Improve this
    }

    @Override
    public ServerListPlusLogger createLogger(ServerListPlusCore core) {
        return new JavaServerListPlusLogger(this.core, this.logger);
    }

    @Override
    public void initialize(ServerListPlusCore core) {
        // TODO
    }

    @Override
    public void reloadCaches(ServerListPlusCore core) {
    }

    @Override
    public void reloadFaviconCache(CacheBuilderSpec spec) {
        // TODO
    }

    @Override
    public void configChanged(InstanceStorage<Object> confs) {
        // TODO
    }

    @Override
    public void statusChanged(StatusManager status, boolean hasChanges) {
        // TODO
    }

}
