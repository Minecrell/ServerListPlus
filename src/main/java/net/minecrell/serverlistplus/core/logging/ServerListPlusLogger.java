/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.logging;

import net.minecrell.serverlistplus.core.ServerListPlusException;

public abstract class ServerListPlusLogger extends AbstractLogger<ServerListPlusException> {
    public static final String CORE_PREFIX = "[Core] "; // Prefix used by core log messages

    private final String prefix;

    protected ServerListPlusLogger(String prefix) {
        super(ServerListPlusCoreException.class);
        this.prefix = prefix;
    }

    protected final String prefixMessage(String message) {
        if (this.prefix == null) {
            return message;
        }

        return this.prefix + message;
    }

    @Override
    protected ServerListPlusException createException(String message, Throwable thrown) {
        return new ServerListPlusCoreException(message, thrown);
    }

    private static final class ServerListPlusCoreException extends ServerListPlusException {
        private ServerListPlusCoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
