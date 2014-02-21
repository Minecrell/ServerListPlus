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

/**
 * Represents a class choosing random entries for the server list and processing the configuration.
 */
public interface ServerListDataProvider extends ServerListPlusClass {
    // TODO: Improve documentation

    /**
     * Reloads the processed configuration from the core configuration manager.
     * @throws ServerListPlusException If an error occurs.
     */
    void reload() throws ServerListPlusException;

    /**
     * Returns whether this data provider has a server ping description set.
     * @return Whether the server ping description should be changed.
     */
    boolean hasDescription();

    /**
     * Gets a random description from this data provider for the server ping.
     * @return A random description.
     */
    String getDescription();

    /**
     * Returns whether this data provider has player hovers set.
     * @return Whether the server ping player hover should be changed.
     */
    boolean hasPlayerHover();

    /**
     * Gets a random player hover message from this data provider for the server ping.
     * @return A random player hover message.
     */
    String[] getPlayerHover();
}
