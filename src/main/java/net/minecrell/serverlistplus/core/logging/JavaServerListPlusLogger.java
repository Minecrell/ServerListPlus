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

import net.minecrell.serverlistplus.core.ServerListPlusCore;

import java.util.logging.Level;

public class JavaServerListPlusLogger extends ServerListPlusLogger {
    private final java.util.logging.Logger logger;

    public JavaServerListPlusLogger(ServerListPlusCore core, java.util.logging.Logger logger) {
        super(core);
        this.logger = logger;
    }

    @Override
    public JavaServerListPlusLogger log(Level level, String message) {
        logger.log(level, LOG_PREFIX + message);
        return this;
    }

    @Override
    public JavaServerListPlusLogger log(Level level, Throwable thrown, String message) {
        logger.log(level, LOG_PREFIX + message, thrown);
        return this;
    }
}
