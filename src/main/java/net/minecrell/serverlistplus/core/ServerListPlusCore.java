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

package net.minecrell.serverlistplus.core;

import static com.google.common.base.StandardSystemProperty.JAVA_CLASS_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_NAME;
import static com.google.common.base.StandardSystemProperty.OS_ARCH;
import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.StandardSystemProperty.OS_VERSION;
import static net.minecrell.serverlistplus.core.logging.Logger.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.INFO;
import static net.minecrell.serverlistplus.core.logging.Logger.REPORT;
import static net.minecrell.serverlistplus.core.logging.Logger.WARN;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.minecrell.serverlistplus.core.config.CoreConf;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.config.help.Examples;
import net.minecrell.serverlistplus.core.logging.Logger;
import net.minecrell.serverlistplus.core.player.IdentificationStorage;
import net.minecrell.serverlistplus.core.player.JSONIdentificationStorage;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import net.minecrell.serverlistplus.core.player.ban.NoBanProvider;
import net.minecrell.serverlistplus.core.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.profile.JSONProfileManager;
import net.minecrell.serverlistplus.core.profile.ProfileManager;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.util.ChatFormat;
import net.minecrell.serverlistplus.core.util.Helper;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the core part of the ServerListPlus plugin.
 */
public class ServerListPlusCore {
    private static @Getter ServerListPlusCore instance;
    
    private final @Getter ServerListPlusPlugin plugin;
    private final @Getter Logger<ServerListPlusException> logger;

    private final CoreDescription info;

    private final ConfigurationManager configManager;
    private final ProfileManager profileManager;
    private final StatusManager statusManager;

    private IdentificationStorage storage;

    private String faviconCacheConf;
    
    private @Getter @Setter BanProvider banProvider;

    public ServerListPlusCore(ServerListPlusPlugin plugin) throws ServerListPlusException {
        this(plugin, null);
    }

    public ServerListPlusCore(ServerListPlusPlugin plugin, ProfileManager profileManager) throws ServerListPlusException {
        instance = this;
        
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
        this.logger = plugin.createLogger(this);
        this.info = CoreDescription.load(this);
        this.banProvider = new NoBanProvider();

        try {
            if (Float.parseFloat(JAVA_CLASS_VERSION.value()) < 52.0) {
                getLogger().log(WARN, "You're using Java 7 or lower. Keep in mind future versions of ServerListPlus " +
                        "will require Java 8 or higher. Java 8 contains various improvements, especially regarding " +
                        "performance. If possible, please upgrade as soon as possible.");
            }
        } catch (NumberFormatException ignored) {
            getLogger().log(WARN, "Failed to detect Java version. If you're using Java 7 or lower, keep in mind " +
                    "future versions of ServerListPlus will require Java 8 or higher. Java 8 contains various " +
                    "improvements, especially regarding performance. If possible, please upgrade as soon as possible.");
        }

        // Print some information about the environment
        getLogger().log(REPORT, Helper.joinLines("",
                "---",
                "Plugin: " + getDisplayName(),
                "Server: " + plugin.getServerImplementation(),
                "Java: " + JAVA_VERSION.value() + " (" + JAVA_VM_NAME.value() + ')',
                "OS: " + OS_NAME.value() + ", " + OS_VERSION.value() + " (" + OS_ARCH.value() + ')',
                "---"
        ));

        // Initialize configuration and status manager, but not yet load it
        this.statusManager = new StatusManager(this);
        this.configManager = new ConfigurationManager(this);

        // Register the configurations
        registerConf(ServerStatusConf.class, new ServerStatusConf(), Examples.forServerStatus(), "Status");
        registerConf(PluginConf.class, new PluginConf(), Examples.forPlugin(), "Plugin");
        registerConf(CoreConf.class, new CoreConf(), null, "Core");

        configManager.getYAML().registerAlias(JSONIdentificationStorage.Conf.class, "JSONStorage");
        //configManager.getYAML().registerAlias(SQLIdentificationStorage.Conf.class, "SQLStorage");

        // Initialize the profile manager
        this.profileManager = profileManager != null ? profileManager : new JSONProfileManager(this);
        this.storage = new JSONIdentificationStorage(this);

        plugin.initialize(this);

        this.reload(); // Now load the configuration!
        storage.enable();
    }

    public void stop() throws ServerListPlusException {
        storage.disable();
    }

    public String getDisplayName() {
        return info.getName() + " v" + info.getVersion() + " (" + plugin.getServerType() + ')';
    }

    public <T> void registerConf(Class<T> clazz, T def, T example, String alias) {
        if (def != null) configManager.getDefaults().set(clazz, def); // Set default configuration
        if (example != null) configManager.getExamples().set(clazz, example);
        configManager.getYAML().registerAlias(clazz, alias); // Register alias for the configuration
    }

    private void reloadCaches() {
        CoreConf conf = this.getConf(CoreConf.class);
        boolean enabled = statusManager.hasFavicon();

        // Check if favicon cache configuration has been changed
        if (!enabled || (faviconCacheConf == null || conf.Caches == null ||
                !faviconCacheConf.equals(conf.Caches.Favicon))) {
            if (plugin.getFaviconCache() != null) {
                getLogger().log(DEBUG, "Deleting old favicon cache due to configuration changes.");
                plugin.reloadFaviconCache(null); // Delete the old favicon cache
            }

            if (enabled) {
                getLogger().log(DEBUG, "Creating new favicon cache...");

                try {
                    this.faviconCacheConf = conf.Caches.Favicon;
                    plugin.reloadFaviconCache(CacheBuilderSpec.parse(faviconCacheConf));
                } catch (IllegalArgumentException e) {
                    getLogger().log(e, "Unable to create favicon cache using configuration settings.");
                    this.faviconCacheConf = getDefaultConf(CoreConf.class).Caches.Favicon;
                    plugin.reloadFaviconCache(CacheBuilderSpec.parse(faviconCacheConf));
                }

                getLogger().log(DEBUG, "Favicon cache created.");
            } else
                faviconCacheConf = null; // Not used, so there is also no cache
        }

        plugin.reloadCaches(this);
    }

    public void reload() throws ServerListPlusException {
        configManager.reload(); // Reload configuration from disk
        this.profileManager.reload(); // Reload profile storage from disk
        if (!profileManager.isEnabled())
            getLogger().log(WARN, "Configuration is not enabled, nothing will be changed on the server! Please execute /slp enable to enable the "
                    + "configuration.");
        statusManager.reload(); // Now actually read and process the configuration
        this.reloadCaches(); // Check for cache setting changes
        storage.reload();
    }

    public void updateClient(InetAddress client, UUID uuid, String playerName) {
        storage.update(client, PlayerIdentity.create(uuid, playerName));
    }

    public PlayerIdentity resolveClient(InetAddress client) {
        return storage.resolve(client);
    }

    public StatusRequest createRequest(InetAddress client) {
        return new StatusRequest(client, resolveClient(client));
    }

    private static final String COMMAND_PREFIX_BASE = ChatFormat.GOLD + "[ServerListPlus] ";
    private static final String COMMAND_PREFIX = COMMAND_PREFIX_BASE + ChatFormat.GRAY;
    private static final String COMMAND_PREFIX_SUCCESS = COMMAND_PREFIX_BASE + ChatFormat.GREEN;
    private static final String COMMAND_PREFIX_ERROR = COMMAND_PREFIX_BASE + ChatFormat.RED;

    private static final String ADMIN_PERMISSION = "serverlistplus.admin";

    private static final String HELP_HEADER = ChatFormat.GOLD + "---- [ServerListPlus Help] ----";

    private static final Set<String> SUB_COMMANDS = ImmutableSet.of("reload", "rl", "save", "enable", "disable",
            "clean", "info", "help");

    private static final Map<String, Function<ServerListPlusCore, Cache<?, ?>>> CACHE_TYPES = ImmutableMap.of(
            "favicons", new Function<ServerListPlusCore, Cache<?, ?>>() {
                @Override
                public Cache<?, ?> apply(ServerListPlusCore core) {
                    return core.getPlugin().getFaviconCache();
                }
            }, "requests", new Function<ServerListPlusCore, Cache<?, ?>>() {
                @Override
                public Cache<?, ?> apply(ServerListPlusCore core) {
                    return core.getPlugin().getRequestCache();
                }
            });

    public void executeCommand(ServerCommandSender sender, String cmd, String[] args) {
        boolean admin = sender.hasPermission(ADMIN_PERMISSION);

        if (args.length > 0) {
            String sub = Helper.toLowerCase(args[0]);

            if (!SUB_COMMANDS.contains(sub)) {
                if (admin)
                    sender.sendMessage(COMMAND_PREFIX + "Unknown command. Type " + ChatFormat.DARK_GRAY
                            + "/slp help" + ChatFormat.GRAY + " for a list of available commands.");
                else
                    sender.sendMessage(COMMAND_PREFIX + "Unknown command.");
                return;
            }

            if (!admin)
                sender.sendMessage(COMMAND_PREFIX_ERROR + "You do not have permission for this command.");

            else if (sub.equals("reload") || sub.equals("rl")) {
                sender.sendMessage(COMMAND_PREFIX + "Reloading configuration...");

                try { // Reload the configuration
                    this.reload();
                    sender.sendMessage(COMMAND_PREFIX_SUCCESS + "Configuration successfully reloaded!");
                } catch (ServerListPlusException e) {
                    sender.sendMessage(COMMAND_PREFIX_ERROR + "An internal error occurred while reloading the " +
                            "configuration.");
                }
            } else if (sub.equals("save")) {
                sender.sendMessage(COMMAND_PREFIX + "Saving configuration...");

                try { // Save the configuration
                    configManager.save();
                    ((JSONIdentificationStorage) storage).save();
                    sender.sendMessage(COMMAND_PREFIX_SUCCESS + "Configuration successfully saved.");
                } catch (ServerListPlusException e) {
                    sender.sendMessage(COMMAND_PREFIX_ERROR + "An internal error occurred while saving the " +
                            "configuration.");
                }
            } else if (sub.equals("enable") || sub.equals("disable")) {
                boolean enable = sub.equalsIgnoreCase("enable");
                String tmp = enable ? "Enabling" : "Disabling";
                sender.sendMessage(COMMAND_PREFIX + tmp + " ServerListPlus...");

                try { // Enable / disable the ServerListPlus profile
                    if (profileManager.setEnabled(enable)) {
                        sender.sendMessage(COMMAND_PREFIX_SUCCESS + "ServerListPlus has been successfully " + (enable ?
                                "enabled" : "disabled") + '!');
                    } else {
                        enable = profileManager.isEnabled();
                        sender.sendMessage(COMMAND_PREFIX_SUCCESS + "No changes. ServerListPlus is already " + (enable ?
                                "enabled" : "disabled") + '!');
                    }
                } catch (ServerListPlusException e) {
                    sender.sendMessage(COMMAND_PREFIX_ERROR + "An internal error occurred while " +
                            (enable ? "enabling" : "disabling") + " ServerListPlus.");
                }
            } else if (sub.equals("clean")) {
                if (args.length > 1) {
                    String cacheName = Helper.toLowerCase(args[1]);
                    Function<ServerListPlusCore, Cache<?, ?>> cacheType = CACHE_TYPES.get(cacheName);
                    if (cacheType != null) {
                        Cache<?, ?> cache = cacheType.apply(this);
                        if (cache != null) {
                            getLogger().log(INFO, "Cleaning {} cache...", cacheName);
                            cache.invalidateAll();
                            cache.cleanUp();
                            getLogger().log(DEBUG, "Done.");

                            sender.sendMessage(COMMAND_PREFIX_SUCCESS +
                                    "Successfully cleaned up " + cacheName + " cache.");
                        } else
                            sender.sendMessage(COMMAND_PREFIX + "The " + cacheName + " cache is currently " +
                                    "disabled. There is nothing to clean up.");
                    } else
                        sender.sendMessage(COMMAND_PREFIX_ERROR + "Unknown cache type. Type " + ChatFormat.DARK_RED
                                + "/slp help" + ChatFormat.RED + " for more information.");
                } else
                    sender.sendMessage(COMMAND_PREFIX_ERROR + "You need to specify the cache type. Type " +
                            ChatFormat.DARK_RED + "/slp help" + ChatFormat.RED + " for more information.");
            } else if (sub.equals("help")) {
                sender.sendMessages(
                        HELP_HEADER,
                        buildCommandHelp("Display an information page about the plugin."),
                        buildCommandHelp("help", "Show this list of all available commands."),
                        buildCommandHelp("reload", "Reload the plugin configuration."),
                        buildCommandHelp("save", "Save the plugin configuration."),
                        buildCommandHelp("enable", "Enable the plugin and start modifying the status ping."),
                        buildCommandHelp("disable", "Disable the plugin and stop modifying the status ping."),
                        buildCommandHelp("clean", "<favicons/players>", "Delete all entries from the specified " +
                                "cache.")
                );
            }

            return;
        }
        // Send the sender some information about the plugin
        sender.sendMessage(ChatFormat.GOLD + this.getDisplayName());
        if (info.getDescription() != null)
            sender.sendMessage(ChatFormat.GRAY + info.getDescription());
        if (info.getAuthor() != null)
            sender.sendMessage(ChatFormat.GOLD + "Author: " + ChatFormat.GRAY + info.getAuthor());
        if (info.getWebsite() != null)
            sender.sendMessage(ChatFormat.GOLD + "Website: " + ChatFormat.GRAY + info.getWebsite());

        if (admin) {
            if (info.getWiki() != null)
                sender.sendMessage(ChatFormat.GOLD + "Wiki: " + ChatFormat.GRAY + info.getWiki());
            sender.sendMessage(ChatFormat.GREEN + "Type " + ChatFormat.DARK_GREEN + "/slp help" + ChatFormat.GREEN
                    + " for a list of available commands.");
        }
    }

    public List<String> tabComplete(ServerCommandSender sender, String cmd, String[] args) {
        if (!sender.hasPermission(ADMIN_PERMISSION) || args.length > 1) return Collections.emptyList();
        String sub = args.length > 0 ? args[0] : "";
        List<String> result = new ArrayList<>();
        for (String subCmd : SUB_COMMANDS)
            if (Helper.startsWithIgnoreCase(subCmd, sub)) result.add(subCmd);
        return result;
    }

    private static String buildCommandHelp(String description) {
        return buildCommandHelp(null, description);
    }

    private static String buildCommandHelp(String cmd, String description) {
        return buildCommandHelp(cmd, null, description);
    }

    private static String buildCommandHelp(String cmd, String usage, String description) {
        StringBuilder help = new StringBuilder();
        help.append(ChatFormat.RED).append("/slp");
        if (cmd != null) help.append(' ').append(cmd);
        if (usage != null) help.append(' ').append(ChatFormat.GOLD).append(usage);
        return help.append(ChatFormat.WHITE).append(" - ").append(ChatFormat.GRAY).append(description).toString();
    }

    public ConfigurationManager getConf() {
        return configManager;
    }

    public <T> T getConf(Class<T> clazz) {
        return configManager.getStorage().get(clazz);
    }

    public <T> T getDefaultConf(Class<T> clazz) {
        return configManager.getDefaults().get(clazz);
    }

    public ProfileManager getProfiles() {
        return profileManager;
    }

    public StatusManager getStatus() {
        return statusManager;
    }
}
