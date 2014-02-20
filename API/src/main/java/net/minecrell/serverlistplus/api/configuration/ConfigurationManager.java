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

package net.minecrell.serverlistplus.api.configuration;

import net.minecrell.serverlistplus.api.ServerListPlusClass;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.configuration.util.Registrar;

/**
 * Represents the plugin configuration manager for the core. It will load the configuration to the memory,
 * store it and save it to the configuration file again if necessary.
 */
public interface ConfigurationManager extends ServerListPlusClass {
    /**
     * Gets a registration manager for the default configuration values if a special configuration does not yet
     * exist in a loaded or saved configuration.
     * @return Registration manager for the default configurations.
     */
    Registrar<Configuration> getDefaults();

    /**
     * Gets a registration manager for the example configurations. Example configuration will be generated in the
     * configuration file with comments - so they won't have any special effect and are just for the help.
     * To get a example generated in the configuration the class has to have a registered default configuration aswell.
     * @return Registration manager for the example configurations.
     * @see #getDefaults()
     */
    Registrar<Configuration> getExamples();

    // Loaded configuration
    /**
     * Resets the stored configurations back to their default values.
     * @return The previously stored configurations, or <code>null</code> if the storage was empty.
     */
    Configuration[] reset();

    /**
     * Returns an array with all currently loaded configurations.
     * @return Array with all loaded configurations.
     */
    Configuration[] get();

    /**
     * Gets a specified stored configuration from this configuration manager.
     * @param configClass The configuration class.
     * @param <T> The configuration type.
     * @return The loaded configuration for this configuration type, or <code>null</code> if it's not loaded.
     */
    <T extends Configuration> T get(Class<T> configClass);

    /**
     * Returns whether the storage contains a configuration of a specified type.
     * @param configClass The configuration class.
     * @param <T> The configuration type.
     * @return If the storage contains a configuration of a specified type.
     */
    boolean has(Class<? extends Configuration> configClass);

    /**
     * Saves a configuration into the internal storage. This will replace the current one if there is already one
     * loaded.
     * @param configClass The configuration class.
     * @param config The config to save.
     * @param <T> The configuration type.
     * @return The previously saved configuration for this configuration type, or <code>null</code> if the storage
     * did not contain a configuration of this type.
     */
    <T extends Configuration> T set(Class<T> configClass, T config);

    /**
     * Reloads the plugin configuration from the configuration file.
     * @return The reloaded configurations, does not contain newly generated ones.
     * @throws ServerListPlusException If an error occurs while reloading the configuration.
     */
    Configuration[] reload() throws ServerListPlusException;

    /**
     * Saves the plugin configuration to the configuration file.
     * This should preferably backup the configuration before saving to prevent data loss.
     * @throws ServerListPlusException If an error occurs while saving the configuration.
     */
    void save() throws ServerListPlusException;
}
