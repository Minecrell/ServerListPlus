/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.sponge;

import net.minecrell.serverlistplus.logger.Logger;

final class Slf4jLogger implements Logger {

    private final org.slf4j.Logger logger;

    Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.debug(message, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.info(message, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

}
