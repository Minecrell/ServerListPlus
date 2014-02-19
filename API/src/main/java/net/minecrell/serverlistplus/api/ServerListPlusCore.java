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

package net.minecrell.serverlistplus.api;

import java.net.InetAddress;

import net.minecrell.serverlistplus.api.configuration.ConfigurationManager;
import net.minecrell.serverlistplus.api.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;

/**
 * Representing the core part of the ServerListPlus plugin.
 * It will handle all server ping requests and is equal in all different implementations of the plugin.
 */
public interface ServerListPlusCore {
    /**
     * Gets the name of this plugin core.
     * @return The plugin core's name.
     */
    String getName();

    /**
     * Gets the version of this plugin core.
     * @return The plugin core version.
     */
    String getVersion();

    /**
     * Gets the instance of the plugin container running the ServerListPlus plugin.
     * @return Instance of the plugin container.
     * @see ServerListPlusPlugin
     */
    ServerListPlusPlugin getPlugin();

    /**
     * Gets a dedicated logger used for logging errors and messages in the core.
     * @return The dedicated core logger.
     */
    ServerListPlusLogger getLogger();

    /**
     * Gets the configuration manager storing the loaded configuration for this core.
     * @return The configuration manager of this core.
     */
    ConfigurationManager getConfigManager();

    /**
     * Reloads the plugin configuration and disables unnecessary services.
     * @throws ServerListPlusException If the configuration file could not be successfully read.
     */
    void reload() throws ServerListPlusException;

    /**
     * Handles an server ping request. This will modify the response with the changed data in the plugin and return the
     * modified response.
     *
     * @param client The IP address of the client. (Used for player tracking)
     * @param response An instance of the server ping wrapper to modify the response.
     * @see ServerPingResponse
     */
    void processRequest(InetAddress client, ServerPingResponse response);

    /**
     * Handles an server ping request with an specific hostname used to ping the server. This will modify the response
     * with the changed data in the plugin and return the modified response.
     *
     * @param client The IP address of the client. (Used for player tracking)
     * @param host The hostname used by the client to ping the server.
     * @param response An instance of the server ping wrapper to modify the response.
     * @see ServerPingResponse
     */
    void processRequest(InetAddress client, String host, ServerPingResponse response);

    /**
     * Handles the login of a player to the server. This is used by the player tracking to track the players with
     * their IP address, so it can be used by the {@link #processRequest(InetAddress, ServerPingResponse)} method to
     * create personalized messages.
     *
     * @param playerName The name the player used to login to the server.
     * @param client The IP address of the client.
     */
    void processLogin(String playerName, InetAddress client);

    /**
     * Handles a command send by a player or the console.
     *
     * @param sender The sender of the command, usually a player or the console.
     * @param cmd The command executed by the player.
     * @param label The command the player has used - if the command has any aliases.
     * @param args The arguments the player has used to perform the command.
     * @see ServerCommandSender
     */
    void processCommand(ServerCommandSender sender, String cmd, String label, String[] args);
}
