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

package net.minecrell.serverlistplus.logger;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JulLogger implements SimpleLogger {

    private final java.util.logging.Logger logger;

    public JulLogger(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String message) {
        logger.fine(message);
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.log(Level.FINE, message, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.log(Level.INFO, message, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.log(Level.WARNING, message, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }

}
