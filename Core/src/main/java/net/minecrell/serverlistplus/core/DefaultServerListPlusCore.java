/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core;

import lombok.Getter;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.ServerListPlusLogger;
import net.minecrell.serverlistplus.api.ServerPingResponse;
import net.minecrell.serverlistplus.api.configuration.ConfigurationManager;
import net.minecrell.serverlistplus.api.configuration.PluginConfiguration;
import net.minecrell.serverlistplus.api.configuration.ServerListConfiguration;
import net.minecrell.serverlistplus.api.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.configuration.ConfigurationExamples;
import net.minecrell.serverlistplus.core.configuration.CoreConfiguration;
import net.minecrell.serverlistplus.core.configuration.CoreConfigurationManager;
import net.minecrell.serverlistplus.core.configuration.util.IOUtil;
import net.minecrell.serverlistplus.core.util.Helper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DefaultServerListPlusCore implements ServerListPlusCore {
    private static final Pattern PLAYER_PATTERN = Pattern.compile("%player%", Pattern.LITERAL);

    private final @Getter ServerListPlusPlugin plugin;
    private final @Getter ServerListPlusLogger logger;

    private final @Getter ConfigurationManager configManager;
    private final @Getter CoreServerListDataProvider dataProvider = new CoreServerListDataProvider(this);

    private final Cache<String, String> playerTracker;

    private static final String INFO_COMMAND_FILENAME = "INFO";
    private final String[] infoCommand;

    public DefaultServerListPlusCore(ServerListPlusPlugin plugin) {
        this.plugin = plugin;
        this.logger = new CoreServerListPlusLogger(this);

        this.getLogger().info("Initializing...");

        this.infoCommand = this.loadInfoCommandLines();
        this.getLogger().info("Loading configuration...");
        this.configManager = new CoreConfigurationManager(this);

        // Register default configurations
        configManager.getDefaults().register(ServerListConfiguration.class, new ServerListConfiguration());
        configManager.getExamples().register(ServerListConfiguration.class,
                ConfigurationExamples.getServerListExample());

        configManager.getDefaults().register(PluginConfiguration.class, new PluginConfiguration());
        configManager.getDefaults().register(CoreConfiguration.class, new CoreConfiguration());

        this.reload(); // Load configuration

        Cache<String, String> playerTracker;
        try {
            playerTracker = CacheBuilder.from(configManager.get(CoreConfiguration.class).Caches.PlayerTracking).build();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, e, "Unable to parse player tracker configuration from the " +
                    "configuration!");
            playerTracker = CacheBuilder.from(new CoreConfiguration().Caches.PlayerTracking).build();
        }

        this.playerTracker = playerTracker;
        this.getLogger().info(this.getName() + " has been successfully initialized.");
    }

    @Override
    public String getName() {
        return this.getClass().getPackage().getImplementationTitle();
    }

    @Override
    public String getVersion() {
        return this.getClass().getPackage().getImplementationVersion();
    }

    @Override
    public InputStream getResource(String fileName) {
        return this.getClass().getClassLoader().getResourceAsStream(fileName);
    }

    @Override
    public void reload() throws ServerListPlusException {
        configManager.reload();
        dataProvider.reload();
        plugin.configurationReloaded();
    }

    @Override
    public void processRequest(InetAddress client, ServerPingResponse response, ServerPingResponse.Modify...
            modifications) {
        this.processRequest(client, null, response, modifications);
    }

    @Override
    public void processRequest(InetAddress client, String host, ServerPingResponse response, ServerPingResponse
            .Modify... modifications) {
        EnumSet<ServerPingResponse.Modify> modify = Helper.getModifications(modifications);

        if (modify.contains(ServerPingResponse.Modify.DESCRIPTION)
                && dataProvider.hasDescription()) {
            // TODO: Player personalization
            response.setDescription(dataProvider.getDescription());
        }

        if (modify.contains(ServerPingResponse.Modify.PLAYER_HOVER)
                && dataProvider.hasPlayerHover()) {
            PluginConfiguration config = this.getConfigManager().get(PluginConfiguration.class);
            String[] playerHover = dataProvider.getPlayerHover();

            // TODO: Improve performance
            if (config.PlayerTracking) {
                playerHover = playerHover.clone(); // :(
                for (int i = 0; i < playerHover.length; i++) {
                    playerHover[i] = PLAYER_PATTERN.matcher(playerHover[i]).replaceAll(dataProvider
                            .getUnknownPlayerReplacement());
                }
            }

            response.setPlayerHover(playerHover);
        }
    }


    @Override
    public void processLogin(String playerName, InetAddress client) {
        playerTracker.put(client.getHostAddress(), playerName);
    }

    @Override // TODO: Implementation independent colored messages
    public void processCommand(ServerCommandSender sender, String cmd, String label, String[] args) {
        String subCommand = (args.length > 0) ? args[0] : null;
        if (subCommand != null) {
            if (subCommand.equalsIgnoreCase("reload")) {
                this.getLogger().info("Reloading configuration per request by '" + sender + "'!");

                try {
                    this.reload();
                    this.sendColoredMessage(sender, "&aConfiguration successfully reloaded!");
                } catch (ServerListPlusException e) {
                    this.sendColoredMessage(sender,"&cAn internal error occurred while reloading the configuration.");
                } return;
            } else if (subCommand.equalsIgnoreCase("save")) {
                this.getLogger().info("Saving configuration per request by '" + sender + "'!");

                try {
                    this.getConfigManager().save();
                    this.sendColoredMessage(sender, "&aConfiguration successfully saved!");
                } catch (ServerListPlusException e) {
                    this.sendColoredMessage(sender, "&cAn internal error occurred while saving the configuration.");
                } return;
            }
        }

        // Send info command to the command sender
        for (String line : infoCommand) sender.sendMessage(line);
    }

    private void sendColoredMessage(ServerCommandSender sender, String... messages) {
        for (String message : messages) sender.sendMessage(this.getPlugin().colorizeString(message));
    }

    private String[] loadInfoCommandLines() {
        try (InputStream in = this.getResource(INFO_COMMAND_FILENAME)) {
            return Helper.toStringArray(Helper.colorize(this.getPlugin(), IOUtil.readLines(in)));
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, e, "Unable to load info command!");
            return new String[0]; // Return empty String array
        }
    }
}
