/*
 * _____                     __    _     _   _____ _
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

package net.minecrell.serverlistplus.canary;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;

import java.util.logging.Level;

public class Log4j2ServerListPlusLogger extends ServerListPlusLogger {
    private final org.apache.logging.log4j.Logger logger;

    public Log4j2ServerListPlusLogger(ServerListPlusCore core, org.apache.logging.log4j.Logger logger) {
        super(core);
        this.logger = logger;
    }

    private static org.apache.logging.log4j.Level convertLevel(Level level) {
        if (level == ERROR) {
            return org.apache.logging.log4j.Level.ERROR;
        } else if (level == WARN) {
            return org.apache.logging.log4j.Level.WARN;
        } else if (level == INFO) {
            return org.apache.logging.log4j.Level.INFO;
        } else if (level == REPORT) {
            return org.apache.logging.log4j.Level.DEBUG;
        } else {
            return org.apache.logging.log4j.Level.TRACE;
        }
    }

    @Override
    public Log4j2ServerListPlusLogger log(Level level, String message) {
        logger.log(convertLevel(level), LOG_PREFIX + message);
        return this;
    }

    @Override
    public Log4j2ServerListPlusLogger log(Level level, Throwable thrown, String message) {
        logger.log(convertLevel(level), LOG_PREFIX + message, thrown);
        return this;
    }
}
