/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your server list ping!
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

package net.minecrell.serverlistplus.core;

import net.minecrell.serverlistplus.core.config.CoreConf;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.config.help.ConfExamples;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;

import java.net.InetAddress;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Represents the core part of the ServerListPlus plugin.
 */
public class ServerListPlusCore {
    private final ServerListPlusPlugin plugin;
    private final ServerListPlusLogger logger;

    private final ConfigurationManager configManager;
    private final ServerStatusManager statusManager;

    private Cache<String, String> playerTracker;
    private String playerTrackerConf;

    public ServerListPlusCore(ServerListPlusPlugin plugin) throws ServerListPlusException {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
        this.logger = new ServerListPlusLogger(this);

        this.getLogger().info("Initializing...");

        this.statusManager = new ServerStatusManager(this);
        this.configManager = new ConfigurationManager(this);

        this.registerConf(ServerStatusConf.class, ConfExamples.forServerStatus(), "Status");
        this.registerConf(PluginConf.class, ConfExamples.forPlugin(), "Plugin");
        this.registerConf(CoreConf.class, ConfExamples.forCore(), "Core");

        this.getPlugin().initialize(this);
        this.reload();

        this.getLogger().info("ServerListPlus has been successfully initialized.");
    }

    public <T> void registerConf(Class<T> clazz, T def, String alias) {
        configManager.getDefaults().set(clazz, def);
        configManager.getYAML().registerAlias(clazz, alias);
    }

    private void reloadCaches() {
        CoreConf conf = this.getConf(CoreConf.class);
        boolean enabled = this.getConf(PluginConf.class).PlayerTracking;

        if (enabled &&
                (playerTrackerConf != null && conf.Caches != null && playerTrackerConf.equals(conf.Caches.PlayerTracking)))
            return;

        if (playerTracker != null) {
            this.getLogger().info("Deleting old player tracking cache due to configuration changes.");
            playerTracker.invalidateAll();
            playerTracker.cleanUp();
            this.playerTracker = null;
        }

        if (enabled) {
            this.getLogger().info("Creating new player tracking cache...");

            try {
                this.playerTrackerConf = this.getConf(CoreConf.class).Caches.PlayerTracking;
                this.playerTracker = CacheBuilder.from(playerTrackerConf).build();
            } catch (Exception e) {
                this.getLogger().severe(e, "Unable to parse player tracker cache configuration.");
                this.playerTrackerConf = this.getDefaultConf(CoreConf.class).Caches.PlayerTracking;
                this.playerTracker = CacheBuilder.from(playerTrackerConf).build();
            }
        }
    }

    public void reload() throws ServerListPlusException {
        configManager.reload();
        this.reloadCaches();
        statusManager.reload();
    }

    public void addClient(String playerName, InetAddress client) {
        if (this.playerTracker != null) playerTracker.put(client.getHostAddress(), playerName);
    }

    public String resolveClient(InetAddress client) {
        return playerTracker.getIfPresent(client.getHostAddress());
    }

    public ServerListPlusLogger getLogger() {
        return logger;
    }

    public ServerListPlusPlugin getPlugin() {
        return plugin;
    }

    public ConfigurationManager getConf() {
        return configManager;
    }

    public <T> T getConf(Class<T> clazz) {
        return this.getConf().getStorage().get(clazz);
    }

    public <T> T getDefaultConf(Class<T> clazz) {
        return this.getConf().getDefaults().get(clazz);
    }

    public ServerStatusManager getStatus() {
        return statusManager;
    }
}
