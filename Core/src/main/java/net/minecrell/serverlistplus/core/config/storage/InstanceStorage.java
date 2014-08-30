/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
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

package net.minecrell.serverlistplus.core.config.storage;

import java.util.Collection;

/**
 * Represents a map similar storage that stores an instance of a class together with the class. There can only
 * be one instances of the same type. In the implementation the class is used as a key to map the instances.
 * @param <B> The type all stored instances need to inherit.
 */
public interface InstanceStorage<B> extends Iterable<B> {
    /**
     * Gets a {@link Collection} with all values that are currently saved in this storage.
     * @return Stored values.
     */
    Collection<B> get();

    /**
     * Gets the assigned instance of a class from this storage.
     * @param type The class of the instance.
     * @param <T> The type of the instance.
     * @return The instance if there is no instance with the specified class then <code>null</code> is returned.
     */
    <T extends B> T get(Class<T> type);

    /**
     * Returns whether this storage has an instance assigned to the specified class.
     * @param type The class of the instance.
     * @return <code>true</code> if the storage contains the instance, <code>false</code> if not.
     */
    boolean has(Class<? extends B> type);

    /**
     * Returns whether this storage has an instance assigned to the specified type.
     * @param instance The instance.
     * @return <code>true</code> if the storage contains the instance, <code>false</code> if not.
     */
    boolean has(B instance);

    /**
     * Adds a new instance to the storage and assigns it to its own type.
     * @param instance The actual instance to store in this storage
     * @param <T> The type of the instance.
     * @return The provided instance.
     */
    <T extends B> T set(T instance);

    /**
     * Assigns a new instance to a specified type and save it in this storage.
     * @param type The class of the instance.
     * @param instance The actual instance to store in this storage.
     * @param <T> The type of the instance.
     * @return The provided instance.
     */
    <T extends B> T set(Class<T> type, T instance);

    void setAll(InstanceStorage<B> other);

    /**
     * Removes an instance of a specified class from the storage.
     * @param type The class of the instance.
     * @return The previous instance of the class, or <code>null</code> if the storage didn't contain an instance.
     */
    B remove(Class<? extends B> type);

    /**
     * Gets the count of the saved instances in this storage.
     * @return The instance count.
     */
    int size();

    InstanceStorage<B> withDefaults(InstanceStorage<B> defaults);
}
