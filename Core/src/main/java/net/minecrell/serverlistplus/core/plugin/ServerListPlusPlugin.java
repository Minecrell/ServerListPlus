/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
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

package net.minecrell.serverlistplus.core.plugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represents a plugin container running ServerListPlus.
 */
public interface ServerListPlusPlugin {
    /**
     * Gets the plugin logger used for logging errors and messages in the plugin.
     * @return Logger of the plugin.
     */
    Logger getLogger();

    /**
     * Called if the plugin configuration was reloaded, so unneeded services can be disabled.
     */
    void configurationReloaded();

    /**
     * Gets the type of the server implementation running this plugin container.
     * @return The type of the server implementation.
     */
    ServerType getServerType();

    /**
     * Replaces the color codes using <code>&amp;</code> with the correct Minecraft ones.
     * @param s The string to be colorized.
     * @return The colorized string.
     */
    String colorizeString(String s); // TODO: Replace by something more independent?

    /**
     * Gets the folder where the configurations for the plugin are stored.
     * @return The plugin folder for the configurations.
     */
    File getDataFolder();
}
