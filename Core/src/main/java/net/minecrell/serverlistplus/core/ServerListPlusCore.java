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

package net.minecrell.serverlistplus.core;

import net.minecrell.serverlistplus.core.config.CoreConf;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.config.help.ConfExamples;
import net.minecrell.serverlistplus.core.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.util.Format;

import java.net.InetAddress;
import java.util.Locale;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;

/**
 * Represents the core part of the ServerListPlus plugin.
 */
public class ServerListPlusCore {
    private final CoreDescription info;

    private final ServerListPlusPlugin plugin;
    private final ServerListPlusLogger logger;

    private final ConfigurationManager configManager;
    private final ProfileManager profileManager;
    private final ServerStatusManager statusManager;

    private Cache<String, String> playerTracker;
    private String playerTrackerConf;

    private String faviconCacheConf;

    public ServerListPlusCore(ServerListPlusPlugin plugin) throws ServerListPlusException {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
        this.logger = new ServerListPlusLogger(this);

        plugin.getLogger().info("Loading core...");
        this.info = CoreDescription.load(this);

        // Initialize configuration and status manager, but not yet load it
        this.statusManager = new ServerStatusManager(this);
        this.configManager = new ConfigurationManager(this);

        // Register the configurations
        this.registerConf(ServerStatusConf.class, ConfExamples.forServerStatus(), "Status");
        this.registerConf(PluginConf.class, ConfExamples.forPlugin(), "Plugin");
        this.registerConf(CoreConf.class, ConfExamples.forCore(), "Core");

        // Initialize the profile manager
        this.profileManager = new ProfileManager(this);

        plugin.initialize(this);
        this.reload(); // Now load the configuration!

        plugin.getLogger().info("Core was successfully loaded!");
    }

    public <T> void registerConf(Class<T> clazz, T def, String alias) {
        configManager.getDefaults().set(clazz, def); // Set default configuration
        configManager.getYAML().registerAlias(clazz, alias); // Register alias of the configuration
    }

    private void reloadCaches() {
        CoreConf conf = this.getConf(CoreConf.class);
        boolean enabled = this.getConf(PluginConf.class).PlayerTracking;

        // Check if player tracker configuration has been changed
        if (!enabled || (playerTrackerConf == null || conf.Caches == null
                || !playerTrackerConf.equals(conf.Caches.PlayerTracking))) {

            if (playerTracker != null) {
                // Delete the player tracker
                this.getLogger().debug("Deleting old player tracking cache due to configuration changes.");
                playerTracker.invalidateAll();
                playerTracker.cleanUp();
                this.playerTracker = null;
            }

            if (enabled) {
                this.getLogger().debug("Creating new player tracking cache...");

                try {
                    Preconditions.checkArgument(conf.Caches != null, "Cache configuration section not found");
                    this.playerTrackerConf = conf.Caches.PlayerTracking;
                    this.playerTracker = CacheBuilder.from(playerTrackerConf).build();
                } catch (IllegalArgumentException e) {
                    this.getLogger().log(e, "Unable to create player tracker cache using configuration settings.");
                    this.playerTrackerConf = this.getDefaultConf(CoreConf.class).Caches.PlayerTracking;
                    this.playerTracker = CacheBuilder.from(playerTrackerConf).build();
                }

                this.getLogger().debug("Player tracking cache created.");
            } else
                playerTrackerConf = null; // Not enabled, so there is also no cache
        }

        enabled = statusManager.hasFavicon();

        // Check if favicon cache configuration has been changed
        if (!enabled || (faviconCacheConf == null || conf.Caches == null
                || !faviconCacheConf.equals(conf.Caches.Favicon))) {
            if (plugin.getFaviconCache() != null) {
                this.getLogger().debug("Deleting old favicon cache due to configuration changes.");
                plugin.reloadFaviconCache(null); // Delete the old favicon cache
            }

            if (enabled) {
                this.getLogger().debug("Creating new favicon cache...");

                try {
                    Preconditions.checkArgument(conf.Caches != null, "Cache configuration section not found!");
                    this.faviconCacheConf = conf.Caches.Favicon;
                    plugin.reloadFaviconCache(CacheBuilderSpec.parse(faviconCacheConf));
                } catch (IllegalArgumentException e) {
                    this.getLogger().log(e, "Unable to create favicon cache using configuration settings.");
                    this.faviconCacheConf = this.getDefaultConf(CoreConf.class).Caches.Favicon;
                    plugin.reloadFaviconCache(CacheBuilderSpec.parse(faviconCacheConf));
                }

                this.getLogger().debug("Favicon cache created.");
            } else
                faviconCacheConf = null; // Not used, so there is also no cache
        }
    }

    public void reload() throws ServerListPlusException {
        configManager.reload(); // Reload configuration from disk
        this.profileManager.reload(); // Reload profile storage from disk
        if (!profileManager.isEnabled())
            this.getLogger().warning("Configuration is not enabled, nothing will be changed on the server!");
        statusManager.reload(); // Now actually read and process the configuration
        this.reloadCaches(); // Check for cache setting changes
    }

    public void addClient(String playerName, InetAddress client) {
        if (this.playerTracker != null) playerTracker.put(client.getHostAddress(), playerName);
    }

    public String resolveClient(InetAddress client) {
        return this.playerTracker != null ? playerTracker.getIfPresent(client.getHostAddress()) : null;
    }

    public void executeCommand(ServerCommandSender sender, String cmd, String[] args) {
        String sub = (args.length > 0) ? args[0] : null;
        if (sub != null) {
            if (sub.equalsIgnoreCase("reload")) {
                this.getLogger().infoF("Reloading configuration at request of %s!", sender);
                sender.sendMessage(Format.GREEN + "Reloading configuration...");

                try { // Reload the configuration
                    this.reload();
                    sender.sendMessage(Format.GREEN + "Configuration successfully reloaded!");
                } catch (ServerListPlusException e) {
                    sender.sendMessage(Format.RED + "An internal error occurred while reloading the " +
                            "configuration.");
                }

                return;
            } else if (sub.equalsIgnoreCase("save")) {
                this.getLogger().infoF("Saving configuration at request of %s!", sender);
                sender.sendMessage(Format.GREEN + "Saving configuration...");

                try { // Save the configuration
                    configManager.save();
                    sender.sendMessage(Format.GREEN + "Configuration successfully saved.");
                } catch (ServerListPlusException e) {
                    sender.sendMessage(Format.RED + "An internal error occurred while saving the configuration.");
                }

                return;
            } else if (sub.equalsIgnoreCase("enable") || sub.equalsIgnoreCase("disable")) {
                boolean enable = sub.equalsIgnoreCase("enable");
                String tmp = enable ? "Enabling" : "Disabling";
                this.getLogger().infoF("%s ServerListPlus at request of %s...", tmp, sender);
                sender.sendMessage(Format.GREEN + tmp + " ServerListPlus...");

                try { // Enable / disable the ServerListPlus profile
                    profileManager.setEnabled(enable);
                    sender.sendMessage(Format.GREEN + "ServerListPlus has been successfully " + (enable ?
                            "enabled" : "disabled") + "!");
                } catch (ServerListPlusException e) {
                    sender.sendMessage(Format.RED + "An internal error occurred while " + (enable ? "enabling" :
                            "disabling") + " ServerListPlus.");
                }

                return;
            } else if (sub.equalsIgnoreCase("clean") && args.length > 1) {
                String cacheName = args[1].toLowerCase(Locale.ENGLISH);
                Cache<?, ?> cache =  cacheName.equals("players") ? playerTracker
                        : (cacheName.equals("favicons") ? plugin.getFaviconCache() : null);
                if (cache != null) {
                    this.getLogger().infoF("Cleaning %s cache at request of %s...", cacheName, sender);
                    cache.invalidateAll();
                    cache.cleanUp();
                    this.getLogger().debug("Done.");

                    sender.sendMessage(Format.GREEN + "Successfully cleaned " + cacheName + " cache.");
                    return;
                }
            }
        }

        // Send the sender some information about the plugin
        sender.sendMessage(Format.GOLD + info.getName() + plugin.getServerType() + " v" + info.getVersion());
        if (info.getDescription() != null)
            sender.sendMessage(Format.GRAY + info.getDescription());
        if (info.getAuthor() != null)
            sender.sendMessage(Format.GOLD + "Author: " + Format.GRAY + info.getAuthor());
        if (info.getWebsite() != null)
            sender.sendMessage(Format.GOLD + "Website: " + Format.GRAY + info.getWebsite());
        if (info.getWiki() != null)
            sender.sendMessage(Format.GOLD + "Wiki: " + Format.GRAY + info.getWiki());

        // Command help
        sender.sendMessages(
                Format.GOLD + "Commands:",
                buildCommandHelp("Display an information page about the plugin and list all available commands."),
                buildCommandHelp("reload", "Reload the plugin configuration."),
                buildCommandHelp("save", "Save the plugin configuration."),
                buildCommandHelp("enable", "Enable the plugin and start modifying the status ping."),
                buildCommandHelp("disable", "Disable the plugin and stop modifying the status ping."),
                buildCommandHelp("clean", "<favicons/players>", "Delete all entries from the specified cache.")
        );
    }

    private static String buildCommandHelp(String description) {
        return buildCommandHelp(null, description);
    }

    private static String buildCommandHelp(String cmd, String description) {
        return buildCommandHelp(cmd, null, description);
    }

    private static String buildCommandHelp(String cmd, String usage, String description) {
        StringBuilder help = new StringBuilder();
        help.append(Format.RED).append("/serverlistplus");
        if (cmd != null) help.append(' ').append(cmd);
        if (usage != null) help.append(' ').append(Format.GOLD).append(usage);
        return help.append(Format.WHITE).append(" - ").append(Format.GRAY).append(description).toString();
    }

    public ServerListPlusLogger getLogger() {
        return logger;
    }

    public ServerListPlusPlugin getPlugin() {
        return plugin;
    }

    public CoreDescription getInfo() {
        return info;
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

    public ProfileManager getProfiles() {
        return profileManager;
    }

    public ServerStatusManager getStatus() {
        return statusManager;
    }
}
