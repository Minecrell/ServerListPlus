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

package net.minecrell.serverlistplus.api.configuration.util;

/**
 * A class providing registrations from a specified type. The registrations are kept using the class and a specified
 * value of the class. It will only take registrations that extend the specified type.
 * @param <T> The registration type this class will accept the registrations from.
 */
public interface Registrar<T> {
    /**
     * Gets all currently registered classes.
     * @return An array containing all currently registered classes.
     */
    T[] get();

    /**
     * Gets the registration of a specified class.
     * @param valueClass The registration class.
     * @param <V> The registration type.
     * @return The registration of the specified class or <code>null</code> if there is no instance registered.
     */
    <V extends T> V get(Class<V> valueClass);

    /**
     * Returns whether there is a instance registered of the specified class.
     * @param valueClass The registration class.
     * @return Whether the class is registered with an instance.
     */
    boolean has(Class<? extends T> valueClass);

    /**
     * Sets the registration for a specified class. This will remove current registration for the class if they are
     * already registrations for the class.
     * @param valueClass The registration class.
     * @param value The registration instance.
     * @param <V> The registration type.
     */
    <V extends T> void register(Class<V> valueClass, V value);

    /**
     * Removes a registration for a specified class.
     * @param valueClass The registration class.
     * @return Whether a registration for the specified class existed.
     */
    boolean unregister(Class<? extends T> valueClass);
}
