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

/**
 * Represents the plugin configuration manager for the core. It will load the configuration to the memory,
 * store it and save it to the configuration file again if necessary.
 */
public interface ConfigurationManager extends ServerListPlusClass {
    // Default configuration stuff

    /**
     * Returns an array with all currently registered default configuration.
     * @return An array with all registered default configurations.
     */
    Configuration[] getDefault();

    /**
     * Gets the default configuration for a specified configuration class.
     * @param configClass The configuration class.
     * @param <T> The configuration type.
     * @return The registered default configuration, or <code>null</code> if no default configuration for this
     * configuration type is registered.
     */
    <T extends Configuration> T getDefault(Class<T> configClass);

    /**
     * Registers a default configuration for this configuration manager.
     * If a default configuration is already specified for the configuration type,
     * then it will be replaced by the new one.
     * @param configClass The configuration class.
     * @param defaultConfig The default configuration values.
     * @param <T> The configuration type.
     */
    <T extends Configuration> void registerDefault(Class<T> configClass, T defaultConfig);

    /**
     * Unregisters a default configuration from this configuration manager.
     * @param configClass The configuration class.
     * @return Whether the configuration was registered as a default configuration before.
     */
    boolean unregisterDefault(Class<? extends Configuration> configClass);

    // Loaded configuration stuff

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
