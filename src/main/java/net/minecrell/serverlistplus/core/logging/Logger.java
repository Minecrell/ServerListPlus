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

public interface Logger<E extends Throwable> {
    enum Level {
        ERROR,
        WARN,
        INFO,
        REPORT,
        DEBUG;
    };

    Logger<E> log(Level level, String message);
    Logger<E> log(Level level, String message, Object arg);
    Logger<E> log(Level level, String message, Object... args);
    //public Logger logf(Level level, String message, Object... args);

    Logger<E> log(Throwable thrown, String message);
    Logger<E> log(Throwable thrown, String message, Object arg);
    Logger<E> log(Throwable thrown, String message, Object... args);
    //public Logger logf(Throwable thrown, String message, Object... args);

    Logger<E> log(Level level, Throwable thrown, String message);
    Logger<E> log(Level level, Throwable thrown, String message, Object arg);
    Logger<E> log(Level level, Throwable thrown, String message, Object... args);
    //public Logger logf(Level level, Throwable thrown, String message, Object... args);

    E process(Throwable thrown, String message);
    E process(Throwable thrown, String message, Object arg);
    E process(Throwable thrown, String message, Object... args);
    //public E processf(Throwable thrown, String message, Object... args);

    E process(Level level, Throwable thrown, String message);
    E process(Level level, Throwable thrown, String message, Object arg);
    E process(Level level, Throwable thrown, String message, Object... args);
    //public E processf(Level level, Throwable thrown, String message, Object... args);
}
