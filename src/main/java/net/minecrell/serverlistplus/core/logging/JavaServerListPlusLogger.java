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

public class JavaServerListPlusLogger extends ServerListPlusLogger {
    public static final java.util.logging.Level
            DEBUG = java.util.logging.Level.FINE,
            REPORT = java.util.logging.Level.CONFIG,
            INFO = java.util.logging.Level.INFO,
            WARN = java.util.logging.Level.WARNING,
            ERROR = java.util.logging.Level.SEVERE;

    private static final java.util.logging.Level[] LEVELS = {
            ERROR,
            WARN,
            INFO,
            REPORT,
            DEBUG,
    };

    private final java.util.logging.Logger logger;

    public JavaServerListPlusLogger(java.util.logging.Logger logger, String prefix) {
        super(prefix);
        this.logger = logger;
    }

    @Override
    public Logger<ServerListPlusException> log(Level level, String message) {
        logger.log(LEVELS[level.ordinal()], prefixMessage(message));
        return this;
    }

    @Override
    public Logger<ServerListPlusException> log(Level level, Throwable thrown, String message) {
        logger.log(LEVELS[level.ordinal()], prefixMessage(message), thrown);
        return this;
    }
}
