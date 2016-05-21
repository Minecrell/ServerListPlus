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

public interface SimpleLogger extends Logger {

    @Override
    default void debug(String message, Object... args) {
        if (isDebugEnabled()) {
            debug(SimpleMessageFormatter.format(message, args));
        }
    }

    @Override
    default void info(String message, Object... args) {
        if (isInfoEnabled()) {
            info(SimpleMessageFormatter.format(message, args));
        }
    }

    @Override
    default void warn(String message, Object... args) {
        if (isWarnEnabled()) {
            warn(SimpleMessageFormatter.format(message, args));
        }
    }

    @Override
    default void error(String message, Object... args) {
        if (isErrorEnabled()) {
            error(SimpleMessageFormatter.format(message, args));
        }
    }

}
