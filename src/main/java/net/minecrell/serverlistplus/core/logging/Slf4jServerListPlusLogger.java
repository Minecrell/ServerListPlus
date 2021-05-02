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

public class Slf4jServerListPlusLogger extends ServerListPlusLogger {
    private final org.slf4j.Logger logger;

    public Slf4jServerListPlusLogger(org.slf4j.Logger logger, String prefix) {
        super(prefix);
        this.logger = logger;
    }

    @Override
    public Logger<ServerListPlusException> log(Level level, String message) {
        switch (level) {
            case ERROR:
                logger.error(prefixMessage(message));
                break;
            case WARN:
                logger.warn(prefixMessage(message));
                break;
            case INFO:
                logger.info(prefixMessage(message));
                break;
            case REPORT:
                logger.debug(prefixMessage(message));
                break;
            case DEBUG:
                logger.trace(prefixMessage(message));
                break;
        }

        return this;
    }

    @Override
    public Logger<ServerListPlusException> log(Level level, Throwable thrown, String message) {
        switch (level) {
            case ERROR:
                logger.error(prefixMessage(message), thrown);
                break;
            case WARN:
                logger.warn(prefixMessage(message), thrown);
                break;
            case INFO:
                logger.info(prefixMessage(message), thrown);
                break;
            case REPORT:
                logger.debug(prefixMessage(message), thrown);
                break;
            case DEBUG:
                logger.trace(prefixMessage(message), thrown);
                break;
        }

        return this;
    }
}
