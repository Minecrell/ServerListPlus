/*
 *       _____                     _____ _         _____ _
 *      |   __|___ ___ _ _ ___ ___|  _  |_|___ ___|  _  | |_ _ ___
 *      |__   | -_|  _| | | -_|  _|   __| |   | . |   __| | | |_ -|
 *      |_____|___|_|  \_/|___|_| |__|  |_|_|_|_  |__|  |_|___|___|
 *                                            |___|
 *  ServerPingPlus - Customize your server ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverpingplus.core.plugin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * A base class that can be used to wrap the implementation specific command
 * sender classes.
 */
public abstract class AbstractCommandSender<T> implements ServerCommandSender {
    protected final @NonNull T sender;

    public T getHandle() {
        return sender;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || sender.equals(obj);
    }

    @Override
    public int hashCode() {
        return sender.hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
