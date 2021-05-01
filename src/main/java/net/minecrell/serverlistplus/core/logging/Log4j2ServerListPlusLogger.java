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

public class Log4j2ServerListPlusLogger extends ServerListPlusLogger {
    private static final org.apache.logging.log4j.Level[] LEVELS = {
            org.apache.logging.log4j.Level.ERROR,
            org.apache.logging.log4j.Level.WARN,
            org.apache.logging.log4j.Level.INFO,
            org.apache.logging.log4j.Level.DEBUG,
            org.apache.logging.log4j.Level.TRACE,
    };

    private final org.apache.logging.log4j.Logger logger;

    public Log4j2ServerListPlusLogger(org.apache.logging.log4j.Logger logger, String prefix) {
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
