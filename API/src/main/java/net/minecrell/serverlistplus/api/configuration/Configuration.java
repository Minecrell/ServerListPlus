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

import net.minecrell.serverlistplus.api.configuration.util.Description;
import net.minecrell.serverlistplus.api.configuration.util.UniqueName;

/**
 * Base class for configuration parts - they have to extend this sub class to be loaded and saved to the file.
 */
public abstract class Configuration extends ConfigurationPart {

    /**
     * Gets the unique name of a configuration part.
     * @param configClass The configuration class.
     * @return The unique name of the specified configuration part.
     */
    public static String getUniqueName(Class<? extends Configuration> configClass) {
        UniqueName name = configClass.getAnnotation(UniqueName.class);
        return (name != null) ? name.value() : null;
    }

    /**
     * Gets the unique name of a configuration part.
     * @param config A configuration instance.
     * @return The unique name of the specified configuration part.
     */
    public static String getUniqueName(Configuration config) {
        return getUniqueName(config.getClass());
    }

    /**
     * Gets the description of a configuration part.
     * @param configClass The configuration class.
     * @return The description of the specified configuration part.
     */
    public static String[] getDescription(Class<? extends Configuration> configClass) {
        Description description = configClass.getAnnotation(Description.class);
        return (description != null) ? description.value() : null;
    }

    /**
     * Gets the description of a configuration part.
     * @param config A configuration instance.
     * @return The description of the specified configuration part.
     */
    public static String[] getDescription(Configuration config) {
        return getDescription(config.getClass());
    }
}
