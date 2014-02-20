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

package net.minecrell.serverlistplus.core.configuration;

import net.minecrell.serverlistplus.api.configuration.util.Registrar;
import net.minecrell.serverlistplus.core.util.Helper;

import com.google.common.collect.ClassToInstanceMap;

public abstract class AbstractRegistrar<T> implements Registrar<T> {
    protected final ClassToInstanceMap<T> registrations = Helper.createLinkedClassMap();

    @Override
    public <V extends T> V get(Class<V> valueClass) {
        return registrations.getInstance(valueClass);
    }

    @Override
    public boolean has(Class<? extends T> valueClass) {
        return registrations.containsKey(valueClass);
    }

    @Override
    public <V extends T> void register(Class<V> valueClass, V value) {
        registrations.putInstance(valueClass, value);
    }

    @Override
    public boolean unregister(Class<? extends T> valueClass) {
        return (registrations.remove(valueClass) != null);
    }
}
